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


  /*
      Model checking procedure:

      Yosys uses an internal cell library that it synthesizes to.
      By checking the BLIF model for subcircuits that require a cell with sequential logic,
      we can determine if the whole model implements sequential logic or not.
   */
  public static boolean checkModel(BlifModel m) {
    boolean isSequential = false;

    Pattern pattern = Pattern.compile("\\.subckt .*?(?= )");
    Matcher matcher = pattern.matcher(m.model);

    while (matcher.find()) {
      for (String cell : sequentialCells) {
        if (matcher.group().substring(8).startsWith(cell)) {
          isSequential = true;
          break;
        }
      }
    }

    return isSequential;
  }
}
