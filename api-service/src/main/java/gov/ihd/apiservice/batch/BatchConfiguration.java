package gov.ihd.apiservice.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ihd.apiservice.model.FeedbackItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectReader;

import java.io.File;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FeedbackItemProcessor processor;
    private final FeedbackItemWriter writer;
    private final ObjectMapper objectMapper;
    
    @Value("${app.batch.chunk-size:100}")
    private int chunkSize;
    
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
                .skipLimit(10)
                .retry(Exception.class)
                .retryLimit(3)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        log.info("Starting step: {}", stepExecution.getStepName());
                    }
                    
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        log.info("Step completed: {}. Read count: {}, Write count: {}, Skip count: {}",
                            stepExecution.getStepName(),
                            stepExecution.getReadCount(),
                            stepExecution.getWriteCount(),
                            stepExecution.getSkipCount());
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
}
