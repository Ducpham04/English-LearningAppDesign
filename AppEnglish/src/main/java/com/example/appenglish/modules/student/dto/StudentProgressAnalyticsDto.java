package com.example.appenglish.modules.student.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentProgressAnalyticsDto {

  private String range; // week|month|year
  private List<SeriesPointDto> xpSeries;
  private List<SeriesPointDto> minutesSeries;
  private List<AchievementDto> achievements;

  @Data
  @Builder
  public static class SeriesPointDto {
    private String label; // e.g. Mon, Tue, 2026-03-26
    private long value;
  }

  @Data
  @Builder
  public static class AchievementDto {
    private String title;
    private String type; // streak|level|lessons
  }
}

