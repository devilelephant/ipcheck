package com.gcoller.ipcheck;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class IpTreeTest {

  @Test
  public void testNoCidrWorkaround() {
    IpTree tree = new IpTree();
    tree.add("77.219.59.9");

    assertEquals("", tree.find("77.219.59.8"));
    assertEquals("77.219.59.9", tree.find("77.219.59.9"));
    assertEquals("", tree.find("77.219.59.10"));
  }
}