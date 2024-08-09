package com.talkdesk.tag.reference.common.metrics;

import com.newrelic.telemetry.micrometer.NewRelicRegistry;
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;
import io.micrometer.common.lang.NonNull;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.quarkus.runtime.Startup;

import javax.enterprise.context.ApplicationScoped;

/**
 * Configures and registers a Micrometer MeterRegistry bean that can be injected into an application class
 * which provides the ability for that class to export metrics to New Relic.
 */
@ApplicationScoped
@Startup
public class NewRelicMeterRegistryConfig {

    public NewRelicMeterRegistryConfig(final MicrometerNewRelicConfig micrometerConfig) {
        final NewRelicRegistryConfig registryConfig = new NewRelicRegistryConfig() {
            @Override
            public String get(final @NonNull String key) {
                // Some common keys passed in here are "newrelic.step", "newrelic.connectTimeout", "newrelic
                // .batchSize", "newrelic.enabled"
                // If you want to override the values for those settings, you can do that here
                return null;
            }

            @Override
            public String apiKey() {
                return micrometerConfig.apiKey();
            }

            @Override
            public String serviceName() {
                return micrometerConfig.serviceName();
            }

            @Override
            public boolean useLicenseKey() {
                return true;
            }
        };

        if (micrometerConfig.isEnabled()) {
            final NewRelicRegistry newRelicRegistry = NewRelicRegistry.builder(registryConfig).build();
            newRelicRegistry.config().namingConvention(NamingConvention.dot);
            newRelicRegistry.start(new NamedThreadFactory("newrelic.micrometer.registry"));

            Metrics.globalRegistry.add(newRelicRegistry);
        }
    }
}
