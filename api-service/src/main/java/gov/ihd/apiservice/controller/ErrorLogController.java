package gov.ihd.apiservice.controller;

import gov.ihd.apiservice.dto.ApiResponse;
import gov.ihd.apiservice.entity.JobErrorLog;
import gov.ihd.apiservice.service.ErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ErrorLogController {

    private final ErrorLogService errorLogService;
    
    @GetMapping("/jobs/{jobId}/errors")
    public ResponseEntity<ApiResponse<List<JobErrorLog>>> getJobErrors(@PathVariable String jobId) {
        List<JobErrorLog> errors = errorLogService.getErrorsForJob(jobId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(errors));
    }
}
