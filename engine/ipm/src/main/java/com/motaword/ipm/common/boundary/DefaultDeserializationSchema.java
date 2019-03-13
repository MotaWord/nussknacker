package com.motaword.ipm.common.boundary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.motaword.ipm.queue.entity.WorkerEvent;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

import static java.time.LocalDateTime.now;

public class DefaultDeserializationSchema extends AbstractDeserializationSchema<WorkerEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDeserializationSchema.class);

    private static final String EVENT = "event";
    private static final String QUEUED_AT = "_queuedAt";
    private static final String PAYLOAD = "payload";
    public static final String UNKNOWN_EVENT_TYPE = "UNKNOWN_EVENT_TYPE";

    @Override
    public WorkerEvent deserialize(byte[] message) {
        WorkerEvent workerEvent = mapEvent(message);
        if (workerEvent == null) {
            try {
                workerEvent = mapEvent(Base64.getDecoder().decode(message));
            } catch (Exception e) {
                LOG.error("Error parsing event : ", e);
            }
        }
        if (workerEvent == null) {
            return mapUnknownEvent(message);
        }
        return workerEvent;
    }

    public WorkerEvent deserialize(String message) {
        WorkerEvent workerEvent = mapEvent(message);
        if (workerEvent == null) {
            try {
                workerEvent = mapEvent(Base64.getDecoder().decode(message));
            } catch (Exception e) {
                LOG.error("Error parsing event : ", e);
            }
        }
        if (workerEvent == null) {
            return mapUnknownEvent(message);
        }
        return workerEvent;
    }


    private WorkerEvent mapEvent(byte[] message) {
        try {
            return mapEvent(Jackson.toJsonNode(message));
        } catch (Exception e) {
            LOG.error("Error parsing event : ", e);
            return null;
        }
    }

    private WorkerEvent mapEvent(String message) {
        try {
            return mapEvent(Jackson.toJsonNode(message));
        } catch (Exception e) {
            LOG.error("Error parsing event : ", e);
            return null;
        }
    }

    private WorkerEvent mapEvent(JsonNode root) {
        try {
            String event = root.get(EVENT).textValue();
            long queuedAt = root.has(QUEUED_AT) ? root.get(QUEUED_AT).longValue() : now().toEpochSecond(ZoneOffset.UTC);
            JsonNode payload = root.get(PAYLOAD);
            return mapEvent(event, queuedAt, payload);
        } catch (Exception e) {
            LOG.error("Error parsing event : ", e);
            return null;
        }
    }

    private WorkerEvent mapEvent(String event, long queuedAt, JsonNode payload) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(queuedAt, 0, ZoneOffset.UTC);
        return new WorkerEvent(event, payload, dateTime);
    }

    private WorkerEvent mapUnknownEvent(byte[] message) {
        ObjectNode objectNode = Jackson.getObjectMapper().createObjectNode();
        objectNode.put(PAYLOAD, message);
        return mapEvent(UNKNOWN_EVENT_TYPE, now().toEpochSecond(ZoneOffset.UTC), objectNode);
    }

    private WorkerEvent mapUnknownEvent(String message) {
        ObjectNode objectNode = Jackson.getObjectMapper().createObjectNode();
        objectNode.put(PAYLOAD, message);
        return mapEvent(UNKNOWN_EVENT_TYPE, now().toEpochSecond(ZoneOffset.UTC), objectNode);
    }

}
