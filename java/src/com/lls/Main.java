package com.lls;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void parseVerilog(Path inputPath, Path outputPath, Path yosysPath) {
        System.out.println(yosysPath.toAbsolutePath());

        try {
            Process p = new ProcessBuilder(yosysPath.toAbsolutePath().toString(), "-f", "verilog", "-b", "blif", "" +
                    "-o", outputPath.toAbsolutePath().toString(), "-p", "proc", "-p", "opt", inputPath.toAbsolutePath().toString()).start();

            if (!p.waitFor(30, TimeUnit.SECONDS)) {
                System.out.println("TIMEOUT!");
                return;
            }

            if (p.exitValue() != 0) {
                System.out.println("Error!");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Wrote blif!");
    }

    // Takes one argument:
    //      - Path to a directory of BLIF files or one BLIF file
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                throw new Exception("Please provide one argument.");
            }

            File blifFile = new File(args[0]);

            if (blifFile.isDirectory()) {
                throw new Exception("Please specify only one BLIF file, it can contain several modules");
            }

            // Read the BLIF file as one big string and pass it to the model parser
            String blifInput = Files.readString(Path.of(args[0]));
            ModelParser parser = new ModelParser(blifInput);
            ModelChecker checker = new ModelChecker();

            // Loop over all the parsed models and determine whether they are sequential or not
            for (BlifModel m : parser.getParsedBlif()){
                System.out.println("Model " + m.name + " is " + checker.checkModel(m));
            }
        } catch (Exception e) {
            System.out.println("Error while running program, please invoke with");
            System.out.println("java -jar Program.jar <Path to generated BLIF file>");

            System.out.println("Error message: " + e.getMessage());
            System.out.println("In " + Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }
    }
}
