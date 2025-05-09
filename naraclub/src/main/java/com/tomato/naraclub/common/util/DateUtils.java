package com.tomato.naraclub.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    /**
     * startDate ~ endDate 기간 중, endDate까지 남은 일수의 퍼센테이지를 구합니다.
     *
     * @param startDate 기간 시작
     * @param endDate   기간 종료
     * @return 남은 일수 비율 (0.0 ~ 100.0)
     */
    public static double remainingDaysPercentage(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();

        // 1) 총 기간(초)
        long totalSeconds = ChronoUnit.SECONDS.between(startDate, endDate);
        if (totalSeconds <= 0) {
            return 0.0; // 이미 종료된 경우
        }

        // 2) 남은 기간(초)
        long remainingSeconds;
        if (now.isBefore(startDate)) {
            // 아직 시작 전이면 100%
            return 100.0;
        } else if (now.isAfter(endDate)) {
            // 이미 종료된 경우
            return 0.0;
        } else {
            remainingSeconds = ChronoUnit.SECONDS.between(now, endDate);
        }

        // 3) 퍼센테이지 계산
        double percent = (double) remainingSeconds * 100.0 / totalSeconds;

        // 4) 남은일 기준 몇퍼 남았는지
        return Math.round(percent * 100.0) / 100.0;
    }

    public static double progressDaysPercentage(LocalDateTime start, LocalDateTime end) {
        LocalDateTime today = LocalDateTime.now();

        if (today.isBefore(start)) {
            // 아직 시작 전이면 0%
            return 0.0;
        }
        if (!today.isBefore(end)) {
            // 종료일 이후면 100%
            return 100.0;
        }

        long totalDays = ChronoUnit.DAYS.between(start, end);
        long elapsedDays = ChronoUnit.DAYS.between(start, today);

        double pct = elapsedDays * 100.0 / totalDays;

        return Math.round(pct * 100.0) / 100.0;
    }

    public static double progressMillsPercentage(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(start)) {
            return 0.0;
        }
        if (!now.isBefore(end)) {
            return 100.0;
        }

        // 전체 구간과 경과 구간을 밀리초 단위로 계산
        long totalMillis = Duration.between(start, end).toMillis();
        long elapsedMillis = Duration.between(start, now).toMillis();

        double pct = elapsedMillis * 100.0 / totalMillis;
        // 소수점 둘째 자리에서 반올림
        return Math.round(pct * 100.0) / 100.0;
    }
}
