package com.lls;

public enum CircuitType {
  COMBINATIONAL,
  SEQUENTIAL,
  LOOPING;

  private static final String RESET = "\033[0m";      // Text Reset
  private static final String RED = "\033[0;31m";     // Red console color
  private static final String GREEN = "\033[0;32m";   // Green console color
  public static final String YELLOW = "\033[0;33m";   // Yellow console color

  // Pretty printing
  @Override
  public String toString() {
    switch (this) {
      case COMBINATIONAL: return GREEN + "combinational" + RESET;
      case SEQUENTIAL: return RED + "sequential" + RESET;
      case LOOPING: return YELLOW + "containing a feedback loop" + RESET;
      default: throw new IllegalArgumentException();
    }
  }
}
