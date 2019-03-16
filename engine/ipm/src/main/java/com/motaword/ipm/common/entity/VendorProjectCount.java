package com.motaword.ipm.common.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class VendorProjectCount implements Serializable {
    // All fields need to be public and non-final for serializable Flink entities
    public Long vendorId;
    public Integer projectCount;

    /**
     * Required for POJO serialization by Flink
     */
    public VendorProjectCount() {
    }

    public VendorProjectCount(Long vendorId, Integer projectCount) {
        this.vendorId = vendorId;
        this.projectCount = projectCount;
    }

    @Override
    public String toString() {
        return "VendorProjectCount{" +
                "vendorId=" + vendorId +
                ", projectCount=" + projectCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorProjectCount that = (VendorProjectCount) o;
        return vendorId.equals(that.vendorId) &&
                projectCount.equals(that.projectCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendorId, projectCount);
    }

}
