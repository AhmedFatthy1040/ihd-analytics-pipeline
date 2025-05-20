package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.DimHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DimHashtagRepository extends JpaRepository<DimHashtag, Integer> {
    Optional<DimHashtag> findByHashtagText(String hashtagText);
}
