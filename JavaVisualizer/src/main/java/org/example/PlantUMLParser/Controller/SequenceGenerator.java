package org.example.PlantUMLParser.Controller;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import net.sourceforge.plantuml.*;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SequenceGenerator {

    public String sourcePath;
    Map<String, Path> classPathMap;
    public String className;
    public String method;
    public String outputPath;
    public ArrayList<String> umlsrc = new ArrayList<>();
    public HashSet<String> parsedMethods = new HashSet<>();

    public SequenceGenerator(String sourcePath, Map<String, Path> classPathMap, String className, String method, String outputPath) {
        this.sourcePath = sourcePath;
        this.classPathMap = classPathMap;
        this.className = className;
        this.method = method;
        this.outputPath = outputPath;
        umlsrc.add("@startuml");
        umlsrc.add("actor user #red");
        umlsrc.add("user" + " -> " + className + " : " + method);
        umlsrc.add("activate " + className);
    }


    public void generate() throws IOException {
        // Set up symbolsolver
        TypeSolver typeSolver = null;
//        String jarPath = "C:\\Users\\Lenovo\\.m2\\repository\\com\\github\\javaparser\\javaparser-core\\3.11.0\\javaparser-core-3.11.0.jar";
        typeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver(),
//                new JarTypeSolver(new File(jarPath)),
//                new JarTypeSolver(new File("C:\\Users\\xnyuq\\.m2\\repository\\com\\github\\javaparser\\javaparser-core\\3.11.0\\javaparser-core-3.11.0.jar")),
                new JavaParserTypeSolver(new File(sourcePath))
        );

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser
                .getStaticConfiguration()
                .setSymbolResolver(symbolSolver);

        parseMethod(method, className);
        umlsrc.add("@enduml");
        writeToFile();
        generateUML();
    }
    private void parseMethod(String methodName, String className) {
        String classPath = classPathMap.get(className).toString();
        File classFile = new File(classPath);
        MethodDeclaration methodDeclaration = null;
        try {
            CompilationUnit compilationUnit = JavaParser.parse(classFile);
            methodDeclaration = compilationUnit.findFirst(MethodDeclaration.class, method -> {
                return method.getNameAsString().equals(methodName);
            }).get();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (methodDeclaration == null) {
            System.out.println("Method not found: " + methodName);
            return;
        }


        try {
//            String calleeClass = methodDeclaration.resolve().getClassName();
//            String fullMethodName = calleeClass + "." + methodDeclaration.getDeclarationAsString();
//            if (parsedMethods.contains(fullMethodName)) {
//                return;
//            }
//            parsedMethods.add(fullMethodName);

            String callerClass = methodDeclaration.resolve().getClassName();
            methodDeclaration.findAll(MethodCallExpr.class).forEach(methodCallExpr -> {
//                System.out.println(methodCallExpr.getNameAsString());
                // parse method call
                try {
                    String calleeClass = methodCallExpr.resolve().getClassName();
                    String context = methodCallExpr.resolve().getName();
                    if (parsedMethods.contains(calleeClass + "." + context)) {
                        return;
                    }
                    parsedMethods.add(calleeClass + "." + context);
                    if (!classPathMap.containsKey(calleeClass)) {
                        return;
                    }

                    umlsrc.add(callerClass + " -> " + calleeClass + " : " + context);
                    umlsrc.add("activate " + calleeClass);
                    // get class file path

                    // continue to parse target callee function
                    MethodDeclaration calleeMethodDeclaration = methodCallExpr.resolve().toAst().orElseThrow();
                    String calleeMethodName = calleeMethodDeclaration.getNameAsString();
                    parseMethod(calleeMethodName, calleeClass);
                    if (!callerClass.equals(calleeClass)) {
                        String returnType = calleeMethodDeclaration.getTypeAsString();
                        umlsrc.add(calleeClass + " -->> " + callerClass + " : " + returnType);
                    }
                    umlsrc.add("deactivate " + calleeClass);
                } catch (Exception e) {
//                    System.out.println("Method cannot be resolved: " + e.getMessage());
                    // Method cannot be resolved -> not in project source -> ignore
                }


            });
        } catch (Exception e) {
            // type cannot be resolved -> not in project source -> ignore
//            System.out.println("Type cannot be resolved: " + e.getMessage());
        }
    }


    public ArrayList<String> getUMLAsString() {
        return umlsrc;
    }

    public String getOutputPath() {
        return outputPath;
    }

    private void generateUML() {
        try {
            File umlSource = new File(this.outputPath);
            SourceFileReader sourceReader = new SourceFileReader(umlSource);
            List<GeneratedImage> images = sourceReader.getGeneratedImages();
            File sequenceDiagram = images.get(0).getPngFile();
            System.out.println("Sequence diagram generated at: " + sequenceDiagram.getAbsolutePath());

            Desktop desktop = Desktop.getDesktop();
            desktop.open(sequenceDiagram);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile() {
        try (PrintWriter fileWriter = new PrintWriter(outputPath)) {
            for (String line : umlsrc) {
                fileWriter.println(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}


