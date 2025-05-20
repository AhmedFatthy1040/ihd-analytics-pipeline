package gov.ihd.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_agency", schema = "ihd_analytics")
public class DimAgency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agency_id")
    private Integer agencyId;
    
    @Column(name = "agency_name", nullable = false, length = 100)
    private String agencyName;
    
    @Column(name = "agency_account", nullable = false, length = 100, unique = true)
    private String agencyAccount;
    
    @Column(name = "sector", length = 100)
    private String sector;
    
    @Column(name = "department", length = 100)
    private String department;
}
