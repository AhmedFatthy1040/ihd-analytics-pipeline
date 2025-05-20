package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.JobErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobErrorLogRepository extends JpaRepository<JobErrorLog, Long> {
    List<JobErrorLog> findByJobId(String jobId);
}
