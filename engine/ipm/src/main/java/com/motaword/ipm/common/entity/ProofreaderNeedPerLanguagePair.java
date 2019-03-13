package com.motaword.ipm.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class ProofreaderNeedPerLanguagePair {
    // All fields need to be public and non-final for serializable Flink entities
    public String sourceLanguage;
    public String targetLanguage;
    public String languagePair;
    public Long count;
    public Double score;

    /**
     * Required for POJO serialization by Flink
     */
    public ProofreaderNeedPerLanguagePair() {
    }

    public ProofreaderNeedPerLanguagePair(String sourceLanguage, String targetLanguage, Long count, Double score) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.languagePair = sourceLanguage + "~" + targetLanguage;
        this.count = count;
        this.score = score;
    }

    public String getLanguagePair() {
        if (languagePair == null) {
            languagePair = sourceLanguage + "~" + targetLanguage;
        }

        return languagePair;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProofreaderNeedPerLanguagePair that = (ProofreaderNeedPerLanguagePair) o;
        return Objects.equals(sourceLanguage, that.sourceLanguage) &&
                Objects.equals(targetLanguage, that.targetLanguage) &&
                Objects.equals(count, that.count) &&
                Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceLanguage, targetLanguage, count, score);
    }

    @Override
    public String toString() {
        return "ProofreaderNeedPerLanguagePair{" +
                "sourceLanguage='" + sourceLanguage + '\'' +
                ", targetLanguage='" + targetLanguage + '\'' +
                ", count=" + count +
                ", score=" + score +
                '}';
    }
}
