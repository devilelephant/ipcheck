package com.gcoller.ipcheck;

import static java.time.Duration.ofMillis;
import static java.util.Optional.ofNullable;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class IPCheckController {

  private final Counter healthCheckCounter;
  private final Timer checkIpTimer;
  private final IpTreeLoader ipTreeLoader;
  private IpTree tree;

  @Autowired
  public IPCheckController(IpTreeLoader ipTreeLoader, MeterRegistry meterRegistry) {
    this.ipTreeLoader = ipTreeLoader;

    healthCheckCounter = meterRegistry.counter("healthcheck_counter");

    checkIpTimer = Timer.builder("check_ip")
        .sla(ofMillis(2), ofMillis(300), ofMillis(600))
        .publishPercentileHistogram()
        .register(meterRegistry);
  }

  @PostConstruct
  public void init() {
//    tree = new IpTree();
//    ipTreeLoader.load(tree);
  }

  @GetMapping(path = "/healthcheck")
  public String healthcheck() {
    healthCheckCounter.increment();
    return "OK";
  }

  @GetMapping(
      path = "/checkip/{ipAddress}",
      produces = {MediaType.APPLICATION_JSON_VALUE}
  )
  public String check(@PathVariable String ipAddress) {
    var result = checkIpTimer.record(() -> ofNullable(tree.find(ipAddress)).orElse(IpTree.EMPTY_SET));
    var output = """
        { "ip" : "%s", "result":"%s" }
        """.formatted(ipAddress, result);
    log.info(output);
    return output;
  }

}
