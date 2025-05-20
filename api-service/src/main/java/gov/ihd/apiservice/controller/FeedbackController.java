package gov.ihd.apiservice.controller;

import gov.ihd.apiservice.dto.ApiResponse;
import gov.ihd.apiservice.dto.ProcessingJobDto;
import gov.ihd.apiservice.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FeedbackController {

    private final FileProcessingService fileProcessingService;
    
    @PostMapping(value = "/feedback/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProcessingJobDto>> uploadFeedbackFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(ApiResponse.<ProcessingJobDto>error("Please select a file to upload"));
            }
            
            // Check if file is JSON
            if (!file.getOriginalFilename().endsWith(".json")) {
                return ResponseEntity
                        .badRequest()
                        .body(ApiResponse.<ProcessingJobDto>error("Only JSON files are supported"));
            }
            
            ProcessingJobDto job = fileProcessingService.uploadAndProcessFile(file);
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(ApiResponse.<ProcessingJobDto>success("File uploaded and processing started", job));
            
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ProcessingJobDto>error("Error uploading file: " + e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error processing file: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<ProcessingJobDto>error(e.getMessage()));
        }
    }
    
    // Jobs endpoints have been moved to JobController
    // Please use /api/v1/jobs and /api/v1/jobs/{jobId} endpoints instead

}
