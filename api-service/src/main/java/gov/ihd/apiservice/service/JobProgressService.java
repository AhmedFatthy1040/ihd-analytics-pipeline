package gov.ihd.apiservice.service;

import gov.ihd.apiservice.dto.JobStatus;
import gov.ihd.apiservice.dto.ProcessingJobDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import gov.ihd.apiservice.event.JobEventListener;

@Slf4j
@Service
public class JobProgressService implements JobEventListener {
    private final Map<String, ProcessingJobDto> jobStatusMap = new ConcurrentHashMap<>();

    public ProcessingJobDto createJob(String jobId, String filename) {
        ProcessingJobDto jobDto = new ProcessingJobDto();
        jobDto.setJobId(jobId);
        jobDto.setFilename(filename);
        jobDto.setStartTime(LocalDateTime.now());
        jobDto.setStatus(JobStatus.STARTED);
        jobDto.setRecordsProcessed(0);
        
        jobStatusMap.put(jobId, jobDto);
        return jobDto;
    }
    
    public void updateJobProgress(String jobId, int recordsProcessed) {
        ProcessingJobDto job = jobStatusMap.get(jobId);
        if (job != null) {
            job.setRecordsProcessed(recordsProcessed);
        }
    }
    
    public void completeJob(String jobId, int recordsProcessed) {
        ProcessingJobDto job = jobStatusMap.get(jobId);
        if (job != null) {
            job.setStatus(JobStatus.COMPLETED);
            job.setEndTime(LocalDateTime.now());
            job.setRecordsProcessed(recordsProcessed);
        }
    }
    
    public void failJob(String jobId, String errorMessage) {
        ProcessingJobDto job = jobStatusMap.get(jobId);
        if (job != null) {
            job.setStatus(JobStatus.FAILED);
            job.setEndTime(LocalDateTime.now());
            job.setErrorMessage(errorMessage);
        }
    }
    
    public ProcessingJobDto getJobStatus(String jobId) {
        return jobStatusMap.get(jobId);
    }
    
    public List<ProcessingJobDto> getAllJobs() {
        return new ArrayList<>(jobStatusMap.values()).stream()
                .sorted((j1, j2) -> j2.getStartTime().compareTo(j1.getStartTime()))
                .collect(Collectors.toList());
    }
    
    public void cleanupOldJobs(int maxJobHistorySize) {
        if (jobStatusMap.size() >= maxJobHistorySize) {
            List<String> oldestJobs = jobStatusMap.entrySet().stream()
                .filter(e -> e.getValue().getStatus().equals(JobStatus.COMPLETED) || 
                           e.getValue().getStatus().equals(JobStatus.FAILED))
                .sorted((e1, e2) -> e1.getValue().getStartTime().compareTo(e2.getValue().getStartTime()))
                .limit(jobStatusMap.size() - maxJobHistorySize + 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            oldestJobs.forEach(jobStatusMap::remove);
        }
    }

    @Override
    public void onJobProgress(String jobId, int recordsProcessed) {
        updateJobProgress(jobId, recordsProcessed);
    }

    @Override
    public void onJobCompleted(String jobId, int recordsProcessed) {
        completeJob(jobId, recordsProcessed);
    }

    @Override
    public void onJobFailed(String jobId, String errorMessage) {
        failJob(jobId, errorMessage);
    }
}
