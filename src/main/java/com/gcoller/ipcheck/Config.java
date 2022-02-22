package com.gcoller.ipcheck;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Configuration
@EnableConfigurationProperties
@Slf4j
public class Config {

  // root path to clone repo locally
  @Value("${app.working_dir}")
  private String workingDir;

  @Value("${app.filters}")
  private Set<String> fileFilters;

  @Bean
  public CloudWatchAsyncClient cloudWatchAsyncClient() {
    return CloudWatchAsyncClient
        .builder()
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

  @Bean
  public Path workingPath() {
    return Path.of(workingDir);
  }

  @Bean
  public IpTreeLoader getIpTreeLoader() {
    var filters = fileFilters.stream()
        .map(f -> f.indexOf('*') == -1 ? ".*%s.*".formatted(f) : f)
        .collect(Collectors.toSet());
    for (String f : filters) {
      log.info("Filter regex={}", f);
    }
    return new IpTreeLoader(workingPath(), filters);
  }

  //
  // METRICS CONFIG
  //

  @Bean
  public MeterRegistry getMeterRegistry() {
    var registry = new CloudWatchMeterRegistry(
        setupCloudWatchConfig(),
        Clock.SYSTEM,
        cloudWatchAsyncClient());

    new JvmThreadMetrics().bindTo(registry);
    return registry;
  }

  private CloudWatchConfig setupCloudWatchConfig() {
    return new CloudWatchConfig() {

      private final Map<String, String> configuration = Map.of(
          "cloudwatch.namespace", "ip_check",
          "cloudwatch.step", Duration.ofMinutes(1).toString());

      @Override
      public String get(String key) {
        return configuration.get(key);
      }
    };
  }
}
