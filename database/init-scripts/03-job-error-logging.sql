-- Add job error logging table
CREATE TABLE IF NOT EXISTS ihd_analytics.job_error_log (
    id SERIAL PRIMARY KEY,
    job_id VARCHAR(100) NOT NULL,
    error_message TEXT,
    stack_trace TEXT,
    item_data TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_job_error_log_job_id ON ihd_analytics.job_error_log(job_id);
CREATE INDEX idx_job_error_log_created_at ON ihd_analytics.job_error_log(created_at);
