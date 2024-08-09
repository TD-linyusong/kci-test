package com.talkdesk.tag.reference.common.metrics;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Configuration class for Micrometer-related settings.
 */
@ConfigMapping(prefix = "micrometer.newrelic")
public interface MicrometerNewRelicConfig {

    /**
     * The API key used to integrate with the downstream APM service.
     * For NewRelic, this will be the license key.
     */
    String apiKey();

    /**
     * The name used to identify this application in the downstream APM service.
     */
    String serviceName();

    /**
     * If true, will create a MeterRegistry that sends metrics to NewRelic.
     */
    @WithDefault("true")
    boolean isEnabled();
}
