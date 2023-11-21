package org.example.PlantUMLParser.Controller;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
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

        classOrInterface.getExtendedTypes().forEach((ClassOrInterfaceType extendedType) -> {
            plantUML.addRelation(
                    new ClassRelation(extendedType.getNameAsString(), className, "<|--"));
        });
        classOrInterface.getImplementedTypes().forEach((ClassOrInterfaceType implementedType) -> {
            plantUML.addRelation(
                    new ClassRelation(implementedType.getNameAsString(), className, "<|.."));
        });
//        System.out.println("[*] Class: " + className);
        // isArrayType() to check if the type is an array
        // isPrimitiveType() to check if the type is a primitive type
        // getElementType() to get the type of the array
        for (FieldDeclaration field: classOrInterface.getFields()) {
//            System.out.println("[*] Field: " + field.toString());
//            Type tp = field.getVariable(0).getType();
//            System.out.println("\t" + getGenericType(tp).toString());

            //System.out.println("\t" + field.getElementType());

            List<VariableDeclarator> variables = field.findAll(VariableDeclarator.class);
            for (VariableDeclarator variable: variables) {
                Type type = getGenericType(variable.getType());
                if (plantUML.containsClassName(type.asString())) {
                    plantUML.addRelation(
                            new ClassRelation(type.asString(), className, "*--"));
                }
            }
        }
        for (MethodDeclaration method: classOrInterface.getMethods()) {
            List<VariableDeclarator> variables = method.findAll(VariableDeclarator.class);
            for (VariableDeclarator variable: variables) {
                String type = getGenericType(variable.getType()).asString();
                if (plantUML.containsClassName(type)) {
                    plantUML.addRelation(
                            new ClassRelation(type, className, "*--"));
                }
            }
        }

//        for (MethodCallExpr methodCall: classOrInterface.findAll(MethodCallExpr.class)) {
//            Optional<Expression> scope = methodCall.getScope();
//            scope.ifPresent((Expression expression) -> {
//                System.out.println(methodCall.getNameAsString() + ": " + expression.toString());
//            });
//        }

        for (MethodCallExpr methodCall: classOrInterface.findAll(MethodCallExpr.class)) {
            Optional<Expression> scope = methodCall.getScope();
            scope.ifPresent((Expression expression) -> {
                String type = expression.toString();
//                System.out.println("[*] Expression extracted: " + methodCall.getNameAsString() + ":" + type);
                if (plantUML.containsClassName(type)) {
                    plantUML.addRelation(
                            new ClassRelation(type, className, "--"));
                }
            });
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
//    public static Type getGenericType(Type type) {
//        while (type.isClassOrInterfaceType()) {
//            ClassOrInterfaceType classOrInterfaceType = type.asClassOrInterfaceType();
//            NodeList<Type> typeArguments = classOrInterfaceType.getTypeArguments().orElse(null);
//            if (typeArguments != null) {
//                return typeArguments.get(0);
//            }
//        }
//        return type;
//    }
    public static Type getGenericType(Type type) {
        if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType classOrInterfaceType = type.asClassOrInterfaceType();
            NodeList<Type> typeArguments = classOrInterfaceType.getTypeArguments().orElse(null);

            if (typeArguments != null && !typeArguments.isEmpty()) {
                // Get the first type argument
                Type firstTypeArgument = typeArguments.get(0);

                // Recursively call the method for nested generic types
                return getGenericType(firstTypeArgument);
            } else {
                // No more generic types, return the current type
                return type;
            }

        }
        return type;
    }
}
