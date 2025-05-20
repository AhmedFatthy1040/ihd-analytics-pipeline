-- Add missing columns to dim_user table to match entity class
SET search_path TO ihd_analytics;

ALTER TABLE dim_user ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE dim_user ADD COLUMN IF NOT EXISTS location VARCHAR(255);
ALTER TABLE dim_user ADD COLUMN IF NOT EXISTS url VARCHAR(255);
ALTER TABLE dim_user ADD COLUMN IF NOT EXISTS protected BOOLEAN;
ALTER TABLE dim_user ADD COLUMN IF NOT EXISTS verified BOOLEAN;
ALTER TABLE dim_user ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(255);
ALTER TABLE dim_user ADD COLUMN IF NOT EXISTS language VARCHAR(10);
ALTER TABLE dim_user ADD COLUMN IF NOT EXISTS email VARCHAR(100);
ALTER TABLE dim_user ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20);
