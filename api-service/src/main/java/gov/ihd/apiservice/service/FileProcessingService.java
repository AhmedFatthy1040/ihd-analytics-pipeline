package gov.ihd.apiservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ihd.apiservice.batch.JsonFileItemReader;
import gov.ihd.apiservice.dto.JobStatus;
import gov.ihd.apiservice.dto.ProcessingJobDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.task.TaskExecutor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import gov.ihd.apiservice.event.JobEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final JobLauncher jobLauncher;
    private final Job importFeedbackJob;
    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper;
    private final TaskExecutor taskExecutor;
    private final JobEventListener jobEventListener;
    
    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;
    
    @Value("${app.job-history.max-size:1000}")
    private int maxJobHistorySize;
    
    public ProcessingJobDto uploadAndProcessFile(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        File uploadDir = new File(this.uploadDir);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new IOException("Failed to create upload directory: " + this.uploadDir);
            }
            log.info("Created upload directory: {}", uploadDir.getAbsolutePath());
        }
        
        // Check if file exists in uploads directory
        String originalFilename = file.getOriginalFilename();
        File targetFile = new File(uploadDir, originalFilename);
        
        if (targetFile.exists()) {
            throw new RuntimeException("File already exists in upload directory: " + originalFilename);
        }
        
        // Save the file
        file.transferTo(targetFile.toPath());
        log.info("File saved to: {}", targetFile.getAbsolutePath());
        
        // Start the processing job
        return startProcessingJob(targetFile, originalFilename);
    }
    
    @Transactional
    public ProcessingJobDto startProcessingJob(File file, String filename) {
        // Clean up old job entries if we exceed the max size
        ((JobProgressService) jobEventListener).cleanupOldJobs(maxJobHistorySize);
        
        // Generate a unique job ID and create the job
        String jobId = UUID.randomUUID().toString();
        ProcessingJobDto jobDto = ((JobProgressService) jobEventListener).createJob(jobId, filename);
        
        // Run job asynchronously using TaskExecutor
        taskExecutor.execute(() -> {
            try {
                // Prepare job parameters
                JobParameters parameters = new JobParametersBuilder()
                        .addString("jobId", jobId)
                        .addString("filePath", file.getAbsolutePath())
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();
                
                // Launch the job
                JobExecution jobExecution = jobLauncher.run(importFeedbackJob, parameters);
                
                // Update status based on job execution
                updateJobStatus(jobId, jobExecution);
                
            } catch (JobExecutionAlreadyRunningException | JobRestartException |
                     JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
                log.error("Error running job: {}", e.getMessage(), e);
                jobEventListener.onJobFailed(jobId, e.getMessage());
            }
        });
        
        return jobDto;
    }
    
    private void updateJobStatus(String jobId, JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            int recordsProcessed = (int)jobExecution.getStepExecutions().stream()
                    .mapToLong(StepExecution::getWriteCount)
                    .sum();
            jobEventListener.onJobCompleted(jobId, recordsProcessed);
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            String errorMessage = jobExecution.getAllFailureExceptions().stream()
                    .map(Throwable::getMessage)
                    .collect(Collectors.joining(", "));
            jobEventListener.onJobFailed(jobId, errorMessage);
        }
    }
}
