package com.meijie.performance.report;

import org.apache.commons.lang3.builder.Builder;

/**
 * @author meijie
 * @since 1.0
 */
public class ManagerBuilder implements Builder<Manager> {

    private Tasker taker;
    private Listener listener;
    private Reporter reporter;
    private CoreConfig config;

    public ManagerBuilder withTasker(Tasker tasker) {
        this.taker = taker;
        return this;
    }

    public ManagerBuilder withListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public ManagerBuilder withReporter(Reporter reporter) {
        this.reporter = reporter;
        return this;
    }

    public ManagerBuilder withConfig(CoreConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public Manager build() {
        return new DefaultManager(taker, listener, reporter, config);
    }
}
