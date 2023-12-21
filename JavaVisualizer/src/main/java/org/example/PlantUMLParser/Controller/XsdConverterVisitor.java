package org.example.PlantUMLParser.Controller;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class XsdConverterVisitor extends VoidVisitorAdapter<Set<String>> {

    private static final String XML_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    private static final String TARGET_NAMESPACE = "/jaxb/gen";

    private BufferedWriter writer;

    public XsdConverterVisitor(String outputPath) {
        try {
            this.writer = new BufferedWriter(new FileWriter(outputPath));
        } catch (IOException e) {
            throw new RuntimeException("Error creating XSD file: " + e.getMessage(), e);
        }
    }

    public void closeWriter() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error closing XSD file: " + e.getMessage(), e);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration classOrInterface, Set<String> projectClasses) {
        writeLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writeLine("<schema xmlns=\"" + XML_NAMESPACE + "\" targetNamespace=\"" + TARGET_NAMESPACE +
                "\" xmlns:userns=\"" + TARGET_NAMESPACE + "\" elementFormDefault=\"qualified\">");

        String className = classOrInterface.getNameAsString();
        writeLine("  <element name=\"" + className + "\" type=\"userns:" + className + "\"></element>");

        writeLine("  <complexType name=\"userns:" + className + "\">");
        writeLine("    <sequence>");

        classOrInterface.getFields().forEach((FieldDeclaration field) -> {
            if (isPrimitiveOrDefined(field, projectClasses)) {
                writeLine("      <element name=\"" + field.getVariables().get(0).getNameAsString() +
                        "\" type=\"" + getXmlType(field.getElementType(), projectClasses) + "\"/>");
            }
        });

//        classOrInterface.getMethods().forEach((MethodDeclaration method) -> {
//            writeLine("      <element name=\"" + method.getNameAsString() +
//                    "\" type=\"" + getXmlType(method.getType(), projectClasses) + "\"/>");
//        });

        writeLine("    </sequence>");
        writeLine("  </complexType>");
        writeLine("</schema>");

        super.visit(classOrInterface, projectClasses);
    }

    private void writeLine(String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error writing to XSD file: " + e.getMessage(), e);
        }
    }

    private boolean isPrimitive(String typeName) {
        // Add mappings for other primitive types as needed
        return typeName.equals("int") || typeName.equals("double") ||
                typeName.equals("float") || typeName.equals("boolean") ||
                typeName.equals("char") || typeName.equals("byte") ||
                typeName.equals("short") || typeName.equals("long") ||
                typeName.equals("String") || typeName.equals("Date");
    }

    private String getXmlType(Type type, Set<String> projectClasses) {
        // Convert Java type to XML Schema type
        if (type.isPrimitiveType()) {
            return getXmlPrimitiveType(type.asString());
        } else if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType classType = type.asClassOrInterfaceType();
            String typeName = classType.toString();

            // Check if it's a known project class or a generic type
            if (projectClasses.contains(typeName) || isGenericType(typeName)) {
                return "tns:" + extractGenericType(typeName);
            }
        }

        return "xs:string";
    }

    private String getXmlPrimitiveType(String typeName) {
        // Convert Java primitive type to XML Schema type
        // ... (Add mappings for other primitive types as needed)
        return switch (typeName) {
            case "int" -> "xs:int";
            case "double" -> "xs:double";
            case "float" -> "xs:float";
            case "boolean" -> "xs:boolean";
            case "char" -> "xs:string";
            case "byte" -> "xs:byte";
            case "short" -> "xs:short";
            case "long" -> "xs:long";
            case "Date" -> "xs:dateTime";
            default -> "xs:string";
        };
    }

    private boolean isGenericType(String typeName) {
        // Check if the type is a generic type
        return typeName.contains("<") && typeName.endsWith(">");
    }

    private boolean isPrimitiveOrDefined(FieldDeclaration field, Set<String> projectClasses) {
        VariableDeclarator variable = field.getVariables().get(0);
        String typeName = variable.getTypeAsString();

        // Check if the type is primitive, defined in the project, or a generic type
        return isPrimitive(typeName) || projectClasses.contains(typeName) || isGenericType(typeName);
    }
        private String extractGenericType(String typeName) {
        // Extract the generic type inside the angle brackets
        int start = typeName.indexOf("<");
        int end = typeName.lastIndexOf(">");

        if (start != -1 && end != -1 && start < end) {
            return typeName.substring(start + 1, end);
        }

        return typeName;
    }
}
