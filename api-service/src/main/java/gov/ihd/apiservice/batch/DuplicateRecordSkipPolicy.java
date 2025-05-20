package gov.ihd.apiservice.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
public class DuplicateRecordSkipPolicy implements SkipPolicy {
    
    @Override
    public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException {
        if (throwable.getMessage() != null && throwable.getMessage().contains("duplicate key value violates unique constraint")) {
            log.debug("Skipping duplicate record. Skip count: {}", skipCount);
            return true;
        }
        
        if (throwable instanceof DataIntegrityViolationException) {
            log.debug("Skipping record due to data integrity violation. Skip count: {}", skipCount);
            return true;
        }
        
        return false;
    }
}
