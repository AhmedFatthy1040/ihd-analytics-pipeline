package gov.ihd.apiservice.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FeedbackItem {
    private String platform;
    private String tweet_id;
    private String text;
    private LocalDateTime created_at;
    private String language;
    private List<String> hashtags;
    private List<String> mentions;
    private Metrics metrics;
    private Issue issue;
    private User user;
    
    // Additional getters for snake_case field names
    public String getTweet_id() {
        return tweet_id;
    }
    
    public LocalDateTime getCreated_at() {
        return created_at;
    }
    
    public User getUser() {
        return user;
    }
}
