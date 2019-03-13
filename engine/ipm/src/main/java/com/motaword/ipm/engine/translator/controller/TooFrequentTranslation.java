package com.motaword.ipm.engine.translator.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.motaword.ipm.common.boundary.Jackson;
import com.motaword.ipm.queue.entity.WorkerEvent;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import pl.touk.nussknacker.engine.api.*;
import pl.touk.nussknacker.engine.flink.api.process.FlinkCustomNodeContext;
import pl.touk.nussknacker.engine.flink.api.process.FlinkCustomStreamTransformation;
import pl.touk.nussknacker.engine.flink.javaapi.process.JavaFlinkCustomStreamTransformation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"Convert2Lambda"})
public class TooFrequentTranslation extends CustomStreamTransformer {

    private static final String FILTER_STATUS = "status";
    private static final String FILTER_EVENT = "SAVE_FINISHED";
    private static final String FILTER_TRANSLATED = "translated";
    private static final String KEY_FIELD_USER = "userId";

    private static final int times = 5;
    private static final Time period = Time.seconds(30);

    private static Pattern definition = Pattern.<WorkerEvent>begin("start").times(times).within(period);

    private final FilterFunction<ValueWithContext<WorkerEvent>> eventFilter = new FilterFunction<ValueWithContext<WorkerEvent>>() {
        @Override
        public boolean filter(ValueWithContext<WorkerEvent> value) {
            WorkerEvent event = value.value();
            return event != null
                    && FILTER_EVENT.equalsIgnoreCase(event.getEvent())
                    && FILTER_TRANSLATED.equalsIgnoreCase(event.getPayload().get(FILTER_STATUS).textValue());
        }
    };

    private final KeySelector<ValueWithContext<WorkerEvent>, Long> keySelector = new KeySelector<ValueWithContext<WorkerEvent>, Long>() {
        @Override
        public Long getKey(ValueWithContext<WorkerEvent> value) {
            WorkerEvent event = value.value();
            if (event == null) {
                return null;
            }

            return event.getPayload().get(KEY_FIELD_USER).longValue();
        }
    };

    @MethodToInvoke
    public FlinkCustomStreamTransformation execute(@ParamName("event") LazyParameter<WorkerEvent> event) {
        return JavaFlinkCustomStreamTransformation.apply((DataStream<Context> start, FlinkCustomNodeContext ctx) -> {
            DataStream<ValueWithContext<WorkerEvent>> filteredEvents;

            filteredEvents = start.map(ctx.nodeServices().lazyMapFunction(event))
                    .filter(eventFilter)
                    .keyBy(keySelector);

            //noinspection unchecked
            PatternStream<ValueWithContext<WorkerEvent>> patternStream = CEP.pattern(filteredEvents, definition);

            return patternStream.select(new PatternSelectFunction<ValueWithContext<WorkerEvent>, ValueWithContext<Object>>() {
                @Override
                public ValueWithContext<Object> select(Map<String, List<ValueWithContext<WorkerEvent>>> pattern) {
                    ValueWithContext<WorkerEvent> value = pattern.get("start").get(0);
                    JsonNode userId = value.value().getPayload().get(KEY_FIELD_USER);
                    ObjectNode payload = Jackson.getObjectMapper().createObjectNode();
                    payload.put(KEY_FIELD_USER, userId.longValue());
                    return (new ValueWithContext<>(new WorkerEvent("TooFrequentTranslation", payload, LocalDateTime.now()), value.context()));
                }
            });
        });
    }

}
