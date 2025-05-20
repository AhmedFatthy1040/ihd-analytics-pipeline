-- PostgreSQL functions for processing JSON feedback data
-- This script creates functions for handling data ingestion

SET search_path TO ihd_analytics;

-- Function to process hashtags from a JSON array
CREATE OR REPLACE FUNCTION process_hashtags(
    p_feedback_id BIGINT,
    p_hashtags JSONB,
    p_created_date DATE
) RETURNS VOID AS $$
DECLARE
    v_hashtag TEXT;
    v_hashtag_id INT;
BEGIN
    IF p_hashtags IS NULL OR jsonb_array_length(p_hashtags) = 0 THEN
        RETURN;
    END IF;
    
    FOR i IN 0..jsonb_array_length(p_hashtags)-1 LOOP
        v_hashtag := p_hashtags->i;
        
        -- Insert hashtag if it doesn't exist and get ID
        INSERT INTO dim_hashtag (hashtag_text)
        VALUES (v_hashtag)
        ON CONFLICT (hashtag_text) DO NOTHING;
        
        SELECT hashtag_id INTO v_hashtag_id
        FROM dim_hashtag
        WHERE hashtag_text = v_hashtag;
        
        -- Create bridge record
        INSERT INTO bridge_feedback_hashtag (feedback_id, hashtag_id, created_date)
        VALUES (p_feedback_id, v_hashtag_id, p_created_date)
        ON CONFLICT (feedback_id, hashtag_id) DO NOTHING;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Function to process mentions (agencies) from a JSON array
CREATE OR REPLACE FUNCTION process_mentions(
    p_feedback_id BIGINT,
    p_mentions JSONB,
    p_created_date DATE
) RETURNS VOID AS $$
DECLARE
    v_mention TEXT;
    v_agency_id INT;
    v_clean_mention TEXT;
BEGIN
    IF p_mentions IS NULL OR jsonb_array_length(p_mentions) = 0 THEN
        RETURN;
    END IF;
    
    FOR i IN 0..jsonb_array_length(p_mentions)-1 LOOP
        v_mention := p_mentions->i;
        
        -- Clean the mention (remove quotes if present)
        v_clean_mention := REPLACE(REPLACE(v_mention, '"', ''), ' ', '');
        
        -- Get agency ID
        SELECT agency_id INTO v_agency_id
        FROM dim_agency
        WHERE REPLACE(REPLACE(agency_account, '"', ''), ' ', '') = v_clean_mention;
        
        -- If agency doesn't exist, create with default values
        IF v_agency_id IS NULL THEN
            INSERT INTO dim_agency (agency_name, agency_account, sector, department)
            VALUES (
                'Unknown Agency', 
                v_clean_mention, 
                'Unclassified', 
                'Unclassified'
            )
            RETURNING agency_id INTO v_agency_id;
        END IF;
        
        -- Create bridge record
        INSERT INTO bridge_feedback_agency (feedback_id, agency_id, created_date)
        VALUES (p_feedback_id, v_agency_id, p_created_date)
        ON CONFLICT (feedback_id, agency_id) DO NOTHING;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Function to process location string and extract country, city, region
CREATE OR REPLACE FUNCTION process_location(
    p_location_string TEXT
) RETURNS INT AS $$
DECLARE
    v_location_id INT;
    v_country TEXT := NULL;
    v_city TEXT := NULL;
    v_region TEXT := NULL;
BEGIN
    IF p_location_string IS NULL OR p_location_string = '' THEN
        RETURN NULL;
    END IF;
    
    -- Simple parsing for location string (format: "City, Country" or just "Country")
    IF position(',' IN p_location_string) > 0 THEN
        v_city := trim(split_part(p_location_string, ',', 1));
        v_country := trim(split_part(p_location_string, ',', 2));
    ELSE
        v_country := trim(p_location_string);
    END IF;
    
    -- Insert location if it doesn't exist
    INSERT INTO dim_location (location_string, country, city, region)
    VALUES (p_location_string, v_country, v_city, v_region)
    ON CONFLICT (location_id) DO NOTHING
    RETURNING location_id INTO v_location_id;
    
    -- If insert didn't return ID, select it
    IF v_location_id IS NULL THEN
        SELECT location_id INTO v_location_id
        FROM dim_location
        WHERE location_string = p_location_string;
    END IF;
    
    RETURN v_location_id;
END;
$$ LANGUAGE plpgsql;

-- Function to get time_id from a timestamp
CREATE OR REPLACE FUNCTION get_time_id(
    p_timestamp TIMESTAMP
) RETURNS INT AS $$
DECLARE
    v_time_id INT;
    v_date DATE;
BEGIN
    v_date := date_trunc('day', p_timestamp)::DATE;
    
    SELECT time_id INTO v_time_id
    FROM dim_time
    WHERE full_date = v_date;
    
    IF v_time_id IS NULL THEN
        -- If date doesn't exist in dim_time, add it
        PERFORM populate_dim_time(v_date, v_date);
        
        SELECT time_id INTO v_time_id
        FROM dim_time
        WHERE full_date = v_date;
    END IF;
    
    RETURN v_time_id;
