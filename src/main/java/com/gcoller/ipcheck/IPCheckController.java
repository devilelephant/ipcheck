package com.gcoller.ipcheck;

import static java.time.Duration.ofMillis;
import static java.util.Optional.ofNullable;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class IPCheckController {

  private final Timer ipCheckTimer;
  private final IpTreeLoader ipTreeLoader;
  private IpTree tree;

  @Autowired
  public IPCheckController(IpTreeLoader ipTreeLoader, MeterRegistry meterRegistry) {
    this.ipTreeLoader = ipTreeLoader;
    this.tree = new IpTree();

    ipCheckTimer = Timer.builder("check_ip")
        .sla(ofMillis(2), ofMillis(300), ofMillis(600))
        .publishPercentileHistogram()
        .register(meterRegistry);
  }

  @PostConstruct
  public void init() {
    log.info("load tree in another thread");
    new Thread(() -> {
      ipTreeLoader.load(tree);
      log.info("load tree finished");
    }).start();
  }

  @PostMapping(path = "/ipcheck/reload")
  public void refreshIps() {
    log.info("start refresh");
    IpTree newTree = new IpTree();
    ipTreeLoader.load(newTree);
    tree = newTree;
    log.info("end refresh");
  }

  @GetMapping(
      path = "/ipcheck/{ipAddress}",
      produces = {MediaType.APPLICATION_JSON_VALUE}
  )
  public String check(@PathVariable String ipAddress) {
    var result = ipCheckTimer.record(() -> ofNullable(tree.find(ipAddress)).orElse(IpTree.EMPTY_SET));
    var output = """
        { "ip" : "%s", "result":"%s" }
        """.formatted(ipAddress, result);
    log.info(output);
    return output;
  }

}
