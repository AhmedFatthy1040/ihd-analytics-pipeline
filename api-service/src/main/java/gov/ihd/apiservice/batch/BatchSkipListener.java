package gov.ihd.apiservice.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import gov.ihd.apiservice.model.FeedbackItem;
import gov.ihd.apiservice.batch.FeedbackBatchItem;

@Slf4j
public class BatchSkipListener implements SkipListener<FeedbackItem, FeedbackBatchItem> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("Skipped item during read: {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(FeedbackBatchItem item, Throwable t) {
        if (item != null && item.getFeedback() != null) {
            log.info("Skipped duplicate feedback item: tweet_id={}, date={}", 
                item.getFeedback().getTweetId(), 
                item.getFeedback().getCreatedDate());
        } else {
            log.warn("Skipped item during write: {}", t.getMessage());
        }
    }

    @Override
    public void onSkipInProcess(FeedbackItem item, Throwable t) {
        log.warn("Skipped item during process: {}", t.getMessage());
    }
}
