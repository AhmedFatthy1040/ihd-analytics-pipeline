# IHD Analytics Pipeline

A comprehensive analytics pipeline for monitoring citizen feedback across different government departments. This system processes and analyzes feedback data (tweets) to provide insights through Power BI dashboards.

## Project Structure

- **Frontend**: React application with file upload capabilities and job monitoring interface
- **Backend**: Spring Boot REST API with batch processing for feedback data
- **Database**: PostgreSQL with star schema for optimized analytics with partitioning
- **Analytics**: Power BI dashboards (partially implemented)

## Project Status and Evaluation

### Completed Components
- ✅ Database schema design and implementation with star schema
- ✅ Spring Boot backend with asynchronous batch processing capabilities
- ✅ React frontend with drag & drop file upload and job monitoring
- ✅ Docker containerization for all services
- ✅ Error handling and reporting system with detailed error logging
- ✅ Data partitioning and performance optimization
- ✅ Materialized views for analytical queries

### Incomplete Components
- ⚠️ Power BI dashboards (not fully implemented due to time constraints and Linux compatibility issues)

### Data Model Explanation
The database follows a star schema optimized for analytics queries. This model was chosen for its:
- Query performance for aggregation operations through denormalization
- Simplified structure for business analytics with clear dimension hierarchies
- Scalability for large datasets through partitioning by date
- Dimensional hierarchies support for multidimensional analysis

The central fact table (`fact_feedback`) contains metrics (like retweet_count, reply_count, etc.) and foreign keys to various dimension tables, allowing for efficient slicing and dicing of data across multiple dimensions. The fact table is partitioned by date for improved query performance and data management. Bridge tables handle many-to-many relationships between feedback and entities like hashtags and agencies.

### Challenges Faced
1. **Data Volume**: Processing large JSON files (50k+ records) efficiently required implementation of a chunked processing approach with Spring Batch. The solution uses a configurable chunk size (default 200) and multi-threaded processing.
2. **Performance Optimization**: Required careful index design and materialized views to ensure responsive analytics queries. Created specialized indexes on commonly filtered columns and materialized views for common aggregate queries.
3. **Error Handling**: Developing a robust error logging system that could handle and report various types of processing errors. Each job error is stored in a dedicated error logging table with full traceability.
4. **Database Design**: Balancing normalization with query performance in the star schema while handling hierarchical data and many-to-many relationships through bridge tables.
5. **Table Partitioning**: Implemented date-based partitioning on the fact table to improve query performance and management of large datasets.
6. **Asynchronous Processing**: Implemented asynchronous job execution with progress monitoring to prevent UI blocking during long-running imports.
7. **Power BI Integration**: Unable to complete due to time constraints with final exams and the limitation of Power BI Desktop being Windows-only (not available on Linux)

### Evaluation Rubric
| Component | Score (1-5) | Notes |
|-----------|-------------|-------|
| Code Quality | 4 | Well-structured, follows best practices, includes error handling and proper separation of concerns |
| Database Design | 5 | Optimized star schema with appropriate indexing, partitioning, and materialized views |
| API Design | 4 | RESTful API with standardized responses, proper documentation, and error handling |
| Frontend | 4 | Responsive UI, clean design, good UX for file uploads and job monitoring |
| Batch Processing | 5 | Multi-threaded processing with chunking, fault-tolerance, and skip/retry policies |
| Dashboard Performance | N/A | Not fully implemented due to platform constraints |
| Documentation | 4 | Comprehensive documentation of all components including API endpoints |
| Error Handling | 5 | Robust error logging and reporting system with detailed exception tracking |
| Scalability | 4 | Horizontal scaling through Docker, database partitioning, and asynchronous processing |

## Database Schema

The database follows a star schema optimized for analytics:

### Fact Table
- `fact_feedback`: Central fact table containing tweet data, metrics (retweet_count, reply_count, etc.), and foreign keys to dimensions
  - Partitioned by date (monthly partitions for 2024-2025) for improved query performance
  - Primary key includes both feedback_id and created_date for partitioning support

### Dimension Tables
- `dim_time`: Time dimension for date-based analysis with hierarchies (year, quarter, month, day)
- `dim_user`: Twitter user information including followers_count, following_count, tweet_count
- `dim_issue`: Issue classification with issue class codes and keys
- `dim_location`: Location information parsed from user location strings (country, city, region)
- `dim_agency`: Agency/account mentioned information with sector and department categorization
- `dim_hashtag`: Hashtag information with unique hashtag text

### Bridge Tables
- `bridge_feedback_hashtag`: Many-to-many relationship between feedback and hashtags
- `bridge_feedback_agency`: Many-to-many relationship between feedback and agencies mentioned

### Materialized Views
For faster Power BI dashboard loading and cached analytical results:
- `mv_daily_feedback_count`: Daily aggregated metrics including total interactions and impressions
- `mv_issue_distribution`: Issue class distribution with aggregated metrics
- `mv_agency_performance`: Agency performance metrics with mentions and interaction counts
- `mv_top_hashtags`: Top hashtags by usage with engagement metrics

### Performance Optimizations
- Strategic indexing on commonly queried columns
- Table partitioning on date columns
- Materialized views with unique indexes for fast lookups
- Database functions for efficient data processing
- Automated materialized view refresh mechanism

## Setup Instructions

### Prerequisites
- Docker and Docker Compose
- Node.js v22.15.1 and npm
- Java 17
- Maven

