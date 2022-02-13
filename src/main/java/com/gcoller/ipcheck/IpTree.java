package com.gcoller.ipcheck;

import com.github.x25.net.tree.IpSubnetTree;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Proxies an underlying {@link IpSubnetTree} instance that simplifies insertion.
 */
public class IpTree {

  public static final Set<String> EMPTY_SET = Collections.emptySet();
  private final IpSubnetTree<Set<String>> tree;

  public IpTree() {
    tree = new IpSubnetTree<>();
    tree.setDefaultValue(EMPTY_SET);
  }

  /**
   * Return a set of zero or more names associated with the given ip address.
   */
  public Set<String> find(String ip) {
    return tree.find(ip);
  }

  public void add(String name, String ip) {
    var set = tree.find(stripCidr(ip));
    if (set == EMPTY_SET) {
      set = new HashSet<>();
      tree.insert(ip, set);
    }
    set.add(name);
  }

  // remove CIDR extension (eg /24)
  static String stripCidr(String line) {
    int i = line.indexOf('/');
    if (i > -1) {
      return line.substring(0, i);
    } else {
      return line;
    }
  }
}
