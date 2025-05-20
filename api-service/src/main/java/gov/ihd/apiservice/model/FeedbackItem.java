package gov.ihd.apiservice.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FeedbackItem {
    private String tweet_id;
    private String text;
    private LocalDateTime created_at;
    private String platform;
    private String language;
    private List<String> hashtags;
    private List<String> mentions;
    private Metrics metrics;
    private Issue issue;
    private User user;

    @Data
    public static class User {
        private String user_id;
        private String username;
        private LocalDateTime created_at;
        private int followers_count;
        private int following_count;
        private int tweet_count;
        private int listed_count;
        private String location_string;
    }

    @Data
    public static class Metrics {
        private int retweet_count;
        private int reply_count;
        private int like_count;
        private int quote_count;
        private int bookmark_count;
        private int impression_count;
    }

    @Data
    public static class Issue {
        private Integer issue_id;
        private IssueClass issue_class;
    }

    @Data
    public static class IssueClass {
        private int issue_class_key;
        private String issue_class_code;
    }

    // Getter method for tweetId to maintain compatibility
    public String getTweetId() {
        return tweet_id;
    }

    // Getter method for createdDate to maintain compatibility
    public String getCreatedDate() {
        return created_at != null ? created_at.toString() : null;
    }
}
