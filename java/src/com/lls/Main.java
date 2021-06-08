package com.lls;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class Main {
    public static void parseVerilog(Path[] inputPath, Path outputPath, Path yosysPath) throws Exception {
            ProcessBuilder pb = new ProcessBuilder(yosysPath.toAbsolutePath().toString(),
                "-f", "verilog", "-b", "blif", "-o", outputPath.toAbsolutePath().toString(), "-p", "synth");

            Arrays.stream(inputPath).map(path -> path.toAbsolutePath().toString()).forEach(s -> pb.command().add(s));

            Process p = pb.start();

            // Close the input stream to stop the process from hanging
            p.getInputStream().close();

            if (!p.waitFor(30, TimeUnit.SECONDS)) {
                throw new TimeoutException("Yosys timed out!");
            }

            if (p.exitValue() != 0) {
                System.out.println("Error while calling Yosys!");
                throw new Exception(new BufferedReader(new InputStreamReader(p.getErrorStream()))
                    .lines().collect(Collectors.joining("\n")));
            }
        System.out.println("Successfully synthesized BLIF from Verilog files.");
    }

    // Methods of invocation:
    //      Provide one BLIF file (default)
    //  OR
    //      Provide one Verilog file (or a directory of Verilog files)
    //      and a Path to the Yosys executable (determined by number of args)
    public static void main(String[] args) {
        Path blifPath = null;
        String blifInput = "";

        try {
            if (args.length == 1) {
                // BLIF only mode
                blifPath = Path.of(args[0]);

            } else if (args.length == 2) {
                // Synth mode

                // Check if Verilog argument is a directory or a single file
                List<Path> verilogPath = new ArrayList<>();
                File verilogArg = new File(args[0]);
                if (verilogArg.isDirectory()) {
                    // A little cumbersome, but filter directory for .v files and then turn each java.io.File into a java.nio.Path
                    File[] verilogFiles = verilogArg.listFiles((dir, name) -> name.toLowerCase().endsWith(".v"));
                    verilogPath = Arrays.stream(verilogFiles).map(f -> Path.of(f.getPath())).collect(Collectors.toList());

                } else {
                    verilogPath.add(Path.of(args[0]));
                }

                Path yosysPath = Path.of(args[1]);
                blifPath = Paths.get("./out.blif");

                parseVerilog(verilogPath.toArray(new Path[]{}), blifPath, yosysPath);

            }

            // Read the BLIF file as one big string
            blifInput = Files.readString(blifPath);

        } catch (Exception e) {
            System.out.println("Error while running program, please invoke with");
            System.out.println("\tjava -jar Program.jar <Path to BLIF file>");
            System.out.println("or");
            System.out.println("\tjava -jar Program.jar <Verilog path> <Yosys path>");
            System.out.println("Where Verilog path points to either one Verilog file or a directory of .v files and Yosys path points to the Yosys executable\n\n");

            System.out.println("Error message: " + e.getMessage());
            System.out.println("In " + Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }

        ModelParser parser = new ModelParser(blifInput);
        ModelChecker checker = new ModelChecker();

        // Loop over all the parsed models and determine whether they are sequential or not
        for (BlifModel m : parser.getParsedBlif()){
            System.out.println("Model " + m.name + " is " + checker.checkModel(m));
        }
    }
}
