package com.lls;

import java.util.HashSet;
import java.util.Set;

// Helper class to define the graph itself
public class WireGraph {
  Set<Wire> wires;

  public WireGraph() {
    this.wires = new HashSet<>();
  }

  public void addWire(Wire g) {
    wires.add(g);
  }

  public void addWire(String wireName) {
    wires.add(new Wire(wireName));
  }

  public void addRelationship(Wire input, Wire output) {
    input.addOutput(output);
  }

  public Wire getWire(String name) {
    for (Wire w : wires) {
      if (w.name.equals(name)) {
        return w;
      }
    }

    return null;
  }

  public Set<Wire> getWires() {
    return this.wires;
  }

  // DFS traversal algorithm
  public boolean hasLoop(Wire start) {
    start.beingProcessed = true;

    for (Wire ow : start.outputs) {
      if (ow.beingProcessed) {
        // Wire loops to itself through one gate
        return true;
      } else if (!ow.seen && hasLoop(ow)) {
        // Wire is part of a larger loop
        return true;
      }
    }

    start.beingProcessed = false;
    start.seen = true;
    return false;
  }
}
