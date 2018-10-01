package com.meijie.performance.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author meijie
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class CoreConfig {
    private int parcelSize;
    private String driver;
    private String jdbcUrl;
}
