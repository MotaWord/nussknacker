package com.motaword.ipm.common.entity.source;

import com.motaword.ipm.business.common.entity.AbstractMySQLQuerySource;
import com.motaword.ipm.business.common.entity.VendorProjectCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectCountSource extends AbstractMySQLQuerySource<VendorProjectCount> {
    private Logger LOG = LoggerFactory.getLogger(ProjectCountSource.class);

    public ProjectCountSource() {
        setWaitMs(100000);
    }

    public PreparedStatement buildQuery() throws SQLException {
        String sql = "" +
                "SELECT vendor_id, count(project_id) as project_count" +
                " FROM vendor_po " +
                " WHERE " +
                "   project_id IS NOT NULL " +
                "   AND deleted_at IS NULL " +
                "   AND source = 'project'" +
                " GROUP BY vendor_id;";

        statement = buildConnection().prepareStatement(sql);

        return statement;
    }

    @Override
    protected VendorProjectCount onEachResult(ResultSet resultSet) throws SQLException {
        // Next query will look for records from this date on
        // Also see the `equals` condition below.
        VendorProjectCount counts = new VendorProjectCount(
                resultSet.getLong("vendor_id"),
                resultSet.getInt("project_count")
        );

        LOG.debug("Collecting new vendor project count report: " + counts);

        return counts;
    }
}
