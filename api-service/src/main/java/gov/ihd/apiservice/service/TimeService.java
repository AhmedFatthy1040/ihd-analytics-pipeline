package gov.ihd.apiservice.service;

import gov.ihd.apiservice.entity.DimTime;
import gov.ihd.apiservice.repository.DimTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeService {

    private final DimTimeRepository timeRepository;

    @Transactional
    public DimTime createTimeForDate(LocalDate date) {
        return timeRepository.findByFullDate(date)
                .orElseGet(() -> {
                    DimTime time = new DimTime();
                    time.setFullDate(date);
                    time.setYear(date.getYear());
                    time.setQuarter((date.getMonthValue() - 1) / 3 + 1);
                    time.setMonth(date.getMonthValue());
                    time.setMonthName(date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                    time.setDay(date.getDayOfMonth());
                    time.setDayOfWeek(date.getDayOfWeek().getValue());
                    time.setDayName(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                    time.setWeekOfYear(date.get(WeekFields.of(Locale.getDefault()).weekOfYear()));
                    time.setIsWeekend(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
                    time.setIsHoliday(false); // Would require a holiday calendar to set properly
                    
                    return timeRepository.save(time);
                });
    }
}
