package com.example.AppEnglish.modules.transcribe.dto;

public class SplitOptions {
    private Integer maxChars;
    private Integer splitOnSilenceGapMs;
    private boolean flushOnComma;
    private boolean flushOnAnyPunctuation;

    public SplitOptions() {
    }

    public SplitOptions(Integer maxChars, Integer splitOnSilenceGapMs, boolean flushOnComma, boolean flushOnAnyPunctuation) {
        this.maxChars = maxChars;
        this.splitOnSilenceGapMs = splitOnSilenceGapMs;
        this.flushOnComma = flushOnComma;
        this.flushOnAnyPunctuation = flushOnAnyPunctuation;
    }

    public static SplitOptions defaultOptions() {
        return new SplitOptions(140, 900, true, false);
    }

    public Integer getMaxChars() {
        return maxChars;
    }

    public void setMaxChars(Integer maxChars) {
        this.maxChars = maxChars;
    }

    public Integer getSplitOnSilenceGapMs() {
        return splitOnSilenceGapMs;
    }

    public void setSplitOnSilenceGapMs(Integer splitOnSilenceGapMs) {
        this.splitOnSilenceGapMs = splitOnSilenceGapMs;
    }

    public boolean isFlushOnComma() {
        return flushOnComma;
    }

    public void setFlushOnComma(boolean flushOnComma) {
        this.flushOnComma = flushOnComma;
    }

    public boolean isFlushOnAnyPunctuation() {
        return flushOnAnyPunctuation;
    }

    public void setFlushOnAnyPunctuation(boolean flushOnAnyPunctuation) {
        this.flushOnAnyPunctuation = flushOnAnyPunctuation;
    }
}
