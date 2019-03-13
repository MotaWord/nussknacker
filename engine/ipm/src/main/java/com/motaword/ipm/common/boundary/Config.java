package com.motaword.ipm.common.boundary;

import lombok.Getter;
import org.apache.flink.api.java.utils.ParameterTool;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.motaword.ipm.business.log.controller.ElasticConnectionConfig.*;
import static com.motaword.ipm.business.queue.controller.RabbitMqConnectionConfig.*;
import static com.motaword.ipm.kernel.error.controller.ExceptionHandler.BUGSNAG_KEY;

@Getter
public class Config {

    private Map<String, String> defaultConfig = new HashMap<>();
    private ParameterTool configuration;

    public Config() {
        readEnvironment();
    }

    public Config(String[] args) {
        readEnvironment();
        configuration.mergeWith(ParameterTool.fromArgs(args));
    }

    public Config(File configFile) throws IOException {
        readEnvironment();
        configuration.mergeWith(ParameterTool.fromPropertiesFile(configFile));
    }

    private void readEnvironment() {
        defaultConfig.put(RABBIT_EXCHANGE, "default-exchange");
        defaultConfig.put(RABBIT_ROUTING, "events.#");
        defaultConfig.put(RABBIT_HOST, "localhost");
        defaultConfig.put(RABBIT_PORT, "5672");
        defaultConfig.put(RABBIT_USER, "ipm");
        defaultConfig.put(RABBIT_QUEUE, "ipm-events");
        defaultConfig.put(RABBIT_PASSWORD, "54VKsYM1Vh29sT");
        defaultConfig.put(RABBIT_VIRTUAL_HOST, "/");

        defaultConfig.put(ELASTIC_HOST, "localhost");
        defaultConfig.put(ELASTIC_PORT, "9200");
        defaultConfig.put(ELASTIC_SCHEME, "http");
        defaultConfig.put(ELASTIC_INDEX, "event-index");
        defaultConfig.put(ELASTIC_TYPE, "event-log");

        defaultConfig.put(BUGSNAG_KEY, "26482dba28ce68b79010ae371bfa60c2");

        Map<String, String> envConfig = new HashMap<>(System.getenv());
        defaultConfig.forEach(envConfig::putIfAbsent);

        configuration = ParameterTool.fromMap(envConfig);
    }

    public ParameterTool getConfiguration() {
        return configuration;
    }
}
