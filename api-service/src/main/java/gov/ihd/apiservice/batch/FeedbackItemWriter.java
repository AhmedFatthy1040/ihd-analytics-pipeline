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

        for (FeedbackBatchItem item : items) {
            if (item != null && item.getFeedback() != null) {
                feedbacks.add(item.getFeedback());
                
                // Save feedback first to get generated ID
                FactFeedback savedFeedback = feedbackRepository.save(item.getFeedback());
                Long feedbackId = savedFeedback.getFeedbackId();
                
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
            }
        }

        log.info("Saving {} feedback items, {} hashtag bridges, and {} agency bridges",
                feedbacks.size(), hashtagBridges.size(), agencyBridges.size());
        
        // Save all the entities
        if (!hashtagBridges.isEmpty()) {
            hashtagBridgeRepository.saveAll(hashtagBridges);
        }
        
        if (!agencyBridges.isEmpty()) {
            agencyBridgeRepository.saveAll(agencyBridges);
        }
    }
}
