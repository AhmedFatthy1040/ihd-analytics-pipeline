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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final JobLauncher jobLauncher;
    private final Job importFeedbackJob;
    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper;
    
    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;
    
    private final Map<String, ProcessingJobDto> jobStatusMap = new ConcurrentHashMap<>();
    
    public ProcessingJobDto uploadAndProcessFile(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Check if file exists in uploads directory
        String originalFilename = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(originalFilename);
        if (Files.exists(filePath)) {
            throw new RuntimeException("File already exists in upload directory: " + originalFilename);
        }
        
        // Save the file
        file.transferTo(filePath.toFile());
        log.info("File saved: {}", filePath);
        
        // Start the processing job
        return startProcessingJob(filePath.toFile(), originalFilename);
    }
    
    public ProcessingJobDto startProcessingJob(File file, String filename) {
        // Generate a unique job ID
        String jobId = UUID.randomUUID().toString();
        
        // Create a job status object
        ProcessingJobDto jobDto = new ProcessingJobDto();
        jobDto.setJobId(jobId);
        jobDto.setFilename(filename);
        jobDto.setStartTime(LocalDateTime.now());
        jobDto.setStatus(JobStatus.STARTED);
        
        // Store job status
        jobStatusMap.put(jobId, jobDto);
        
        // Run job asynchronously
        new Thread(() -> {
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
                
                // Update status on failure
                ProcessingJobDto job = jobStatusMap.get(jobId);
                job.setStatus(JobStatus.FAILED);
                job.setEndTime(LocalDateTime.now());
                job.setErrorMessage(e.getMessage());
            }
        }).start();
        
        return jobDto;
    }
    
    public List<ProcessingJobDto> getAllJobs() {
        return jobStatusMap.values().stream()
                .sorted((j1, j2) -> j2.getStartTime().compareTo(j1.getStartTime()))
                .collect(Collectors.toList());
    }
    
    public ProcessingJobDto getJobStatus(String jobId) {
        return jobStatusMap.get(jobId);
    }
    
    private void updateJobStatus(String jobId, JobExecution jobExecution) {
        ProcessingJobDto job = jobStatusMap.get(jobId);
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            job.setStatus(JobStatus.COMPLETED);
            job.setEndTime(LocalDateTime.now());
            job.setRecordsProcessed((int)jobExecution.getStepExecutions().stream()
                    .mapToLong(StepExecution::getWriteCount).sum());
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            job.setStatus(JobStatus.FAILED);
            job.setEndTime(LocalDateTime.now());
            job.setErrorMessage(jobExecution.getAllFailureExceptions().stream()
                    .map(Throwable::getMessage)
                    .collect(Collectors.joining(", ")));
        } else {
            job.setStatus(JobStatus.valueOf(jobExecution.getStatus().name()));
        }
    }
}
