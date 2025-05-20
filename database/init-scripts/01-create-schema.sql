-- PostgreSQL initialization script for IHD Analytics Pipeline
-- This script creates the star schema for feedback analysis

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";  -- For UUID generation
CREATE EXTENSION IF NOT EXISTS pg_trgm;      -- For text search

-- Create schema
CREATE SCHEMA IF NOT EXISTS ihd_analytics;
SET search_path TO ihd_analytics;

-- Create sequences
CREATE SEQUENCE seq_feedback_id START 1;

-- Create dimension tables
-- 1. Time dimension
CREATE TABLE dim_time (
    time_id SERIAL PRIMARY KEY,
    full_date DATE NOT NULL,
    year INT NOT NULL,
    quarter INT NOT NULL,
    month INT NOT NULL,
    month_name VARCHAR(10) NOT NULL,
    day INT NOT NULL,
    day_of_week INT NOT NULL,
    day_name VARCHAR(10) NOT NULL,
    week_of_year INT NOT NULL,
    is_weekend BOOLEAN NOT NULL,
    is_holiday BOOLEAN NOT NULL,
    CONSTRAINT uk_dim_time_full_date UNIQUE (full_date)
);

-- 2. User dimension
CREATE TABLE dim_user (
    user_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    created_at TIMESTAMP,
    followers_count INT,
    following_count INT,
    tweet_count INT,
    listed_count INT
);
CREATE INDEX idx_dim_user_username ON dim_user(username);

-- 3. Location dimension
CREATE TABLE dim_location (
    location_id SERIAL PRIMARY KEY,
    location_string VARCHAR(255),
    country VARCHAR(100),
    city VARCHAR(100),
    region VARCHAR(100)
);
CREATE INDEX idx_dim_location_country ON dim_location(country);
CREATE INDEX idx_dim_location_city ON dim_location(city);

-- 4. Issue dimension
CREATE TABLE dim_issue (
    issue_id INT PRIMARY KEY,
    issue_class_key INT NOT NULL,
    issue_class_code VARCHAR(100) NOT NULL
);
CREATE INDEX idx_dim_issue_class_key ON dim_issue(issue_class_key);
CREATE INDEX idx_dim_issue_class_code ON dim_issue(issue_class_code);

-- 5. Agency dimension
CREATE TABLE dim_agency (
    agency_id SERIAL PRIMARY KEY,
    agency_name VARCHAR(100) NOT NULL,
    agency_account VARCHAR(100) NOT NULL,
    sector VARCHAR(100),
    department VARCHAR(100),
    CONSTRAINT uk_dim_agency_account UNIQUE (agency_account)
);
CREATE INDEX idx_dim_agency_name ON dim_agency(agency_name);
CREATE INDEX idx_dim_agency_sector ON dim_agency(sector);

-- 6. Hashtag dimension
CREATE TABLE dim_hashtag (
    hashtag_id SERIAL PRIMARY KEY,
    hashtag_text VARCHAR(100) NOT NULL,
    CONSTRAINT uk_dim_hashtag_text UNIQUE (hashtag_text)
);
CREATE INDEX idx_dim_hashtag_text ON dim_hashtag(hashtag_text);

-- Create fact table with partitioning
CREATE TABLE fact_feedback (
    feedback_id BIGINT DEFAULT nextval('seq_feedback_id'),
    tweet_id VARCHAR(50) NOT NULL,
    time_id INT NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    location_id INT,
    issue_id INT,
    platform VARCHAR(50) NOT NULL,
    text TEXT,
    language VARCHAR(10),
    retweet_count INT DEFAULT 0,
    reply_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    quote_count INT DEFAULT 0,
    bookmark_count INT DEFAULT 0,
    impression_count INT DEFAULT 0,
    created_date DATE NOT NULL,
    
    PRIMARY KEY (feedback_id, created_date),
    UNIQUE (tweet_id, created_date),
    FOREIGN KEY (time_id) REFERENCES dim_time(time_id),
    FOREIGN KEY (user_id) REFERENCES dim_user(user_id),
    FOREIGN KEY (location_id) REFERENCES dim_location(location_id),
    FOREIGN KEY (issue_id) REFERENCES dim_issue(issue_id)
) PARTITION BY RANGE (created_date);

