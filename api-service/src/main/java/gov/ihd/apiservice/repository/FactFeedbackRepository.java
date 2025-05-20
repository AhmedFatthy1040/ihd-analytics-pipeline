package gov.ihd.apiservice.repository;

import gov.ihd.apiservice.entity.FactFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface FactFeedbackRepository extends JpaRepository<FactFeedback, FactFeedback.FeedbackId> {
    Optional<FactFeedback> findByTweetIdAndCreatedDate(String tweetId, LocalDate createdDate);
}
