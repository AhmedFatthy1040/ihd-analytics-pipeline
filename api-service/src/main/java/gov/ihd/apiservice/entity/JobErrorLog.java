package gov.ihd.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "job_error_log", schema = "ihd_analytics")
public class JobErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "job_id", nullable = false)
    private String jobId;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;
    
    @Column(name = "item_data", columnDefinition = "TEXT")
    private String itemData;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public JobErrorLog(String jobId, String errorMessage, String stackTrace, String itemData) {
        this.jobId = jobId;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
        this.itemData = itemData;
    }
}
