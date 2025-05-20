package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.DimLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimLocationRepository extends JpaRepository<DimLocation, Integer> {
    Optional<DimLocation> findByCountryAndCityAndRegion(String country, String city, String region);
}
