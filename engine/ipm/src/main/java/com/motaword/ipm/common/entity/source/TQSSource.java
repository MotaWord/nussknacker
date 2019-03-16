package com.motaword.ipm.common.entity.source;

import com.motaword.ipm.business.common.entity.AbstractMySQLQuerySource;
import com.motaword.ipm.business.common.entity.VendorTQS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TQSSource extends AbstractMySQLQuerySource<VendorTQS> {
    private Logger LOG = LoggerFactory.getLogger(TQSSource.class);

    public TQSSource() {}

    public PreparedStatement buildQuery() throws SQLException {
        String sql = "SELECT vendor_id, AVG(score) as score FROM vendor_scores a GROUP BY vendor_id;";

        return buildConnection().prepareStatement(sql);
    }

    @Override
    protected VendorTQS onEachResult(ResultSet resultSet) throws SQLException {
        // Next query will look for records from this date on
        // Also see the `equals` condition below.
        VendorTQS tqs = new VendorTQS(
                resultSet.getLong("vendor_id"),
                resultSet.getDouble("score")
        );

        LOG.debug("Collecting new Vendor TQS: " + tqs);

        return tqs;
    }
}
