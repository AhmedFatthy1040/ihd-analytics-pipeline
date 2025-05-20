package gov.ihd.apiservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessingJobDto {
    private String jobId;
    private String filename;
    private JobStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer recordsProcessed;
    private String errorMessage;
}
