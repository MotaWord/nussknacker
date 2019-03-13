package com.motaword.ipm.common.entity.source;

import com.motaword.ipm.business.common.entity.ProofreaderNeedPerLanguagePair;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

public class ProofreaderNeedSource extends RichSourceFunction<ProofreaderNeedPerLanguagePair> {
    private volatile boolean isRunning = true;

    public ProofreaderNeedSource() {}

    @Override
    public void run(SourceContext<ProofreaderNeedPerLanguagePair> sourceContext) throws Exception {
        while(isRunning()) {
            Thread.sleep(10000);

            sourceContext.collect(new ProofreaderNeedPerLanguagePair("en-US", "fr", 1L, 1.0));
        }
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
    }

    private boolean isRunning() {
        return isRunning;
    }
}
