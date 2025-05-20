# IHD Analytics API Service

This Spring Boot service provides REST APIs to handle the upload and processing of feedback data in JSON format.

## Features

- REST API Endpoints:
  - Upload and process JSON files
  - Check processing job status
- File Handling:
  - Store uploaded files in ./uploads directory
  - Prevent duplicate file processing
- Batch Processing:
  - Parse JSON and insert into PostgreSQL database
  - Chunk-oriented processing (100 records/chunk)
  - Error handling with failed records logging

## Project Structure

```
api-service/
├── src/main/java/gov/ihd/apiservice/
│   ├── ApiServiceApplication.java (Main application class)
│   ├── batch/ (Spring Batch components)
│   │   ├── BatchConfiguration.java (Batch job configuration)
│   │   ├── FeedbackBatchItem.java (Data structure for batch processing)
│   │   ├── FeedbackItemProcessor.java (Processes JSON records)
│   │   ├── FeedbackItemWriter.java (Writes processed data to DB)
│   │   ├── JobCompletionNotificationListener.java (Job event listener)
│   │   └── JsonFileItemReader.java (Reads JSON files)
│   ├── config/
│   │   └── WebConfig.java (Web and Jackson configuration)
│   ├── controller/
│   │   └── FeedbackController.java (REST endpoints)
│   ├── dto/
│   │   ├── ApiResponse.java (Standardized API responses)
│   │   ├── JobStatus.java (Enum for job status)
│   │   └── ProcessingJobDto.java (Job status transfer object)
│   ├── entity/ (JPA entities matching database schema)
│   ├── exception/
│   │   └── GlobalExceptionHandler.java (Exception handling)
│   ├── model/
│   │   └── FeedbackItem.java (JSON data model)
│   ├── repository/ (Spring Data JPA repositories)
│   └── service/
│       ├── FileProcessingService.java (Handles file upload and processing)
│       └── TimeService.java (Time dimension utilities)
└── src/main/resources/
    └── application.properties (Application configuration)
```

## API Endpoints

### 1. Upload Feedback File

```
POST /api/v1/feedback/upload
Content-Type: multipart/form-data

file: [JSON file]
```

Response:
```json
{
  "success": true,
  "message": "File uploaded and processing started",
  "data": {
    "jobId": "uuid",
    "filename": "Feedback_50k_1.json",
    "status": "STARTED",
    "startTime": "2025-05-20T10:00:00"
  }
}
```

### 2. List All Processing Jobs

```
GET /api/v1/jobs
```

Response:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": [
    {
      "jobId": "uuid",
      "filename": "Feedback_50k_1.json",
      "status": "COMPLETED",
      "startTime": "2025-05-20T10:00:00",
      "endTime": "2025-05-20T10:05:00",
      "recordsProcessed": 50000
    }
  ]
}
```

### 3. Get Job Status

```
GET /api/v1/jobs/{jobId}
```

Response:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "jobId": "uuid",
    "filename": "Feedback_50k_1.json",
    "status": "COMPLETED",
    "startTime": "2025-05-20T10:00:00",
    "endTime": "2025-05-20T10:05:00",
    "recordsProcessed": 50000
  }
}
```

## Setup and Running

1. Make sure PostgreSQL is running (via Docker or local installation)
2. Build the application:
   ```
   ./gradlew build
   ```
3. Run the application:
   ```
   ./gradlew bootRun
   ```
4. The service will be available at http://localhost:8080

## Configuration

Application properties can be adjusted in `src/main/resources/application.properties`.