END;
$$ LANGUAGE plpgsql;

-- Main function to process a single feedback JSON record
CREATE OR REPLACE FUNCTION process_feedback_json(
    p_feedback JSONB
) RETURNS BIGINT AS $$
DECLARE
    v_feedback_id BIGINT;
    v_tweet_id TEXT;
    v_time_id INT;
    v_user_id TEXT;
    v_location_id INT;
    v_issue_id INT;
    v_created_at TIMESTAMP;
    v_created_date DATE;
BEGIN
    -- Extract tweet ID
    v_tweet_id := p_feedback->>'tweet_id';
    
    -- Check if feedback already exists
    SELECT feedback_id INTO v_feedback_id
    FROM fact_feedback
    WHERE tweet_id = v_tweet_id;
    
    IF v_feedback_id IS NOT NULL THEN
        -- Feedback already processed
        RETURN v_feedback_id;
    END IF;
    
    -- Process created_at timestamp
    v_created_at := (p_feedback->>'created_at')::TIMESTAMP;
    v_created_date := date_trunc('day', v_created_at)::DATE;
    v_time_id := get_time_id(v_created_at);
    
    -- Process user
    v_user_id := p_feedback->'user'->>'user_id';
    
    -- Insert user if not exists
    INSERT INTO dim_user (
        user_id, 
        username, 
        created_at, 
        followers_count, 
        following_count, 
        tweet_count, 
        listed_count
    )
    VALUES (
        v_user_id,
        p_feedback->'user'->>'username',
        (p_feedback->'user'->>'created_at')::TIMESTAMP,
        (p_feedback->'user'->>'followers_count')::INT,
        (p_feedback->'user'->>'following_count')::INT,
        (p_feedback->'user'->>'tweet_count')::INT,
        (p_feedback->'user'->>'listed_count')::INT
    )
    ON CONFLICT (user_id) DO UPDATE SET
        followers_count = EXCLUDED.followers_count,
        following_count = EXCLUDED.following_count,
        tweet_count = EXCLUDED.tweet_count,
        listed_count = EXCLUDED.listed_count;
    
    -- Process location
    v_location_id := process_location(p_feedback->'user'->>'location_string');
    
    -- Process issue
    v_issue_id := (p_feedback->'issue'->>'issue_id')::INT;
    
    -- Insert issue if not exists
    INSERT INTO dim_issue (
        issue_id,
        issue_class_key,
        issue_class_code
    )
    VALUES (
        v_issue_id,
        (p_feedback->'issue'->'issue_class'->>'issue_class_key')::INT,
        p_feedback->'issue'->'issue_class'->>'issue_class_code'
    )
    ON CONFLICT (issue_id) DO NOTHING;
    
    -- Insert feedback record
    INSERT INTO fact_feedback (
        tweet_id,
        time_id,
        user_id,
        location_id,
        issue_id,
        platform,
        text,
        language,
        retweet_count,
        reply_count,
        like_count,
        quote_count,
        bookmark_count,
        impression_count,
        created_date
    )
    VALUES (
        v_tweet_id,
        v_time_id,
        v_user_id,
        v_location_id,
        v_issue_id,
        p_feedback->>'platform',
        p_feedback->>'text',
        p_feedback->>'language',
        (p_feedback->'metrics'->>'retweet_count')::INT,
        (p_feedback->'metrics'->>'reply_count')::INT,
        (p_feedback->'metrics'->>'like_count')::INT,
        (p_feedback->'metrics'->>'quote_count')::INT,
        (p_feedback->'metrics'->>'bookmark_count')::INT,
        (p_feedback->'metrics'->>'impression_count')::INT,
        v_created_date
    )
    RETURNING feedback_id INTO v_feedback_id;
    
    -- Process hashtags
    PERFORM process_hashtags(v_feedback_id, p_feedback->'hashtags', v_created_date);
    
    -- Process mentions
    PERFORM process_mentions(v_feedback_id, p_feedback->'mentions', v_created_date);
    
    RETURN v_feedback_id;
END;
$$ LANGUAGE plpgsql;

-- Function to process an entire JSON array of feedback records
CREATE OR REPLACE FUNCTION process_feedback_json_array(
    p_feedback_array JSONB
) RETURNS INT AS $$
DECLARE
    v_count INT := 0;
BEGIN
    IF p_feedback_array IS NULL OR jsonb_typeof(p_feedback_array) != 'array' THEN
        RETURN 0;
    END IF;
    
    FOR i IN 0..jsonb_array_length(p_feedback_array)-1 LOOP
        PERFORM process_feedback_json(p_feedback_array->i);
        v_count := v_count + 1;
    END LOOP;
    
    RETURN v_count;
END;
$$ LANGUAGE plpgsql;
