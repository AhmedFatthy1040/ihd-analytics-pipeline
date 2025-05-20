package gov.ihd.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "bridge_feedback_agency", schema = "ihd_analytics")
@IdClass(BridgeFeedbackAgency.BridgeId.class)
public class BridgeFeedbackAgency {
    
    @Id
    @Column(name = "feedback_id")
    private Long feedbackId;
    
    @Id
    @Column(name = "agency_id")
    private Integer agencyId;
    
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;
    
    @Data
    public static class BridgeId implements Serializable {
        private Long feedbackId;
        private Integer agencyId;
    }
}
