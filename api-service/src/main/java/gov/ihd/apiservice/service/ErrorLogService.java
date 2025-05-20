package gov.ihd.apiservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ihd.apiservice.entity.JobErrorLog;
import gov.ihd.apiservice.repository.JobErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorLogService {

    private final JobErrorLogRepository errorLogRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * Log an error that occurred during job processing
     * 
     * @param jobId The ID of the job
     * @param errorMessage The error message
     * @param throwable The exception that occurred
     * @param item The data item that caused the error
     */
    public void logError(String jobId, String errorMessage, Throwable throwable, Object item) {
        try {
            // Convert stack trace to string
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTrace = sw.toString();
            
            // Convert item to JSON string
            String itemJson = null;
            if (item != null) {
                try {
                    itemJson = objectMapper.writeValueAsString(item);
                } catch (JsonProcessingException e) {
                    log.warn("Could not serialize item to JSON: {}", e.getMessage());
                    itemJson = "Could not serialize item: " + item.toString();
                }
            }
            
            // Create and save error log
            JobErrorLog errorLog = new JobErrorLog(jobId, errorMessage, stackTrace, itemJson);
            errorLogRepository.save(errorLog);
            
        } catch (Exception e) {
            log.error("Failed to log error: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get all errors for a specific job
     * 
     * @param jobId The ID of the job
     * @return List of error logs
     */
    public List<JobErrorLog> getErrorsForJob(String jobId) {
        return errorLogRepository.findByJobId(jobId);
    }
}