-- Create partitions for each month of 2024
CREATE TABLE fact_feedback_y2024m01 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
CREATE TABLE fact_feedback_y2024m02 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');
CREATE TABLE fact_feedback_y2024m03 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-03-01') TO ('2024-04-01');
CREATE TABLE fact_feedback_y2024m04 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-04-01') TO ('2024-05-01');
CREATE TABLE fact_feedback_y2024m05 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-05-01') TO ('2024-06-01');
CREATE TABLE fact_feedback_y2024m06 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-06-01') TO ('2024-07-01');
CREATE TABLE fact_feedback_y2024m07 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-07-01') TO ('2024-08-01');
CREATE TABLE fact_feedback_y2024m08 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-08-01') TO ('2024-09-01');
CREATE TABLE fact_feedback_y2024m09 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-09-01') TO ('2024-10-01');
CREATE TABLE fact_feedback_y2024m10 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-10-01') TO ('2024-11-01');
CREATE TABLE fact_feedback_y2024m11 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-11-01') TO ('2024-12-01');
CREATE TABLE fact_feedback_y2024m12 PARTITION OF fact_feedback
    FOR VALUES FROM ('2024-12-01') TO ('2025-01-01');

-- Create partitions for each month of 2025
CREATE TABLE fact_feedback_y2025m01 PARTITION OF fact_feedback
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');
CREATE TABLE fact_feedback_y2025m02 PARTITION OF fact_feedback
    FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');
CREATE TABLE fact_feedback_y2025m03 PARTITION OF fact_feedback
    FOR VALUES FROM ('2025-03-01') TO ('2025-04-01');
CREATE TABLE fact_feedback_y2025m04 PARTITION OF fact_feedback
    FOR VALUES FROM ('2025-04-01') TO ('2025-05-01');
CREATE TABLE fact_feedback_y2025m05 PARTITION OF fact_feedback
    FOR VALUES FROM ('2025-05-01') TO ('2025-06-01');
CREATE TABLE fact_feedback_y2025m06 PARTITION OF fact_feedback
    FOR VALUES FROM ('2025-06-01') TO ('2025-07-01');

-- Create bridge tables
-- 1. Bridge table for feedback-hashtag relationship
CREATE TABLE bridge_feedback_hashtag (
    feedback_id BIGINT NOT NULL,
    hashtag_id INT NOT NULL,
    created_date DATE NOT NULL,
    PRIMARY KEY (feedback_id, hashtag_id),
    FOREIGN KEY (feedback_id, created_date) REFERENCES fact_feedback(feedback_id, created_date) ON DELETE CASCADE,
    FOREIGN KEY (hashtag_id) REFERENCES dim_hashtag(hashtag_id)
);
CREATE INDEX idx_bridge_feedback_hashtag_feedback_id ON bridge_feedback_hashtag(feedback_id);
CREATE INDEX idx_bridge_feedback_hashtag_hashtag_id ON bridge_feedback_hashtag(hashtag_id);

-- 2. Bridge table for feedback-agency relationship
CREATE TABLE bridge_feedback_agency (
    feedback_id BIGINT NOT NULL,
    agency_id INT NOT NULL,
    created_date DATE NOT NULL,
    PRIMARY KEY (feedback_id, agency_id),
    FOREIGN KEY (feedback_id, created_date) REFERENCES fact_feedback(feedback_id, created_date) ON DELETE CASCADE,
    FOREIGN KEY (agency_id) REFERENCES dim_agency(agency_id)
);
CREATE INDEX idx_bridge_feedback_agency_feedback_id ON bridge_feedback_agency(feedback_id);
CREATE INDEX idx_bridge_feedback_agency_agency_id ON bridge_feedback_agency(agency_id);

-- Create additional indexes on fact table
CREATE INDEX idx_fact_feedback_time_id ON fact_feedback(time_id);
CREATE INDEX idx_fact_feedback_user_id ON fact_feedback(user_id);
CREATE INDEX idx_fact_feedback_issue_id ON fact_feedback(issue_id);
CREATE INDEX idx_fact_feedback_language ON fact_feedback(language);
CREATE INDEX idx_fact_feedback_created_date ON fact_feedback(created_date);

-- Create materialized views for common analytics queries
-- 1. Daily feedback metrics
CREATE MATERIALIZED VIEW mv_daily_feedback_count AS
SELECT 
    dt.full_date,
    COUNT(*) as feedback_count,
    SUM(ff.retweet_count) as total_retweets,
    SUM(ff.reply_count) as total_replies,
    SUM(ff.like_count) as total_likes,
    SUM(ff.quote_count) as total_quotes,
    SUM(ff.bookmark_count) as total_bookmarks,
    SUM(ff.impression_count) as total_impressions,
    SUM(ff.retweet_count + ff.reply_count + ff.like_count) as total_interactions
FROM 
    fact_feedback ff
JOIN 
    dim_time dt ON ff.time_id = dt.time_id
GROUP BY 
    dt.full_date;

