package org.example;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.utils.SourceRoot;
import org.example.PlantUMLParser.Controller.PlantUMLVisitor;
import org.example.PlantUMLParser.Controller.SequenceGenerator;
import org.example.PlantUMLParser.Controller.XsdConverterVisitor;
import org.example.PlantUMLParser.Controller.XsdToJavaConverter;
import org.example.PlantUMLParser.Model.PlantUMLDiag;
import org.apache.commons.cli.*;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.PlantUMLParser.Controller.PlantUMLHelper.renderPlantUMLAsPNG;

public class Main {
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption("i", "input", true, "Input source folder");
        options.addOption("g", "generate", true, "Generate output (uml/xsd/seq/class)");
        options.addOption("o", "output", true, "Output folder path");
        options.addOption("r", "render", true, "Render format (png or ascii)");
        options.addOption("c", "class", true, "Class name");
        options.addOption("m", "method", true, "Method name");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            String sourcePath = cmd.getOptionValue("i");
            String generateOption = cmd.getOptionValue("g");
            String outputFolder = cmd.getOptionValue("o", "output/");
            String renderFormat = cmd.getOptionValue("r", "png");
            String className = cmd.getOptionValue("c", "Main");
            String methodName = cmd.getOptionValue("m", "main");

            // Accept the path to the source from the user

            if ("uml".equals(generateOption) || "xsd".equals(generateOption) || "seq".equals(generateOption)) {
                Path pathToSource = Paths.get(sourcePath);
                SourceRoot sourceRoot = new SourceRoot(pathToSource);
                sourceRoot.tryToParse();
                List<CompilationUnit> compilationUnits = sourceRoot.getCompilationUnits();

                PlantUMLVisitor umlVisitor = new PlantUMLVisitor();
                PlantUMLDiag plantUML = new PlantUMLDiag();

                Map<String, Path> classPathMap = new HashMap<>();
                // Maintain a list of project's class names for later use
                for (CompilationUnit cu : compilationUnits) {
                    String className1 = cu.getPrimaryTypeName().get();
                    plantUML.addClassName(className1);
                    Path filePath = cu.getStorage().get().getPath();
                    classPathMap.put(className1, filePath);
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
                        String className1 = cu.getPrimaryTypeName().get();
                        XsdConverterVisitor xsdVisitor = new XsdConverterVisitor(outputFolder + className1 + ".xsd");
                        System.out.println("Parsing " + className1 + "...");
                        xsdVisitor.visit(cu, plantUML.classNames);
                        xsdVisitor.closeWriter();
                    }
                } else if ("seq".equals(generateOption)) {
                    // Generate Sequence Diagram
                    SequenceGenerator sequenceGenerator = new SequenceGenerator(sourcePath, classPathMap, className , methodName, "sequence1.txt");
                    sequenceGenerator.generate();
                    System.out.println("File generated at " + sequenceGenerator.getOutputPath());
                }

            } else if ("class".equals(generateOption)) {
                XsdToJavaConverter xsdToJavaConverter = new XsdToJavaConverter();
                ClassOrInterfaceDeclaration classOrInterface = xsdToJavaConverter.generateClassFromXml(sourcePath);
                System.out.println(classOrInterface.toString());

            }
            else {
                System.err.println("Invalid option for -g/--generate. Use 'uml' or 'xsd' or 'seq'.");
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


    private static void renderPlantUMLAsAscii(String plantUmlSource) {
        // Render PlantUML as ASCII plantUML code
        System.out.println("Rendering PlantUML as ASCII:\n" + plantUmlSource);
    }
}
