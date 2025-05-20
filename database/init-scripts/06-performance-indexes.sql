-- Add performance indexes for batch processing
SET search_path TO ihd_analytics;

-- Add index on tweet_id for faster duplicate checks
CREATE INDEX IF NOT EXISTS idx_fact_feedback_tweet_id ON fact_feedback(tweet_id);

-- Add combined index for tweet lookup with date
CREATE INDEX IF NOT EXISTS idx_fact_feedback_tweet_date ON fact_feedback(tweet_id, created_date);

-- Add index on user_id in dim_user for faster lookups
CREATE INDEX IF NOT EXISTS idx_dim_user_id ON dim_user(user_id); 

-- Add index for hashtag lookup
CREATE INDEX IF NOT EXISTS idx_bridge_feedback_hashtag_id ON bridge_feedback_hashtag(hashtag_id);

-- Add index for agency lookup  
CREATE INDEX IF NOT EXISTS idx_bridge_feedback_agency_id ON bridge_feedback_agency(agency_id);

-- Analyze tables for better query planning
ANALYZE dim_user;
ANALYZE dim_location;
ANALYZE dim_hashtag;
ANALYZE dim_agency;
ANALYZE fact_feedback;
ANALYZE bridge_feedback_hashtag;
ANALYZE bridge_feedback_agency;
