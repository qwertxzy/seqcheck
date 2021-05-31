package com.lls;

import java.util.ArrayList;
import java.util.List;

public class ModelParser {
  final static String autoGenPrefix = "GeneratedModel_";

  List<BlifModel> parsedBlif;

  ModelParser(String source) {
    parsedBlif = new ArrayList<>();

    // resolve \ linebreaks and remove comments
    source = source.replaceAll("\\\\\\r\\n|\\\\\\n|#.+", "");

    // split into models (named and unnamed)
    FSMState parseState = FSMState.Initial;

    // String buffer for the current model
    StringBuilder buf = new StringBuilder();

    // Counter for the unnamed models
    int unnamedCounter = 0;

    // Iterate over the text line by line
    for (String line : source.split("\r\n")) {

      // The current keyword at the start of the line until the first space
      String keyword = line.split(" ")[0];

      switch (parseState) {
        case Initial:
          switch (keyword) {
            case ".model":
              buf.append(line).append("\n");
              parseState = FSMState.NamedModel;
              break;
            case ".names":
              buf.append(line).append("\n");
              parseState = FSMState.UnnamedModel;
              break;
            default:
              break;
          }
          break;
        case NamedModel:
          buf.append(line).append("\n");
          if (keyword.equals(".end")) {
            // Extract the model name, dump the buffer into the model
            String modelName = buf.substring(6, buf.indexOf("\n"));
            BlifModel m = new BlifModel(modelName, buf.toString());
            parsedBlif.add(m);

            parseState = FSMState.Initial;
            buf = new StringBuilder();
          }
          break;
        case UnnamedModel:
          if (keyword.equals(".model")) {
            // Generate an incrementing modelName, dump the buffer into the model
            String modelName = autoGenPrefix + unnamedCounter; //TODO: this could collide with existing model names
            BlifModel m = new BlifModel(modelName, buf.toString());
            parsedBlif.add(m);

            unnamedCounter++;
            parseState = FSMState.NamedModel;
            buf = new StringBuilder(line + "\n");
          } else {
            buf.append(line).append("\n");
          }
          break;
      }
    }

    // If string buffer isn't empty that means an unnamed model was being defined until EOF
    if (buf.length() > 0) {
      String modelName = autoGenPrefix + unnamedCounter;
      BlifModel m = new BlifModel(modelName, buf.toString());
      parsedBlif.add(m);
    }
  }

  public List<BlifModel> getParsedBlif() {
    return this.parsedBlif;
  }

  // Enumeration type for the parsing FSM
  private enum FSMState {
    Initial,
    NamedModel,
    UnnamedModel
  }
}
