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

## Project Status

Current progress:
- ✅ Database schema design and implementation
- ✅ Spring Boot backend
- ✅ React frontend
- ⬜ Power BI dashboards

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
