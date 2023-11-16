package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import org.example.PlantUMLParser.Controller.PlantUMLVisitor;
import org.example.PlantUMLParser.Model.PlantUMLDiag;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Path pathToSource = Paths.get("D:\\study\\do_an\\JavaVisualizer\\src\\main\\java");
        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.tryToParse();
        List<CompilationUnit> compilationUnits = sourceRoot.getCompilationUnits();

        PlantUMLVisitor visitor = new PlantUMLVisitor();
        PlantUMLDiag plantUML = new PlantUMLDiag();
//        for (CompilationUnit cu: compilationUnits) {
//
//        }
        for (CompilationUnit cu: compilationUnits) {
            visitor.visit(cu, plantUML);
        }
//        System.out.println(plantUML.toString());
    }
}