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
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;

public class SequenceGenerator {

    public String sourcePath;
    Map<String, Path> classPathMap;
    public String className;
    public String method;
    public String outputPath;
    public String renderFormat;
    public String plantUML;
    HashSet<String> parsedMethods = new HashSet<>();

    public SequenceGenerator(String sourcePath, Map<String, Path> classPathMap, String className, String method, String outputPath, String renderFormat) {
        this.sourcePath = sourcePath;
        this.classPathMap = classPathMap;
        this.className = className;
        this.method = method;
        this.outputPath = sourcePath + "\\" + outputPath;
        this.renderFormat = renderFormat;
        plantUML = "@startuml\n";
        plantUML = plantUML + "actor user #red\n";
        plantUML = plantUML + "user" + " -> " + className + " : " + method + "\n";
        plantUML = plantUML + "activate " + className + "\n";
    }


    public void generate() throws IOException {
        // set up symbolsolver
        TypeSolver typeSolver = null;
        typeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JarTypeSolver(new File("C:\\Users\\xnyuq\\.m2\\repository\\com\\github\\javaparser\\javaparser-core\\3.11.0\\javaparser-core-3.11.0.jar")),
                new JavaParserTypeSolver(new File(sourcePath))
        );

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser
                .getStaticConfiguration()
                .setSymbolResolver(symbolSolver);

        parseMethod(method, className);
        plantUML = plantUML + "@enduml";
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

                    plantUML += callerClass + " -> " + calleeClass + " : " + context + "\n";
                    plantUML += "activate " + calleeClass + "\n";
                    // get class file path

                    // continue to parse target callee function
                    MethodDeclaration calleeMethodDeclaration = methodCallExpr.resolve().toAst().orElseThrow();
                    String calleeMethodName = calleeMethodDeclaration.getNameAsString();
                    parseMethod(calleeMethodName, calleeClass);
                    if (!callerClass.equals(calleeClass))
                        plantUML += calleeClass + " -->> " + callerClass + "\n";
                    plantUML += "deactivate " + calleeClass + "\n";
                } catch (Exception e) {
                    System.out.println("Method cannot be resolved: " + e.getMessage());
                    // Method cannot be resolved -> not in project source -> ignore
                }

                    // plantuml

            });
        } catch (Exception e) {
            // type cannot be resolved -> not in project source -> ignore
//            System.out.println("Type cannot be resolved: " + e.getMessage());
        }
    }


    public String getUMLAsString() {
        return plantUML;
    }

    public void generateImg() {
        File file = new File(outputPath);
        SourceStringReader reader = new SourceStringReader(plantUML);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            reader.outputImage(outputStream, new FileFormatOption(FileFormat.PNG));
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(outputStream.toByteArray());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String getOutputPath() {
        return outputPath;
    }
}


