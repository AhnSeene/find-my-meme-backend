package com.findmymeme.memepost.dto;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Getter
public enum Period {
    ALL, WEEK, MONTH, YEAR;

    public LocalDateTime getStartDateTime() {
        return switch (this) {
            case WEEK -> LocalDateTime.now().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
            case MONTH -> LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
            case YEAR -> LocalDateTime.now().withDayOfYear(1).toLocalDate().atStartOfDay();
            default -> throw new FindMyMemeException(ErrorCode.INVALID_PERIOD);
        };
    }

    public LocalDateTime getEndDateTime() {
        return switch (this) {
            case WEEK -> LocalDateTime.now().with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59);
            case MONTH -> LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(23, 59, 59);
            case YEAR -> LocalDateTime.now().with(TemporalAdjusters.lastDayOfYear()).toLocalDate().atTime(23, 59, 59);
            default -> throw new FindMyMemeException(ErrorCode.INVALID_PERIOD);
        };
    }
}
