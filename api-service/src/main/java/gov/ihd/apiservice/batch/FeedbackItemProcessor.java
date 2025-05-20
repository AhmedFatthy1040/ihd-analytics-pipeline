package gov.ihd.apiservice.batch;

import gov.ihd.apiservice.entity.*;
import gov.ihd.apiservice.model.FeedbackItem;
import gov.ihd.apiservice.repository.*;
import gov.ihd.apiservice.service.ErrorLogService;
import gov.ihd.apiservice.service.TimeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackItemProcessor implements ItemProcessor<FeedbackItem, FeedbackBatchItem> {

    private final DimTimeRepository timeRepository;
    private final DimUserRepository userRepository;
    private final DimLocationRepository locationRepository;
    private final DimIssueRepository issueRepository;
    private final DimAgencyRepository agencyRepository;
    private final DimHashtagRepository hashtagRepository;
    private final TimeService timeService;
    private final FactFeedbackRepository feedbackRepository;
    private final EntityManager entityManager;
    private final ErrorLogService errorLogService;
    
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:100}")
    private int batchSize;
    
    private String jobId;
    private long feedbackIdCounter = 0;
    
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        this.jobId = jobExecution.getJobParameters().getString("jobId");
        
        // Get the next value from the sequence
        Query query = entityManager.createNativeQuery("SELECT nextval('ihd_analytics.seq_feedback_id')");
        this.feedbackIdCounter = ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public FeedbackBatchItem process(FeedbackItem item) {
        try {
            LocalDate createdDate = item.getCreated_at().toLocalDate();
            
            // Find or create time dimension
            DimTime time = timeRepository.findByFullDate(createdDate)
                    .orElseGet(() -> timeService.createTimeForDate(createdDate));
            
            // Find or create user dimension
            DimUser user = userRepository.findById(item.getUser().getUser_id())
                    .orElseGet(() -> {
                        DimUser newUser = new DimUser();
                        newUser.setUserId(item.getUser().getUser_id());
                        newUser.setUsername(item.getUser().getUsername());
                        newUser.setCreatedAt(item.getUser().getCreated_at());
                        newUser.setFollowersCount(item.getUser().getFollowers_count());
                        newUser.setFollowingCount(item.getUser().getFollowing_count());
                        newUser.setTweetCount(item.getUser().getTweet_count());
                        newUser.setListedCount(item.getUser().getListed_count());
                        return userRepository.save(newUser);
                    });
            
            // Create the fact record
            FactFeedback feedback = new FactFeedback();
            feedback.setFeedbackId(feedbackIdCounter++);  // Set and increment the counter
            feedback.setTweetId(item.getTweet_id());
            feedback.setTime(time);
            feedback.setUser(user);
            feedback.setPlatform(item.getPlatform());
            feedback.setText(item.getText());
            feedback.setLanguage(item.getLanguage());
            feedback.setRetweetCount(item.getMetrics().getRetweet_count());
            feedback.setReplyCount(item.getMetrics().getReply_count());
            feedback.setLikeCount(item.getMetrics().getLike_count());
            feedback.setQuoteCount(item.getMetrics().getQuote_count());
            feedback.setBookmarkCount(item.getMetrics().getBookmark_count());
            feedback.setImpressionCount(item.getMetrics().getImpression_count());
            feedback.setCreatedDate(createdDate);
            
            // Process location if available from user.location_string
            if (item.getUser() != null && item.getUser().getLocation_string() != null) {
                String locationString = item.getUser().getLocation_string();
                
                // Try to extract city and country from location string (usually in format "City, Country")
                final String[] cityHolder = new String[1];
                final String[] countryHolder = new String[1];
                final String[] regionHolder = new String[1];
                
                if (locationString.contains(",")) {
                    String[] parts = locationString.split(",", 2);
                    cityHolder[0] = parts[0].trim();
                    countryHolder[0] = parts[1].trim();
                } else {
                    // If no comma, use the whole string as the country
                    countryHolder[0] = locationString.trim();
                    cityHolder[0] = null;
                }
                regionHolder[0] = null;
                
                DimLocation location = locationRepository.findByCountryAndCityAndRegion(
                        countryHolder[0], 
                        cityHolder[0],
                        regionHolder[0]
                ).orElseGet(() -> {
                    DimLocation newLocation = new DimLocation();
                    newLocation.setLocationString(locationString);
                    newLocation.setCountry(countryHolder[0]);
                    newLocation.setCity(cityHolder[0]);
                    newLocation.setRegion(regionHolder[0]);
                    return locationRepository.save(newLocation);
                });
                feedback.setLocation(location);
            }
            
            // Process issue if available
            if (item.getIssue() != null) {
                DimIssue issue = issueRepository.findById(item.getIssue().getIssue_id())
                        .orElseGet(() -> {
                            DimIssue newIssue = new DimIssue();
                            newIssue.setIssueId(item.getIssue().getIssue_id());
                            newIssue.setIssueClassKey(item.getIssue().getIssue_class().getIssue_class_key());
                            newIssue.setIssueClassCode(item.getIssue().getIssue_class().getIssue_class_code());
                            return issueRepository.save(newIssue);
                        });
                feedback.setIssue(issue);
            }
            
            // Process hashtags
            List<BridgeFeedbackHashtag> hashtagBridges = new ArrayList<>();
            if (item.getHashtags() != null && !item.getHashtags().isEmpty()) {
                for (String hashtagText : item.getHashtags()) {
                    // Remove # character if present
                    String cleanHashtag = hashtagText.startsWith("#") ? hashtagText.substring(1) : hashtagText;
                    
                    DimHashtag hashtag = hashtagRepository.findByHashtagText(cleanHashtag)
                            .orElseGet(() -> {
                                DimHashtag newHashtag = new DimHashtag();
                                newHashtag.setHashtagText(cleanHashtag);
                                return hashtagRepository.save(newHashtag);
                            });
                    
                    BridgeFeedbackHashtag bridge = new BridgeFeedbackHashtag();
                    bridge.setHashtagId(hashtag.getHashtagId());
                    bridge.setCreatedDate(createdDate);
                    hashtagBridges.add(bridge);
                }
            }
            
            // Process mentions (agencies)
            List<BridgeFeedbackAgency> agencyBridges = new ArrayList<>();
            if (item.getMentions() != null && !item.getMentions().isEmpty()) {
                for (String mention : item.getMentions()) {
                    // Remove @ character if present
                    String agencyAccount = mention.startsWith("@") ? mention.substring(1) : mention;
                    
                    DimAgency agency = agencyRepository.findByAgencyAccount(agencyAccount)
                            .orElseGet(() -> {
                                DimAgency newAgency = new DimAgency();
                                newAgency.setAgencyAccount(agencyAccount);
                                newAgency.setAgencyName(agencyAccount); // Use account as name until enriched
                                return agencyRepository.save(newAgency);
                            });
                    
                    BridgeFeedbackAgency bridge = new BridgeFeedbackAgency();
                    bridge.setAgencyId(agency.getAgencyId());
                    bridge.setCreatedDate(createdDate);
                    agencyBridges.add(bridge);
                }
            }
            
            return new FeedbackBatchItem(feedback, hashtagBridges, agencyBridges);
            
        } catch (Exception e) {
            log.error("Error processing feedback item: {}", e.getMessage(), e);
            
            // Log the error for later retrieval
            if (jobId != null) {
                errorLogService.logError(
                    jobId, 
                    "Error processing feedback item: " + e.getMessage(),
                    e,
                    item
                );
            }
            
            return null;
        }
    }
}
