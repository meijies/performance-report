package com.meijie.performance.report;

import com.google.common.base.Preconditions;

import java.sql.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author meijie
 * @since 1.0
 *
 * execute jdbc task with parcel
 */
public class JdbcTask implements Tasker<String> {

    private List<String> sqlList;
    private Listener listener;
    private ResourcePool<Connection> resourcePool;
    private ExecutorService executorService;
    private CountDownLatch latch;

    @Override
    public List<Metric> doTask(final ExecutorService executorService, CoreConfig coreConfig) throws TaskException {
        try {
            this.executorService = executorService;
            initResourcePool(coreConfig);
            for (String sql : this.sqlList) {
                CompletableFuture.runAsync(() -> executeSqlConsumer.accept(sql), executorService);
            }
            latch.await();
            return listener.getMetric();
        } catch (ClassNotFoundException | SQLException | InterruptedException e) {
            throw new TaskException(e);
        }
    }

    private void initResourcePool(CoreConfig coreConfig) throws ClassNotFoundException, SQLException {
        resourcePool = new ResourcePool<>(coreConfig.getParcelSize());
        for (int i = 0; i < coreConfig.getParcelSize(); i++) {
            Class.forName(coreConfig.getDriver());
            Connection conn = DriverManager.getConnection(coreConfig.getJdbcUrl());
            resourcePool.putItem(conn);
        }
        Preconditions.checkArgument(resourcePool.size() == coreConfig.getParcelSize(),
                "resource size not equal parcel size");
    }

    public Consumer<String> executeSqlConsumer = sql -> {
        try {
            Connection conn = resourcePool.getItem();
            Preconditions.checkNotNull(conn, "resource pool is on not working status");
            listener.onStart(sql);
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listener.onComplete();
                    latch.countDown();
                    break;
                }
            }
        } catch (InterruptedException | SQLException e) {
            executorService.shutdownNow();
            listener.onError(e);
            throw new RuntimeException(e);
        }
    };

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void prepare(List<String> dataList) {
        Preconditions.checkNotNull(dataList);
        this.sqlList = dataList;
        this.latch = new CountDownLatch(dataList.size());
    }
}
