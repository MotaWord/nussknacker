package com.motaword.ipm.common.controller;

import com.motaword.ipm.common.boundary.DefaultDeserializationSchema;
import com.motaword.ipm.queue.entity.WorkerEvent;
import pl.touk.nussknacker.engine.api.MethodToInvoke;
import pl.touk.nussknacker.engine.api.ParamName;
import pl.touk.nussknacker.engine.api.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ToWorkerEvent extends Service {
    @MethodToInvoke
    public CompletableFuture<WorkerEvent> invoke(@ParamName("message") String message, Executor executor) {
        DefaultDeserializationSchema ser = new DefaultDeserializationSchema();
        return CompletableFuture.supplyAsync(() -> ser.deserialize(message), executor);
    }
}
