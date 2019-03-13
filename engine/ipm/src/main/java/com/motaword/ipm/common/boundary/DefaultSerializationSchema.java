package com.motaword.ipm.common.boundary;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.motaword.ipm.queue.entity.WorkerEvent;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class DefaultSerializationSchema implements SerializationSchema<WorkerEvent> {

    public DefaultSerializationSchema() {
        Jackson.getObjectMapper().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public byte[] serialize(WorkerEvent element) {
        return Jackson.toString(element).getBytes();
    }

}
