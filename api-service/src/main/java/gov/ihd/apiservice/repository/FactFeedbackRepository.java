package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.FactFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactFeedbackRepository extends JpaRepository<FactFeedback, FactFeedback.FeedbackId> {
    Optional<FactFeedback> findByTweetIdAndCreatedDate(String tweetId, LocalDate createdDate);
    
    /**
     * Find existing tweet IDs from a list of IDs in a single query
     * This helps optimize batch operations by avoiding individual lookups
     */
    @Query("SELECT f.tweetId FROM FactFeedback f WHERE f.tweetId IN :tweetIds")
    List<String> findExistingTweetIds(@Param("tweetIds") List<String> tweetIds);
}
