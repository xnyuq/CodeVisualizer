package org.example.PlantUMLParser.Controller;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;

public class SequenceGenerator {

    public String sourcePath;
    public String classPath;
    public String method;
    public String outputPath;
    public String renderFormat;
    public String plantUML;
    HashSet<String> parsedMethods = new HashSet<>();

    public SequenceGenerator(String sourcePath, String classPath, String method, String outputPath, String renderFormat) {
        this.sourcePath = sourcePath;
        this.classPath = classPath;
        this.method = method;
        this.outputPath = sourcePath + "\\" + outputPath;
        this.renderFormat = renderFormat;
        plantUML = "@startuml\n";
        plantUML = plantUML + "actor user #red\n";
        String className = classPath.substring(classPath.lastIndexOf("\\") + 1, classPath.lastIndexOf("."));
        plantUML = plantUML + "user" + " -> " + className + " : " + method + "\n";
        plantUML = plantUML + "activate " + className + "\n";
    }


    public void generate() {
        // set up symbolsolver
        TypeSolver typeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(new File(sourcePath))
        );

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser
                .getStaticConfiguration()
                .setSymbolResolver(symbolSolver);

        File file = new File(classPath);
        try {
            CompilationUnit compilationUnit = JavaParser.parse(file);
            compilationUnit.findFirst(MethodDeclaration.class, methodDeclaration -> {
                return methodDeclaration.getNameAsString().equals(method);
            }).ifPresent(methodDeclaration -> {
                parseMethod(compilationUnit.getType(0).getNameAsString(), methodDeclaration);
            });
            plantUML = plantUML + "@enduml";
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void parseMethod(String callerClass, MethodDeclaration methodDeclaration) {
        if (parsedMethods.contains(methodDeclaration.getDeclarationAsString())) {
            return;
        }
        String methodName = methodDeclaration.getNameAsString();
        parsedMethods.add(methodName);
        methodDeclaration.findAll(MethodCallExpr.class).forEach(methodCallExpr -> {
            try {
                // parse method call
                String context = methodCallExpr.resolve().getName();
                String calleeClass = methodCallExpr.resolve().getClassName();
                plantUML += callerClass + " -> " + calleeClass + " : " + context + "\n";
                plantUML += "activate " + calleeClass + "\n";
                // get class file path

//                parseMethod(calleeClass, methodCallExpr.resolve().getDeclaration().asMethodDeclaration());
                plantUML += calleeClass + " -->> " + callerClass + "\n";
                plantUML += "deactivate " + calleeClass + "\n";

                // plantuml

            } catch (Exception e) {
                // type cannot be resolved -> not in project source -> ignore
            }
        });

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

