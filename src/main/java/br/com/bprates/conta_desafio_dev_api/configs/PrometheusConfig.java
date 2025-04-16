package br.com.bprates.conta_desafio_dev_api.configs;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrometheusConfig {

    @Bean
    MeterRegistryCustomizer<PrometheusMeterRegistry> prometheusMetricsCustomizer() {
        return registry -> {
            // Aqui você pode configurar tags globais se quiser
            registry.config().commonTags("application", "conta-desafio-dev-api");
        };
    }
}
