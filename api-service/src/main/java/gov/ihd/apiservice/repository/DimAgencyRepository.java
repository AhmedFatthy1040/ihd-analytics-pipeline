package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.DimAgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimAgencyRepository extends JpaRepository<DimAgency, Integer> {
    Optional<DimAgency> findByAgencyAccount(String agencyAccount);
}
