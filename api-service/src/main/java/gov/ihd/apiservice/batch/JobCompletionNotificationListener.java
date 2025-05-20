package gov.ihd.apiservice.batch;

import gov.ihd.apiservice.event.JobEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {
    private final JobEventListener jobEventListener;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobId = jobExecution.getJobParameters().getString("jobId");
        if (jobId != null) {
            log.info("Job {} is starting", jobExecution.getJobInstance().getJobName());
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobId = jobExecution.getJobParameters().getString("jobId");
        
        if (jobId != null) {
            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                int recordsProcessed = (int)jobExecution.getStepExecutions().stream()
                        .mapToLong(stepExecution -> stepExecution.getWriteCount())
                        .sum();
                jobEventListener.onJobCompleted(jobId, recordsProcessed);
                log.info("Job {} completed successfully. Processed {} records.", 
                    jobExecution.getJobInstance().getJobName(),
                    recordsProcessed);
            } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
                String errorMessage = jobExecution.getAllFailureExceptions().stream()
                        .map(Throwable::getMessage)
                        .collect(Collectors.joining("; "));
                jobEventListener.onJobFailed(jobId, errorMessage);
                log.error("Job {} failed with status {}. Errors: {}", 
                    jobExecution.getJobInstance().getJobName(),
                    jobExecution.getStatus(),
                    errorMessage);
            }
        }
    }
}
