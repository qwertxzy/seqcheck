package com.lls;

import java.util.ArrayList;
import java.util.List;

// Helper class to define a Wire in the directed graph
public class Wire {
  String name;
  List<Wire> outputs;

  boolean seen;
  boolean beingProcessed;

  public Wire(String name) {
    this.name = name;
    outputs = new ArrayList<>();

    seen = false;
    beingProcessed = false;
  }

  public void addOutput(Wire output) {
    outputs.add(output);
  }
}