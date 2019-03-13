package com.motaword.ipm.common.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class UpgradeScore implements Serializable {
    // All fields need to be public and non-final for serializable Flink entities
    public Long vendorId;
    public Double score;

    /**
     * Required for POJO serialization by Flink
     */
    public UpgradeScore() {
    }

    @Override
    public String toString() {
        return "UpgradeScore{" +
                "vendorId=" + vendorId +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpgradeScore that = (UpgradeScore) o;
        return Objects.equals(vendorId, that.vendorId) &&
                Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendorId, score);
    }
}
