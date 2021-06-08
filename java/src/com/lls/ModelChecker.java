package com.lls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelChecker {

  // List of Cell prefixes that require sequential logic
  final static String[] sequentialCells = {
      "$_SR_",
      "$_FF_",
      "$_DFF_",
      "$_DFFE_",
      "$_DFFSR_",
      "$_DFFSRE_",
      "$_SDFF_",
      "$_SDFFE_",
      "$_SDFFCE_",
      "$_DLATCH_",
      "$_DLATCHSR_"
  };

  public ModelChecker() {}

  /*
      Model checking procedure:

      Yosys uses an internal cell library that it synthesizes to.
      By checking the BLIF model for subcircuits that require a cell with sequential logic,
      we can determine if the whole model implements sequential logic or not. Afterwards the lack
      of existence of .latch / .mlatch / .clock blocks is asserted.
      If these checks pass, another one is carried out that tests for wire loops within .names
      as these are an indicator of sequential logic with only gates.
   */
  public CircuitType checkModel(BlifModel m) {

    // First check
    Pattern pattern = Pattern.compile("\\.subckt .*?(?= )");
    Matcher matcher = pattern.matcher(m.model);

    while (matcher.find()) {
      for (String cell : sequentialCells) {
        if (matcher.group().substring(8).startsWith(cell)) {
          return CircuitType.SEQUENTIAL;
        }
      }
    }

    // Second check
    pattern = Pattern.compile("\\.(m?)latch|\\.clock");
    matcher = pattern.matcher(m.model);

    if (matcher.find()) {
      return CircuitType.SEQUENTIAL;
    }

    // Third check
    WireGraph wireGraph = new WireGraph();

    // Create wires from .inputs and .outputs
    pattern = Pattern.compile("(\\.inputs.+|\\.outputs.+)");
    matcher = pattern.matcher(m.model);

    while (matcher.find()) {
      String[] parts = matcher.group().split(" ");
      for (int i = 1; i < parts.length; i++) {
        wireGraph.addWire(new Wire(parts[i]));
      }
    }

    // get all .names blocks, split to inputs and output and build them to a directed graph
    pattern = Pattern.compile("\\.names.+");
    matcher = pattern.matcher(m.model);

    while (matcher.find()) {
      String[] parts = matcher.group().split(" ");

      // filter out preset definitions
      if (parts[1].equals("$true") || parts[1].equals("$false") || parts[1].equals("$undef")) {
        continue;
      }

      // TryAdd Output Wire
      wireGraph.addWire(parts[parts.length - 1]);

      // Go through each input wire
      for (int i = 1; i < parts.length - 1; i++) {
        // Might be an internal wire, so tryAdd it to the graph
        wireGraph.addWire(parts[i]);


        // Add the relationship between current wire and output wire
        Wire inputWire = wireGraph.getWire(parts[i]);
        Wire outputWire = wireGraph.getWire(parts[parts.length - 1]);

        wireGraph.addRelationship(inputWire, outputWire);
      }
    }


    // Check the graph for cycles
    for (Wire w : wireGraph.getWires()) {
      if (!w.seen && wireGraph.hasLoop(w)) {
        // Circuit has a wire loop, is potentially sequential
        return CircuitType.LOOPING;
      }
    }

    return CircuitType.COMBINATIONAL;
  }




}
