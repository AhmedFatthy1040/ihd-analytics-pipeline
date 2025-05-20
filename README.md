# IHD Analytics Pipeline

A comprehensive analytics pipeline for monitoring citizen feedback across different government departments. This system processes and analyzes feedback data (using tweets) to provide insights through Power BI dashboards.

## Project Structure

- **Frontend**: React application with file upload capabilities
- **Backend**: Spring Boot REST API with batch processing
- **Database**: PostgreSQL with star schema for optimized analytics
- **Analytics**: Power BI dashboards

## Database Schema

The database follows a star schema optimized for analytics:

### Fact Table
- `fact_feedback`: Central fact table containing feedback metrics and foreign keys to dimensions, partitioned by date

### Dimension Tables
- `dim_time`: Time dimension for date-based analysis
- `dim_user`: User information
- `dim_issue`: Issue classification
- `dim_location`: Location information
- `dim_agency`: Agency/account mentioned information
- `dim_hashtag`: Hashtag information

### Bridge Tables
- `bridge_feedback_hashtag`: Many-to-many relationship between feedback and hashtags
- `bridge_feedback_agency`: Many-to-many relationship between feedback and agencies

### Materialized Views
For faster Power BI dashboard loading:
- `mv_daily_feedback_count`: Daily aggregated metrics
- `mv_issue_distribution`: Issue class distribution
- `mv_agency_performance`: Agency performance metrics
- `mv_top_hashtags`: Top hashtags by usage

## Setup Instructions

### Prerequisites
- Docker and Docker Compose
- Node.js and npm
- Java 17
- Maven

### Database Setup
```bash
cd database
docker-compose up -d
```

The PostgreSQL database will be available at:
- Host: localhost
- Port: 5432
- Database: ihd_analytics
- Username: ihd_user
- Password: ihd_password

## Project Status

Current progress:
- ✅ Database schema design and implementation
- ⬜ Spring Boot backend
- ⬜ React frontend
- ⬜ Power BI dashboards
