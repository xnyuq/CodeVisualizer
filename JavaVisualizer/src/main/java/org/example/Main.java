package org.example;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import net.sourceforge.plantuml.SourceStringReader;
import org.example.PlantUMLParser.Controller.PlantUMLVisitor;
import org.example.PlantUMLParser.Controller.XsdConverterVisitor;
import org.example.PlantUMLParser.Model.PlantUMLDiag;
import org.apache.commons.cli.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption("g", "generate", true, "Generate output (uml or xsd)");
        options.addOption("o", "output", true, "Output folder path");
        options.addOption("r", "render", true, "Render format (png or ascii)");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            String sourcePath = cmd.getOptionValue("i");
            String generateOption = cmd.getOptionValue("g");
            String outputFolder = cmd.getOptionValue("o", "output/");
            String renderFormat = cmd.getOptionValue("r", "png");

            // Accept the path to the source from the user

            if ("uml".equals(generateOption) || "xsd".equals(generateOption)) {
                Path pathToSource = Paths.get(sourcePath);
                SourceRoot sourceRoot = new SourceRoot(pathToSource);
                sourceRoot.tryToParse();
                List<CompilationUnit> compilationUnits = sourceRoot.getCompilationUnits();

                PlantUMLVisitor umlVisitor = new PlantUMLVisitor();
                PlantUMLDiag plantUML = new PlantUMLDiag();

                // Maintain a list of project's class names for later use
                for (CompilationUnit cu : compilationUnits) {
                    String className = cu.getPrimaryTypeName().get();
                    plantUML.addClassName(className);
                }

                for (CompilationUnit cu : compilationUnits) {
                    umlVisitor.visit(cu, plantUML);
                }

                if ("uml".equals(generateOption)) {
                    // Display PlantUML diagrams at runtime
                    String plantUmlSource = plantUML.toString();
                    renderPlantUML(plantUmlSource, renderFormat);
                } else if ("xsd".equals(generateOption)) {
                    // Generate XSD files
                    for (CompilationUnit cu : compilationUnits) {
                        String className = cu.getPrimaryTypeName().get();
                        XsdConverterVisitor xsdVisitor = new XsdConverterVisitor(outputFolder + className + ".xsd");
                        System.out.println("Parsing " + className + "...");
                        xsdVisitor.visit(cu, plantUML.classNames);
                        xsdVisitor.closeWriter();
                    }
                }
            } else {
                System.err.println("Invalid option for -g/--generate. Use 'uml' or 'xsd'.");
            }
        } catch (ParseException e) {
            System.err.println("Error parsing command-line arguments: " + e.getMessage());
        }
    }

    private static void renderPlantUML(String plantUmlSource, String renderFormat) {
        try {
            if ("png".equals(renderFormat)) {
                renderPlantUMLAsPNG(plantUmlSource);
            } else if ("ascii".equals(renderFormat)) {
                renderPlantUMLAsAscii(plantUmlSource);
            } else {
                System.err.println("Invalid render format. Use 'png' or 'ascii'.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void renderPlantUMLAsPNG(String plantUmlSource) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SourceStringReader reader = new SourceStringReader(plantUmlSource);
        reader.outputImage(outputStream);

        // Implement your logic for displaying or saving the PNG image
        // For simplicity, let's just print the image bytes to the console
        System.out.println(outputStream.toString());
    }

    private static void renderPlantUMLAsAscii(String plantUmlSource) {
        // Render PlantUML as ASCII art
        System.out.println("Rendering PlantUML as ASCII:\n" + plantUmlSource);
    }
}
