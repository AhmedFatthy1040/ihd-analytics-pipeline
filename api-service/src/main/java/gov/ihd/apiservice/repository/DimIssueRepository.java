package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.DimIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimIssueRepository extends JpaRepository<DimIssue, Integer> {
    Optional<DimIssue> findByIssueClassKeyAndIssueClassCode(Integer issueClassKey, String issueClassCode);
}