CREATE UNIQUE INDEX idx_mv_daily_feedback_count_date ON mv_daily_feedback_count(full_date);

-- 2. Issue class distribution
CREATE MATERIALIZED VIEW mv_issue_distribution AS
SELECT 
    di.issue_class_code,
    COUNT(*) as feedback_count,
    SUM(ff.retweet_count + ff.reply_count + ff.like_count) as total_interactions
FROM 
    fact_feedback ff
JOIN 
    dim_issue di ON ff.issue_id = di.issue_id
GROUP BY 
    di.issue_class_code;

CREATE UNIQUE INDEX idx_mv_issue_distribution_code ON mv_issue_distribution(issue_class_code);

-- 3. Agency performance metrics
CREATE MATERIALIZED VIEW mv_agency_performance AS
SELECT 
    da.agency_name,
    da.sector,
    da.department,
    COUNT(DISTINCT bfa.feedback_id) as mention_count,
    SUM(ff.retweet_count) as total_retweets,
    SUM(ff.reply_count) as total_replies,
    SUM(ff.like_count) as total_likes,
    SUM(ff.retweet_count + ff.reply_count + ff.like_count) as total_interactions
FROM 
    bridge_feedback_agency bfa
JOIN 
    dim_agency da ON bfa.agency_id = da.agency_id
JOIN 
    fact_feedback ff ON bfa.feedback_id = ff.feedback_id
GROUP BY 
    da.agency_name, da.sector, da.department;

CREATE UNIQUE INDEX idx_mv_agency_performance_name ON mv_agency_performance(agency_name);

-- 4. Top hashtags
CREATE MATERIALIZED VIEW mv_top_hashtags AS
SELECT 
    dh.hashtag_text,
    COUNT(DISTINCT bfh.feedback_id) as usage_count,
    SUM(ff.retweet_count + ff.reply_count + ff.like_count) as total_interactions
FROM 
    bridge_feedback_hashtag bfh
JOIN 
    dim_hashtag dh ON bfh.hashtag_id = dh.hashtag_id
JOIN 
    fact_feedback ff ON bfh.feedback_id = ff.feedback_id
GROUP BY 
    dh.hashtag_text;

CREATE UNIQUE INDEX idx_mv_top_hashtags_text ON mv_top_hashtags(hashtag_text);

-- Function to populate time dimension table
CREATE OR REPLACE FUNCTION populate_dim_time(start_date DATE, end_date DATE)
RETURNS VOID AS $$
DECLARE
    curr_date DATE;
BEGIN
    curr_date := start_date;
    
    WHILE curr_date <= end_date LOOP
        INSERT INTO dim_time (
            full_date, 
            year, 
            quarter, 
            month,
            month_name,
            day, 
            day_of_week, 
            day_name,
            week_of_year, 
            is_weekend, 
            is_holiday
        )
        VALUES (
            curr_date,
            EXTRACT(YEAR FROM curr_date),
            EXTRACT(QUARTER FROM curr_date),
            EXTRACT(MONTH FROM curr_date),
            TO_CHAR(curr_date, 'Month'),
            EXTRACT(DAY FROM curr_date),
            EXTRACT(DOW FROM curr_date),
            TO_CHAR(curr_date, 'Day'),
            EXTRACT(WEEK FROM curr_date),
            CASE WHEN EXTRACT(DOW FROM curr_date) IN (0, 6) THEN true ELSE false END,
            false  -- Default is_holiday to false, can be updated later
        )
        ON CONFLICT (full_date) DO NOTHING;
        
        curr_date := curr_date + 1;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Populate time dimension for 2024-2025
SELECT populate_dim_time('2024-01-01', '2025-12-31');

-- Insert initial agency data
INSERT INTO dim_agency (agency_name, agency_account, sector, department) VALUES
('Agency 1', '@Account1', 'Government', 'Customer Service'),
('Agency 2', '@Account2', 'Government', 'Public Relations'),
('Agency 3', '@Account3', 'Government', 'Technical Support'),
('Agency 4', '@Account4', 'Government', 'Information Technology'),
('Agency 5', '@Account5', 'Government', 'Public Works');

-- Create a function to refresh all materialized views
CREATE OR REPLACE FUNCTION refresh_all_materialized_views()
RETURNS VOID AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_daily_feedback_count;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_issue_distribution;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_agency_performance;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_top_hashtags;
END;
$$ LANGUAGE plpgsql;

-- Note: In a production environment, you would use pg_cron extension to schedule regular refreshes
-- This requires additional setup and is not included in the standard PostgreSQL image
-- For this project, we'll refresh the materialized views via the Spring Boot application

-- Grant privileges
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ihd_analytics TO ihd_user;
