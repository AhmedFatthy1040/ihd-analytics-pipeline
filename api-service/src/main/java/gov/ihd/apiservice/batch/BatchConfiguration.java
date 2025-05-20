package gov.ihd.apiservice.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ihd.apiservice.model.FeedbackItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.stream.Collectors;
import org.springframework.core.io.FileSystemResource;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FeedbackItemProcessor processor;
    private final FeedbackItemWriter writer;
    private final ObjectMapper objectMapper;
    
    @Value("${app.batch.chunk-size:200}")
    private int chunkSize;
    
    @Value("${app.batch.thread-count:4}")
    private int threadCount;
    
    @Bean
    public Job importFeedbackJob(JobCompletionNotificationListener listener, Step step1) {
        return new JobBuilder("importFeedbackJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }
    
    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .<FeedbackItem, FeedbackBatchItem>chunk(chunkSize, transactionManager)
                .reader(fileItemReader(null))
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE) // Don't limit skips for duplicate records
                .skipPolicy(new DuplicateRecordSkipPolicy())
                .retry(Exception.class)
                .retryLimit(3)
                // Enable multi-threaded processing
                .taskExecutor(batchTaskExecutor())
                .throttleLimit(threadCount)
                .listener(new BatchSkipListener())
                .listener(new StepExecutionListener() {
                    private String jobId;

                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        jobId = stepExecution.getJobParameters().getString("jobId");
                        log.info("Starting step: {} for job: {}", stepExecution.getStepName(), jobId);
                    }
                    
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        log.info("Step completed for job {}: Read count: {}, Write count: {}, Skip count: {}, Filter count: {}",
                            jobId,
                            stepExecution.getReadCount(),
                            stepExecution.getWriteCount(),
                            stepExecution.getSkipCount(),
                            stepExecution.getFilterCount());

                        // Check for rollback issues
                        if (stepExecution.getFailureExceptions() != null && !stepExecution.getFailureExceptions().isEmpty()) {
                            log.error("Step failures for job {}: {}", jobId,
                                stepExecution.getFailureExceptions().stream()
                                    .map(Throwable::getMessage)
                                    .collect(Collectors.joining("; ")));
                            return ExitStatus.FAILED;
                        }

                        // If we didn't write anything but read some records, that's suspicious
                        if (stepExecution.getWriteCount() == 0 && stepExecution.getReadCount() > 0) {
                            log.warn("Step wrote no records despite reading {}. Check for silent failures or invalid data.", 
                                stepExecution.getReadCount());
                            return new ExitStatus("COMPLETED_WITH_WARNINGS", 
                                "No records were written despite reading " + stepExecution.getReadCount() + " records");
                        }

                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }
    
    @Bean
    @StepScope
    public JsonItemReader<FeedbackItem> fileItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        if (filePath == null) {
            log.warn("No file path provided during context initialization");
            return new JsonItemReader<>(); // Empty reader for context initialization
        }
        
        File jsonFile = new File(filePath);
        log.info("Creating reader for file: {}", jsonFile.getAbsolutePath());
        
        JsonItemReader<FeedbackItem> reader = new JsonItemReader<>();
        reader.setResource(new FileSystemResource(jsonFile));
        reader.setJsonObjectReader(new JacksonJsonObjectReader<>(objectMapper, FeedbackItem.class));
        reader.setName("feedbackJsonItemReader");
        
        return reader;
    }

    @Bean
    public TaskExecutor batchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCount);
        executor.setMaxPoolSize(threadCount);
        executor.setQueueCapacity(threadCount * 2);
        executor.setThreadNamePrefix("batch-");
        executor.initialize();
        return executor;
    }
}
