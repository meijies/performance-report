package com.meijie.performance.report;

import java.util.List;

/**
 * @author meijie
 * @since 1.0
 */
public interface Reporter {
    void doReport(List<Metric> metricList);
}
