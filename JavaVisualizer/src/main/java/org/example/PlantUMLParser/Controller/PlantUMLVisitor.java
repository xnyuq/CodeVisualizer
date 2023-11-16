package org.example.PlantUMLParser.Controller;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.example.PlantUMLParser.Model.ClassField;
import org.example.PlantUMLParser.Model.ClassMethod;
import org.example.PlantUMLParser.Model.JavaClass;
import org.example.PlantUMLParser.Model.PlantUMLDiag;

import java.util.ArrayList;
import java.util.List;

public class PlantUMLVisitor extends VoidVisitorAdapter<PlantUMLDiag> {
    @Override
    public void visit(ClassOrInterfaceDeclaration classOrInterface, PlantUMLDiag plantUML) {
        JavaClass javaClass = new JavaClass();
        // Extract class name and generate PlantUML code
        String className = classOrInterface.getNameAsString();
        javaClass.setName(className);
        javaClass.setModifier(classOrInterface.getAccessSpecifier().asString().trim());
        if (classOrInterface.isInterface()) {
            javaClass.setType("interface");
        } else {
            javaClass.setType("class");
            for (Modifier modifier: classOrInterface.getModifiers()) {
                if (modifier.toString().contains("abstract")) {
                    javaClass.setType("{abstract} class");
                }
            }
        }


        // Parse fields
        List<ClassField> fields = new ArrayList<>();
        classOrInterface.getFields().forEach(field -> fields.add(PlantUMLHelper.toClassField(field)));
        // Parse methods
        List<ClassMethod> methods = new ArrayList<>();
        // Parse Constructors
        classOrInterface.getConstructors().forEach(constructor -> methods.add(PlantUMLHelper.toClassMethod(constructor)));
        // Parse class methods
        classOrInterface.getMethods().forEach(method -> methods.add(PlantUMLHelper.toClassMethod(method)));


        javaClass.setFields(fields);
        javaClass.setMethods(methods);


        // Deal with relation
        PlantUMLHelper.parseRelation(classOrInterface, plantUML);

        // Add to classes list after each class is visited
        plantUML.addClass(javaClass);

        super.visit(classOrInterface, plantUML);
    }
}