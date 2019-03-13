package com.motaword.ipm.common.entity.source;

import com.motaword.ipm.business.common.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResponsivitySource extends AbstractMySQLQuerySource<VendorResponsivity> {
    private Logger LOG = LoggerFactory.getLogger(ResponsivitySource.class);

    public ResponsivitySource() {
        setWaitMs(100000);
    }

    public PreparedStatement buildQuery() throws SQLException {
        String sql = "" +
                " SELECT *, 100 * (((works * 1) + (entries * 0.3) + (invites * 0)) / invites) as score" +
                " FROM (" +
                "   SELECT vrs.vendor_id, SUM(vrs.works) as works,SUM(vrs.entries) as entries, SUM(vrs.invites) as invites" +
                "   FROM vendor_responsivity_score vrs" +
                "   WHERE vrs.calculation_date >= (NOW() - INTERVAL 2 MONTH)" +
                "   GROUP BY vrs.vendor_id" +
                " ) scores;";

        statement = buildConnection().prepareStatement(sql);

        return statement;
    }

    @Override
    protected VendorResponsivity onEachResult(ResultSet resultSet) throws SQLException {
        // Next query will look for records from this date on
        // Also see the `equals` condition below.
        VendorResponsivity counts = new VendorResponsivity(
                resultSet.getLong("vendor_id"),
                resultSet.getLong("invites"),
                resultSet.getLong("entries"),
                resultSet.getLong("works"),
                resultSet.getDouble("score")
        );

        LOG.debug("Collecting new VRS report: " + counts);

        return counts;
    }


}
