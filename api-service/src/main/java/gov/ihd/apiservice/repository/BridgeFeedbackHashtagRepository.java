package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.BridgeFeedbackHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BridgeFeedbackHashtagRepository extends JpaRepository<BridgeFeedbackHashtag, BridgeFeedbackHashtag.BridgeId> {
}
