package gov.ihd.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_location", schema = "ihd_analytics")
public class DimLocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer locationId;
    
    @Column(name = "location_string", length = 255)
    private String locationString;
    
    @Column(name = "country", length = 100)
    private String country;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "region", length = 100)
    private String region;
    
    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }
    
    public String getLocationString() {
        return locationString;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getRegion() {
        return region;
    }
}
