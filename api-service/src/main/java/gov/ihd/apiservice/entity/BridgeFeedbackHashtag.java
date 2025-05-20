package gov.ihd.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "bridge_feedback_hashtag", schema = "ihd_analytics")
@IdClass(BridgeFeedbackHashtag.BridgeId.class)
public class BridgeFeedbackHashtag {
    
    @Id
    @Column(name = "feedback_id")
    private Long feedbackId;
    
    @Id
    @Column(name = "hashtag_id")
    private Integer hashtagId;
    
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;
    
    @Data
    public static class BridgeId implements Serializable {
        private Long feedbackId;
        private Integer hashtagId;
    }
}
