package gov.ihd.apiservice.batch;

import gov.ihd.apiservice.entity.BridgeFeedbackAgency;
import gov.ihd.apiservice.entity.BridgeFeedbackHashtag;
import gov.ihd.apiservice.entity.FactFeedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackBatchItem {
    private FactFeedback feedback;
    private List<BridgeFeedbackHashtag> hashtagBridges;
    private List<BridgeFeedbackAgency> agencyBridges;
}
