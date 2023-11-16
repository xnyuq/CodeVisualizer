package org.example.PlantUMLParser.Controller;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.example.PlantUMLParser.Model.ClassField;
import org.example.PlantUMLParser.Model.ClassMethod;
import org.example.PlantUMLParser.Model.ClassRelation;
import org.example.PlantUMLParser.Model.PlantUMLDiag;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlantUMLHelper {
    public static String getModifier(String modifier) {
        return switch (modifier) {
            case "public" -> "+";
            case "private" -> "-";
            case "protected" -> "#";
            default -> "~";
        };
    }
    public static String getRelationType(String type) {
        return switch (type) {
            case "association" -> "--";
            case "inheritance" -> "<|--";
            case "composition" -> "*--";
            case "aggregation" -> "o--";
            case "implementation" -> "<|..";
            default -> "";
        };
    }

    public static ClassField toClassField(FieldDeclaration jField) {
        ClassField field = new ClassField();

        VariableDeclarator variable = jField.getVariable(0);
        field.setModifier(jField.getAccessSpecifier().asString().trim());
        field.setType(variable.getTypeAsString());
        field.setName(variable.getNameAsString());

        return field;
    }

    public static ClassMethod toClassMethod(MethodDeclaration jMethod) {
        ClassMethod method = new ClassMethod();

        method.setName(jMethod.getNameAsString());
        method.setIsStatic(jMethod.isStatic());
        method.setIsAbstract(jMethod.isAbstract());
        method.setModifier(jMethod.getAccessSpecifier().asString());
        method.setReturnType(jMethod.getTypeAsString());
        for (Parameter parameter: jMethod.getParameters()) {
            method.addParameter(parameter.getTypeAsString());
        }

        return method;
    }
    public static void parseRelation(ClassOrInterfaceDeclaration classOrInterface, PlantUMLDiag plantUML) {
        // Deal with inheritance and implementation
        String className = classOrInterface.getNameAsString();

        classOrInterface.getExtendedTypes().forEach(extendedType -> plantUML.addRelation(
                new ClassRelation(extendedType.getNameAsString(), className, "<|--")));
        classOrInterface.getImplementedTypes().forEach(implementedType -> plantUML.addRelation(
                new ClassRelation(implementedType.getNameAsString(), className, "<|..")));
        System.out.println("[*] Class: " + className);
//        for (MethodDeclaration method: classOrInterface.getMethods()) {
//            List<VariableDeclarator> variables = method.findAll(VariableDeclarator.class);
//            for (VariableDeclarator variable: variables) {
//                System.out.println(variable.getNameAsString() + ": " + variable.getTypeAsString());
//            }
//        }
        for (MethodCallExpr methodCall: classOrInterface.findAll(MethodCallExpr.class)) {
            Optional<Expression> scope = methodCall.getScope();
            if (scope.isPresent()) {
                System.out.println(methodCall.getNameAsString() + ": " + scope.get().toString());
            }
        }

    }

    public static ClassMethod toClassMethod(ConstructorDeclaration jMethod) {
        ClassMethod method = new ClassMethod();

        method.setName(jMethod.getNameAsString());
        method.setIsStatic(jMethod.isStatic());
        method.setIsAbstract(jMethod.isAbstract());
        method.setModifier(jMethod.getAccessSpecifier().asString());
        for (Parameter parameter: jMethod.getParameters()) {
            method.addParameter(parameter.getTypeAsString());
        }

        return method;
    }
}
