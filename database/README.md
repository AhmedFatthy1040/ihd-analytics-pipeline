# IHD Analytics Pipeline - Database Setup

This directory contains the PostgreSQL database setup for the IHD Analytics Pipeline project.

## Database Structure

The database follows a star schema design optimized for analytics workloads:

### Fact Table
- `fact_feedback` - Central fact table containing feedback metrics and foreign keys to dimensions

### Dimension Tables
1. `dim_time` - Time dimension for date-based analysis
2. `dim_user` - User information
3. `dim_issue` - Issue classification
4. `dim_location` - Location information
5. `dim_agency` - Agency/account mentioned information
6. `dim_hashtag` - Hashtag information
7. `bridge_feedback_hashtag` - Bridge table between feedback and hashtags (many-to-many)
8. `bridge_feedback_agency` - Bridge table between feedback and agencies (many-to-many)

### Materialized Views
For faster Power BI dashboard loading, the following materialized views are created:
1. `mv_daily_feedback_count` - Daily aggregated metrics
2. `mv_issue_distribution` - Issue class distribution
3. `mv_agency_performance` - Agency performance metrics
4. `mv_top_hashtags` - Top hashtags by usage

## Design Decisions

### Partitioning
The fact table is partitioned by date (monthly partitions) to improve query performance and maintenance. This is especially important for time-series data where queries often filter by date range.

### Indexing Strategy
- Indexes on foreign keys for faster joins
- Indexes on frequently filtered columns (language, created_date)
- Unique indexes on dimension tables for faster lookups

### Materialized Views
Materialized views pre-compute common aggregations to speed up dashboard loading time. These views are refreshed daily through a scheduled cron job.

## Setup Instructions

### Prerequisites
- Docker and Docker Compose installed

### Starting the Database

1. From the project root, run:
   ```bash
   docker-compose up -d
   ```

2. The database will be available at:
   - Host: localhost
   - Port: 5432
   - Database: ihd_analytics
   - Username: ihd_user
   - Password: ihd_password

### Connecting from Power BI

1. Open Power BI Desktop
2. Select "Get Data" > "Database" > "PostgreSQL"
3. Enter server and database information
4. Choose "Import" or "DirectQuery" mode (DirectQuery recommended for large datasets)
5. Select the tables/views to include in your model

## Optimizations for Power BI

The schema is designed with Power BI optimization in mind:

1. Star schema works perfectly with Power BI's relationship model
2. Materialized views reduce the computational load when loading dashboards
3. Precalculated total_interactions measure (retweets + replies + likes) for quicker visualization
4. Dimension hierarchies (e.g., agency sector > department) support drill-down functionality
5. Date dimension facilitates time intelligence functions in DAX

## Manual Database Access

To connect directly to the database for administration:

```bash
docker exec -it ihd_analytics_db psql -U ihd_user -d ihd_analytics
```

## Performance Monitoring

Monitor the database performance:

```bash
docker exec -it ihd_analytics_db psql -U ihd_user -d ihd_analytics -c "SELECT * FROM pg_stat_activity;"
```
