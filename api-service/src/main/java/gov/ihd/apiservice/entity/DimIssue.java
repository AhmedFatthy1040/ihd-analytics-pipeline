package gov.ihd.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_issue", schema = "ihd_analytics")
public class DimIssue {
    
    @Id
    @Column(name = "issue_id")
    private Integer issueId;
    
    @Column(name = "issue_class_key", nullable = false)
    private Integer issueClassKey;
    
    @Column(name = "issue_class_code", nullable = false, length = 100)
    private String issueClassCode;
}
