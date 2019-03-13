package com.motaword.ipm.common.entity;

import com.motaword.ipm.business.common.boundary.Config;
import com.motaword.ipm.business.queue.entity.WorkerEvent;
import lombok.Getter;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

@Getter
public abstract class AbstractJob implements Serializable {

    protected Logger LOG = LoggerFactory.getLogger(AbstractJob.class);

    protected StreamExecutionEnvironment environment;
    protected ExecutionEnvironment batchEnvironment;
    protected DataStream<WorkerEvent> dataStream;
    protected ParameterTool parameterTool = new Config().getConfiguration();

    public void setEnvironment(StreamExecutionEnvironment environment) {
        this.environment = environment;
    }

    public void setBatchEnvironment(ExecutionEnvironment batchEnvironment) {
        this.batchEnvironment = batchEnvironment;
    }

    public void addSource(SourceFunction<WorkerEvent> source, String name) {
        dataStream = environment.addSource(source, name);
    }

    public void addSink(SinkFunction<WorkerEvent> sink, String name) {
        DataStreamSink<WorkerEvent> dataStreamSink = dataStream.addSink(sink);
        dataStreamSink.name(name);
    }

    public DataStream<WorkerEvent> getDataStream() {
        return dataStream;
    }
}
