package com.motaword.ipm.queue.entity;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.motaword.ipm.queue.controller.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class WorkerEvent implements Serializable {

    private String event;
    private JsonNode payload;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime _queuedAt = LocalDateTime.now();

    public WorkerEvent() {
    }

    public WorkerEvent(String event, JsonNode payload, LocalDateTime _queuedAt) {
        this.event = event;
        this.payload = payload;
        this._queuedAt = _queuedAt;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }
}
