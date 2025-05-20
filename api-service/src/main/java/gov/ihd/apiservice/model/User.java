package gov.ihd.apiservice.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private String user_id;
    private String username;
    private LocalDateTime created_at;
    private int followers_count;
    private int following_count;
    private int tweet_count;
    private int listed_count;
    private String location_string;
    
    // Getters for snake_case field names
    public String getUser_id() {
        return user_id;
    }
    
    public LocalDateTime getCreated_at() {
        return created_at;
    }
    
    public int getFollowers_count() {
        return followers_count;
    }
    
    public int getFollowing_count() {
        return following_count;
    }
    
    public int getTweet_count() {
        return tweet_count;
    }
    
    public int getListed_count() {
        return listed_count;
    }
    
    public String getLocation_string() {
        return location_string;
    }
}
