package gov.ihd.apiservice.controller;

import gov.ihd.apiservice.dto.ApiResponse;
import gov.ihd.apiservice.dto.ProcessingJobDto;
import gov.ihd.apiservice.service.JobProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobProgressService jobProgressService;

    @Autowired
    public JobController(JobProgressService jobProgressService) {
        this.jobProgressService = jobProgressService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProcessingJobDto>>> getAllJobs() {
        List<ProcessingJobDto> jobs = jobProgressService.getAllJobs();
        ApiResponse<List<ProcessingJobDto>> response = new ApiResponse<>(true, "Jobs retrieved successfully", jobs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<ProcessingJobDto>> getJobStatus(@PathVariable String jobId) {
        ProcessingJobDto job = jobProgressService.getJobStatus(jobId);
        if (job == null) {
            ApiResponse<ProcessingJobDto> response = new ApiResponse<>(false, "Job not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<ProcessingJobDto> response = new ApiResponse<>(true, "Job retrieved successfully", job);
        return ResponseEntity.ok(response);
    }
}
