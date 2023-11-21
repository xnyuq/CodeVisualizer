package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import org.example.PlantUMLParser.Controller.PlantUMLVisitor;
import org.example.PlantUMLParser.Model.ClassRelation;
import org.example.PlantUMLParser.Model.PlantUMLDiag;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    List<List<String>> test;
    public static void main(String[] args) throws IOException {
        Path pathToSource = Paths.get("C:\\Users\\xnyuq\\IdeaProjects\\CodeVisualizer\\JavaVisualizer\\src");
        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.tryToParse();
        List<CompilationUnit> compilationUnits = sourceRoot.getCompilationUnits();

        PlantUMLVisitor visitor = new PlantUMLVisitor();
        PlantUMLDiag plantUML = new PlantUMLDiag();

        // Maintain a list of project's class names for later use
        for (CompilationUnit cu: compilationUnits) {
            String className = cu.getPrimaryTypeName().get();
            plantUML.addClassName(className);
        }
        for (CompilationUnit cu: compilationUnits) {
            visitor.visit(cu, plantUML);
        }
        List<ClassRelation> relations = plantUML.getRelations();
//        for (ClassRelation classRelation: relations) {
//            System.out.println(classRelation.toString());
//        }
        System.out.println(plantUML.toString());
    }
}