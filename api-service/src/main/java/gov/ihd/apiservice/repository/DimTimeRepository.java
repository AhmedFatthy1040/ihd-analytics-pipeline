package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.DimTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DimTimeRepository extends JpaRepository<DimTime, Integer> {
    Optional<DimTime> findByFullDate(LocalDate date);
}
