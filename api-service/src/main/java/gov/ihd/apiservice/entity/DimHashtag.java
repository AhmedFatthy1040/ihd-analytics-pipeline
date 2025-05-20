package gov.ihd.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_hashtag", schema = "ihd_analytics")
public class DimHashtag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Integer hashtagId;
    
    @Column(name = "hashtag_text", nullable = false, length = 100, unique = true)
    private String hashtagText;
}
