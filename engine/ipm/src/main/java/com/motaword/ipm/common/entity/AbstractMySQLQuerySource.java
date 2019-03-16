package com.motaword.ipm.common.entity;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

abstract public class AbstractMySQLQuerySource<OUT> extends RichSourceFunction<OUT> {
    private Logger LOG = LoggerFactory.getLogger(AbstractMySQLQuerySource.class);
    /**
     * How often do we want to check DB for updated records?
     *
     * todo determine better MS, possibly much longer.
     */
    @SuppressWarnings("FieldCanBeLocal")
    protected int RUN_WAIT_MS = 100000;
    @SuppressWarnings("WeakerAccess")
    protected volatile boolean isRunning = true;
    protected PreparedStatement statement;
    @SuppressWarnings("WeakerAccess")
    protected Connection connection;

    protected Connection buildConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:mysql://database:3306/motaword", "root", "password");
        }

        return connection;
    }

    protected int getWaitMs() {
        return RUN_WAIT_MS;
    }

    protected void setWaitMs(int waitMs) {
        RUN_WAIT_MS = waitMs;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void cancel() {
        try {
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        isRunning = false;

        if (connection != null) {
            connection.close();
        }
        if (statement != null) {
            statement.close();
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run(SourceContext<OUT> sourceContext) throws Exception {
        while(isRunning()) {
            beforeEachRun();

            statement = buildQuery();
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                OUT entry = onEachResult(resultSet);
                if (entry != null) {
                    LOG.info(String.valueOf(entry));
                    try {
                        sourceContext.collect(entry);
                    } catch(RuntimeException r) {
                        LOG.error("Caught error: " + r.getMessage());
                    }
                }
            }

            Thread.sleep(RUN_WAIT_MS);
        }
    }

    protected abstract PreparedStatement buildQuery() throws SQLException;

    // to override
    @SuppressWarnings("WeakerAccess")
    protected void beforeEachRun() {

    }

    protected abstract OUT onEachResult(ResultSet resultSet) throws SQLException;
}
