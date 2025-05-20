package gov.ihd.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "dim_user", schema = "ihd_analytics")
public class DimUser {
    
    @Id
    @Column(name = "user_id", length = 50)
    private String userId;
    
    @Column(name = "username", nullable = false, length = 100)
    private String username;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "followers_count")
    private Integer followersCount;
    
    @Column(name = "following_count")
    private Integer followingCount;
    
    @Column(name = "tweet_count")
    private Integer tweetCount;
    
    @Column(name = "listed_count")
    private Integer listedCount;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "url")
    private String url;
    
    @Column(name = "protected")
    private Boolean isProtected;
    
    @Column(name = "verified")
    private Boolean isVerified;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "language")
    private String language;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }
    
    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
    
    public void setTweetCount(int tweetCount) {
        this.tweetCount = tweetCount;
    }
    
    public void setListedCount(int listedCount) {
        this.listedCount = listedCount;
    }
}
