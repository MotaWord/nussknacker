package com.motaword.ipm.common.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class VendorResponsivity implements Serializable {
    // All fields need to be public and non-final for serializable Flink entities
    public Long vendorId;
    public Long invites;
    public Long entries;
    public Long works;
    public Double score;

    /**
     * Required for POJO serialization by Flink
     */
    public VendorResponsivity() {
    }

    public VendorResponsivity(Long vendorId, Long invites, Long entries, Long works, Double score) {
        this.vendorId = vendorId;
        this.invites = invites;
        this.entries = entries;
        this.works = works;
        this.score = score;
    }

    @Override
    public String toString() {
        return "VendorResponsivity{" +
                "vendorId=" + vendorId +
                ", invites=" + invites +
                ", entries=" + entries +
                ", works=" + works +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorResponsivity that = (VendorResponsivity) o;
        return vendorId.equals(that.vendorId) &&
                invites.equals(that.invites) &&
                entries.equals(that.entries) &&
                works.equals(that.works) &&
                Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendorId, invites, entries, works, score);
    }
}
