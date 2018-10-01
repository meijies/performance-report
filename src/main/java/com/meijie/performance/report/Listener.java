package com.meijie.performance.report;

import java.util.List;

/**
 * @author meijie
 * @since 1.0
 *
 */
public interface Listener {

    void onStart();

    void onStart(String sql);

    void onComplete();

    void onError(Throwable throwable);

    List<Metric> getMetric();
}
