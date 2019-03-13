package com.motaword.ipm.engine;

import com.motaword.ipm.engine.translator.controller.TooFrequentTranslation;
import com.typesafe.config.Config;
import pl.touk.nussknacker.engine.api.CustomStreamTransformer;
import pl.touk.nussknacker.engine.api.ProcessListener;
import pl.touk.nussknacker.engine.api.Service;
import pl.touk.nussknacker.engine.api.exception.ExceptionHandlerFactory;
import pl.touk.nussknacker.engine.api.process.SingleNodeConfig$;
import pl.touk.nussknacker.engine.api.process.SinkFactory;
import pl.touk.nussknacker.engine.api.process.SourceFactory;
import pl.touk.nussknacker.engine.api.process.WithCategories;
import pl.touk.nussknacker.engine.api.signal.ProcessSignalSender;
import pl.touk.nussknacker.engine.example.LoggingExceptionHandlerFactory;
import pl.touk.nussknacker.engine.javaapi.process.ExpressionConfig;
import pl.touk.nussknacker.engine.javaapi.process.ProcessConfigCreator;
import scala.collection.JavaConversions;

import java.util.*;

public class IPMProcessConfigCreator implements ProcessConfigCreator {

    @Override
    public Map<String, WithCategories<Service>> services(Config config) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, WithCategories<SourceFactory<?>>> sourceFactories(Config config) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, WithCategories<SinkFactory>> sinkFactories(Config config) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, WithCategories<CustomStreamTransformer>> customStreamTransformers(Config config) {
        Map<String, WithCategories<CustomStreamTransformer>> m = new HashMap<>();
        m.put("Translator.TooFrequentTranslation", all(new TooFrequentTranslation(), "Translator"));
        return m;
    }

    @Override
    public Map<String, WithCategories<ProcessSignalSender>> signals(Config config) {
        return Collections.emptyMap();
    }

    @Override
    public Collection<ProcessListener> listeners(Config config) {
        return Collections.emptyList();
    }

    @Override
    public ExceptionHandlerFactory exceptionHandlerFactory(Config config) {
        return new LoggingExceptionHandlerFactory(config);
    }

    @Override
    public ExpressionConfig expressionConfig(Config config) {
        return new ExpressionConfig(Collections.emptyMap(), Collections.emptyList());
    }

    @Override
    public Map<String, String> buildInfo() {
        return Collections.emptyMap();
    }

    private <T> WithCategories<T> all(T value, ArrayList<String> categories) {
        if (categories == null) {
            categories = new ArrayList<>();
        }

        return new WithCategories<>(
                value,
                JavaConversions.collectionAsScalaIterable(categories).toList(),
                SingleNodeConfig$.MODULE$.zero());
    }

    private <T> WithCategories<T> all(T value, String category) {
        if (category == null) {
            category = "Generic";
        }

        ArrayList<String> categories = new ArrayList<>();
        categories.add(category);

        return all(value, categories);
    }

}
