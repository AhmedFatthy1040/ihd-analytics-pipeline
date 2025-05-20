package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.BridgeFeedbackAgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BridgeFeedbackAgencyRepository extends JpaRepository<BridgeFeedbackAgency, BridgeFeedbackAgency.BridgeId> {
}
