package gov.ihd.apiservice.model;

import lombok.Data;

@Data
public class Metrics {
    private int retweet_count;
    private int reply_count;
    private int like_count;
    private int quote_count;
    private int bookmark_count;
    private int impression_count;
    
    // Getters and setters for snake_case field names
    public int getRetweet_count() {
        return retweet_count;
    }
    
    public int getReply_count() {
        return reply_count;
    }
    
    public int getLike_count() {
        return like_count;
    }
    
    public int getQuote_count() {
        return quote_count;
    }
    
    public int getBookmark_count() {
        return bookmark_count;
    }
    
    public int getImpression_count() {
        return impression_count;
    }
}
