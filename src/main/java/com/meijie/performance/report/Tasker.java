package com.meijie.performance.report;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author meijie
 * @since 1.0
 */
public interface Tasker<T> {
    List<Metric> doTask(ExecutorService executorService, CoreConfig coreConfig) throws TaskException;
    void setListener(Listener listener);
    void prepare(List<T> dataList);
}
