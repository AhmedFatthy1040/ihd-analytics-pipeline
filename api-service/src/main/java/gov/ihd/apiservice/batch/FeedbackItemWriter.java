package gov.ihd.apiservice.batch;

import gov.ihd.apiservice.entity.BridgeFeedbackAgency;
import gov.ihd.apiservice.entity.BridgeFeedbackHashtag;
import gov.ihd.apiservice.entity.FactFeedback;
import gov.ihd.apiservice.repository.BridgeFeedbackAgencyRepository;
import gov.ihd.apiservice.repository.BridgeFeedbackHashtagRepository;
import gov.ihd.apiservice.repository.FactFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackItemWriter implements ItemWriter<FeedbackBatchItem> {

    private final FactFeedbackRepository feedbackRepository;
    private final BridgeFeedbackHashtagRepository hashtagBridgeRepository;
    private final BridgeFeedbackAgencyRepository agencyBridgeRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends FeedbackBatchItem> items) throws Exception {
        if (items.isEmpty()) {
            return;
        }

        List<FactFeedback> feedbacks = new ArrayList<>();
        List<BridgeFeedbackHashtag> hashtagBridges = new ArrayList<>();
        List<BridgeFeedbackAgency> agencyBridges = new ArrayList<>();
        
        // Pre-fetch existing tweet IDs to avoid individual lookups
        List<String> tweetIds = new ArrayList<>();
        for (FeedbackBatchItem item : items) {
            if (item != null && item.getFeedback() != null) {
                tweetIds.add(item.getFeedback().getTweetId());
            }
        }
        
        // Get existing tweet IDs in a single query
        List<String> existingTweetIds = feedbackRepository.findExistingTweetIds(tweetIds);
        
        for (FeedbackBatchItem item : items) {
            if (item != null && item.getFeedback() != null) {
                FactFeedback feedback = item.getFeedback();
                
                // Skip if tweet already exists
                if (existingTweetIds.contains(feedback.getTweetId())) {
                    log.debug("Skipping duplicate feedback with tweet_id: {}", feedback.getTweetId());
                    continue;
                }
                
                feedbacks.add(feedback);
            }
        }
        
        // Batch save all feedback items at once
        if (!feedbacks.isEmpty()) {
            List<FactFeedback> savedFeedbacks = feedbackRepository.saveAll(feedbacks);
            
            // Process bridges for all saved feedback items
            for (int i = 0; i < savedFeedbacks.size(); i++) {
                FactFeedback savedFeedback = savedFeedbacks.get(i);
                Long feedbackId = savedFeedback.getFeedbackId();
                
                // Find the matching FeedbackBatchItem
                for (FeedbackBatchItem item : items) {
                    if (item != null && item.getFeedback() != null && 
                        item.getFeedback().getTweetId().equals(savedFeedback.getTweetId())) {
                        
                        // Process hashtag bridges
                        if (item.getHashtagBridges() != null) {
                            for (BridgeFeedbackHashtag bridge : item.getHashtagBridges()) {
                                bridge.setFeedbackId(feedbackId);
                                hashtagBridges.add(bridge);
                            }
                        }
                        
                        // Process agency bridges
                        if (item.getAgencyBridges() != null) {
                            for (BridgeFeedbackAgency bridge : item.getAgencyBridges()) {
                                bridge.setFeedbackId(feedbackId);
                                agencyBridges.add(bridge);
                            }
                        }
                        
                        break;
                    }
                }
            }
        }

        log.info("Saving {} feedback items, {} hashtag bridges, and {} agency bridges",
                feedbacks.size(), hashtagBridges.size(), agencyBridges.size());
        
        // Use batched operations for better performance
        if (!hashtagBridges.isEmpty()) {
            hashtagBridgeRepository.saveAll(hashtagBridges);
        }
        
        if (!agencyBridges.isEmpty()) {
            agencyBridgeRepository.saveAll(agencyBridges);
        }
    }
}
