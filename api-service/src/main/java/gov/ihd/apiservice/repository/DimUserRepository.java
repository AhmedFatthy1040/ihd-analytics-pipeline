package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.DimUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DimUserRepository extends JpaRepository<DimUser, String> {
}
