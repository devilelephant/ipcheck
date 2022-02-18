package com.gcoller.ipcheck;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.junit.jupiter.api.Test;

class IpTreeTest {

  @Test
  public void testNoCidrWorkaround() {
    IpTree tree = new IpTree();
    tree.add("77.219.59.9", "WAP Tele2");

    assertEquals(Set.of(), tree.find("77.219.59.8"));
    assertEquals(Set.of("WAP Tele2"), tree.find("77.219.59.9"));
    assertEquals(Set.of(), tree.find("77.219.59.10"));
  }
}