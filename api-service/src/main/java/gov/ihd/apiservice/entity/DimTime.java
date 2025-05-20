package gov.ihd.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "dim_time", schema = "ihd_analytics")
public class DimTime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_id")
    private Integer timeId;
    
    @Column(name = "full_date", nullable = false, unique = true)
    private LocalDate fullDate;
    
    @Column(name = "year", nullable = false)
    private Integer year;
    
    @Column(name = "quarter", nullable = false)
    private Integer quarter;
    
    @Column(name = "month", nullable = false)
    private Integer month;
    
    @Column(name = "month_name", nullable = false)
    private String monthName;
    
    @Column(name = "day", nullable = false)
    private Integer day;
    
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;
    
    @Column(name = "day_name", nullable = false)
    private String dayName;
    
    @Column(name = "week_of_year", nullable = false)
    private Integer weekOfYear;
    
    @Column(name = "is_weekend", nullable = false)
    private Boolean isWeekend;
    
    @Column(name = "is_holiday", nullable = false)
    private Boolean isHoliday;

    public void setFullDate(LocalDate fullDate) {
        this.fullDate = fullDate;
    }
    
    public LocalDate getFullDate() {
        return fullDate;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setQuarter(Integer quarter) {
        this.quarter = quarter;
    }
    
    public Integer getQuarter() {
        return quarter;
    }
    
    public void setMonth(Integer month) {
        this.month = month;
    }
    
    public Integer getMonth() {
        return month;
    }
    
    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }
    
    public String getMonthName() {
        return monthName;
    }
    
    public void setDay(Integer day) {
        this.day = day;
    }
    
    public Integer getDay() {
        return day;
    }
    
    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public Integer getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayName(String dayName) {
        this.dayName = dayName;
    }
    
    public String getDayName() {
        return dayName;
    }
    
    public void setWeekOfYear(Integer weekOfYear) {
        this.weekOfYear = weekOfYear;
    }
    
    public Integer getWeekOfYear() {
        return weekOfYear;
    }
    
    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }
    
    public Boolean getIsWeekend() {
        return isWeekend;
    }
    
    public void setIsHoliday(Boolean isHoliday) {
        this.isHoliday = isHoliday;
    }
    
    public Boolean getIsHoliday() {
        return isHoliday;
    }
}
