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
}
