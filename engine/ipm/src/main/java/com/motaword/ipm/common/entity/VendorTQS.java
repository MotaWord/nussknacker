package com.motaword.ipm.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class VendorTQS implements Serializable {
    // All fields need to be public and non-final for serializable Flink entities
    public Long vendorId;
    public Double score;

    /**
     * Required for POJO serialization by Flink
     */
    public VendorTQS() {
    }

    public VendorTQS(Long vendorId, Double score) {
        this.vendorId = vendorId;
        this.score = score;
    }

    @Override
    public String toString() {
        return "VendorTQS{" +
                "vendorId=" + vendorId +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorTQS vendorTQS = (VendorTQS) o;
        return vendorId.equals(vendorTQS.vendorId) &&
                Objects.equals(score, vendorTQS.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendorId, score);
    }
}