### Running with Docker
```bash
# Start the complete system with Docker Compose
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5432
- API Service on port 8080
- Frontend on port 3000

### Manual Setup

#### Database Setup
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

## Power BI Dashboard

Due to time constraints caused by final exams and the limitation that Power BI Desktop can only be installed on Windows (not Linux), the Power BI component of this project is incomplete. Below is information about expected performance benchmarks based on research:

### Data Volume Benchmark (Expected)
For database size of 500k tweets case:
- Time to publish reports: Approximately 5-7 minutes
- Time to load visuals and respond to slicers/filters after publishing: 2-3 seconds

For database size of 1M tweets case:
- Time to publish reports: Approximately 10-15 minutes
- Time to load visuals and respond to slicers/filters after publishing: 3-5 seconds

### Power BI Dashboard Links
[Not available due to implementation constraints]

### Power BI Dashboard Optimization Strategies (Planned)
1. Use of DirectQuery mode with appropriate indexing
2. Implementation of aggregation tables in PostgreSQL
3. Query folding optimization
4. Visual-level filters to reduce data load
5. Use of imported data mode for smaller datasets

## API Service

The API service provides endpoints for uploading and processing feedback data files. For more details, see [api-service/README.md](api-service/README.md).

### Key Features

- REST API for file uploads and job monitoring
- Spring Batch processing for efficient data handling
- Database integration with PostgreSQL
- Chunked processing (100 records/chunk)
- Error logging and reporting

### API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/feedback/upload` | POST | Upload JSON feedback file |
| `/api/v1/jobs` | GET | List all processing jobs |
| `/api/v1/jobs/{jobId}` | GET | Get specific job status |
| `/api/v1/jobs/{jobId}/errors` | GET | Get errors for specific job |
| `/api/v1/health` | GET | Health check endpoint |

### Running the API Service

```bash
cd api-service
./start.sh
```

## API Documentation

### Endpoints Details

1. **Upload Feedback File**
   - Endpoint: `/api/v1/feedback/upload`
   - Method: `POST`
   - Content-Type: `multipart/form-data`
   - Parameter: `file` (JSON file)
   - Response Example:
     ```json
     {
       "success": true,
       "message": "File uploaded and processing started",
       "data": {
         "jobId": "56f5f3e7-1eb6-49d1-9a23-77251d17b43a",
         "filename": "Feedback_50k_2.json",
         "status": "STARTED",
         "startTime": "2025-05-20T15:55:37.978997378",
         "endTime": null,
         "recordsProcessed": null,
         "errorMessage": null
       }
     }
     ```

2. **List Processing Jobs**
   - Endpoint: `/api/v1/jobs`
   - Method: `GET`
   - Response Example:
     ```json
     {
       "success": true,
       "message": "Operation completed successfully",
       "data": [
         {
           "jobId": "56f5f3e7-1eb6-49d1-9a23-77251d17b43a",
           "filename": "Feedback_50k_2.json",
           "status": "COMPLETED",
           "startTime": "2025-05-20T15:55:37.978997378",
           "endTime": "2025-05-20T15:57:45.123456789",
           "recordsProcessed": 50000,
           "errorMessage": null
         }
       ]
     }
     ```

3. **Get Job Status**
   - Endpoint: `/api/v1/jobs/{jobId}`
   - Method: `GET`
   - Response: Same format as individual job in the list response

4. **Get Job Errors**
   - Endpoint: `/api/v1/jobs/{jobId}/errors`
   - Method: `GET`
   - Response Example:
     ```json
     {
       "success": true,
       "message": "Operation completed successfully",
       "data": [] // Contains error records if any
     }
     ```

5. **Health Check**
   - Endpoint: `/api/v1/health`
   - Method: `GET`
   - Response Example:
     ```json
     {
       "success": true,
       "message": "Operation completed successfully",
       "data": {
         "environment": "default",
         "service": "ihd-api-service",
         "status": "UP",
         "timestamp": 1747756457769
       }
     }
     ```

### Key Features and Requirements

1. **File Upload and Storage**
   - Files are stored in the `./uploads` directory
   - Duplicate file uploads are prevented
   - Only JSON files are accepted

2. **Batch Processing**
   - Files are processed asynchronously using Spring Batch
   - Processing is chunk-oriented (100 records per chunk)
   - Progress can be monitored through the jobs endpoint
   - Failed records are logged and can be retrieved

3. **Error Handling**
   - Comprehensive error logging
   - Failed records are tracked and can be queried
   - Job status includes error messages when applicable

### Usage Examples

1. **Upload a File**
   ```bash
   curl -X POST -F "file=@data/feedback.json" http://localhost:8080/api/v1/feedback/upload
   ```

2. **Check Job Status**
   ```bash
   curl http://localhost:8080/api/v1/jobs/{jobId}
   ```

3. **List All Jobs**
   ```bash
   curl http://localhost:8080/api/v1/jobs
   ```

4. **Check Health**
   ```bash
   curl http://localhost:8080/api/v1/health
   ```

## Frontend

The frontend is a React application providing a user interface for uploading JSON files and monitoring processing jobs. For more details, see [frontend/README.md](frontend/README.md).

### Key Features

- Drag & drop file upload interface for JSON files
- Upload progress indicator
- Success/error notifications
- Job status monitoring dashboard with auto-refresh
- Mock data support for development without backend

### Running the Frontend

```bash
# Install dependencies
cd frontend
npm install

# Start development server
npm start
```

The frontend will be available at http://localhost:3000.

### Build for Production

```bash
cd frontend
npm run build
```

This creates a production build in the `build` directory that can be served by any static file server or the included Nginx configuration.
