package com.gcoller.ipcheck;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Configuration
@EnableConfigurationProperties
@Slf4j
public class Config {

  @Autowired
  private Properties properties;

  @Bean
  public CloudWatchAsyncClient cloudWatchAsyncClient() {
    return CloudWatchAsyncClient
        .builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build();
  }

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

  @Bean
  public IpTreeLoader getIpTreeLoader() {
    return new IpTreeLoader(properties.repoDir);
  }
}
