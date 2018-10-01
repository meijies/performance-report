package com.meijie.performance.report;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author meijie
 * @since 1.0
 */
@AllArgsConstructor
public class DefaultManager implements Manager {


    private static final Logger logger = LoggerFactory.getLogger(DefaultManager.class);

    private Tasker tasker;
    private Listener listener;
    private Reporter reporter;
    private CoreConfig config;

    private ThreadFactory buildThreadFactory() {

        return new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(this.getClass().getName() + "-%d")
                .setUncaughtExceptionHandler((thread, throwable) -> {
                    logger.error(thread.getName() + " occur error: ", throwable);
                    throw new RuntimeException(throwable);
                }).build();
    }


    @Override
    public void process() throws TaskException {
        ExecutorService executorService = Executors.newFixedThreadPool(config.getParcelSize(), buildThreadFactory());
        List<Metric> metrics = tasker.doTask(executorService, config);
        reporter.doReport(metrics);
    }
}
