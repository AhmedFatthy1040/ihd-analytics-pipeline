package gov.ihd.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "fact_feedback", schema = "ihd_analytics")
@IdClass(FactFeedback.FeedbackId.class)
public class FactFeedback {
    
    @Id
    @Column(name = "feedback_id")
    private Long feedbackId;
    
    @Id
    @Column(name = "created_date")
    private LocalDate createdDate;
    
    @Column(name = "tweet_id", nullable = false, length = 50)
    private String tweetId;
    
    @ManyToOne
    @JoinColumn(name = "time_id", nullable = false)
    private DimTime time;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private DimUser user;
    
    @ManyToOne
    @JoinColumn(name = "location_id")
    private DimLocation location;
    
    @ManyToOne
    @JoinColumn(name = "issue_id")
    private DimIssue issue;
    
    @Column(name = "platform", nullable = false, length = 50)
    private String platform;
    
    @Column(name = "text")
    private String text;
    
    @Column(name = "language", length = 10)
    private String language;
    
    @Column(name = "retweet_count")
    private Integer retweetCount = 0;
    
    @Column(name = "reply_count")
    private Integer replyCount = 0;
    
    @Column(name = "like_count")
    private Integer likeCount = 0;
    
    @Column(name = "quote_count")
    private Integer quoteCount = 0;
    
    @Column(name = "bookmark_count")
    private Integer bookmarkCount = 0;
    
    @Column(name = "impression_count")
    private Integer impressionCount = 0;
    
    public DimTime getTime() {
        return time;
    }
    
    public void setTime(DimTime time) {
        this.time = time;
    }
    
    public DimUser getUser() {
        return user;
    }
    
    public void setUser(DimUser user) {
        this.user = user;
    }
    
    public DimLocation getLocation() {
        return location;
    }
    
    public void setLocation(DimLocation location) {
        this.location = location;
    }
    
    public DimIssue getIssue() {
        return issue;
    }
    
    public void setIssue(DimIssue issue) {
        this.issue = issue;
    }
    
    public Long getFeedbackId() {
        return feedbackId;
    }
    
    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getTweetId() {
        return tweetId;
    }
    
    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }
    
    @Data
    public static class FeedbackId implements Serializable {
        private Long feedbackId;
        private LocalDate createdDate;
    }
}
