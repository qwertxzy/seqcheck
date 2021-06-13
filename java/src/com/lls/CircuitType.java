package com.lls;

public enum CircuitType {
  COMBINATIONAL,
  SEQUENTIAL,
  LOOPING;

  // Pretty printing
  @Override
  public String toString() {
    switch (this) {
      case COMBINATIONAL: return "combinational";
      case SEQUENTIAL: return "sequential";
      case LOOPING: return "containing a feedback loop";
      default: throw new IllegalArgumentException();
    }
  }
}
