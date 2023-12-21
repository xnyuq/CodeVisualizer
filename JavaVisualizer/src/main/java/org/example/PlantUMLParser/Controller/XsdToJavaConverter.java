package org.example.PlantUMLParser.Controller;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.VoidType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XsdToJavaConverter {
    public ClassOrInterfaceDeclaration generateClassFromXml(String xmlFilePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFilePath);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            XPathExpression expression = xpath.compile("//complexType");
            NodeList complexTypes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

            if (complexTypes.getLength() > 0) {
                Node complexTypeNode = complexTypes.item(0); // Assuming there is only one complexType
                ClassOrInterfaceDeclaration classDeclaration = buildClassFromNode(complexTypeNode);
                return classDeclaration;
            }

        } catch (ParserConfigurationException | XPathExpressionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ClassOrInterfaceDeclaration buildClassFromNode(Node complexTypeNode) {
        Element complexTypeElement = (Element) complexTypeNode;

        // Extract class name from XML (after namespace)
        String typeName = complexTypeElement.getAttribute("name");
        String[] nameParts = typeName.split(":");
        String className = nameParts[nameParts.length - 1];
        ClassOrInterfaceDeclaration classDeclaration = new ClassOrInterfaceDeclaration();
        classDeclaration.setName(className);


        // Extract fields and methods from XML
        NodeList elements = complexTypeElement.getElementsByTagName("element");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            String memberName = element.getAttribute("name");
            String memberType = element.getAttribute("type");

            // Map element types to actual Java types
            String javaType = getJavaType(memberType);
            classDeclaration.addField(javaType, memberName, Modifier.Keyword.PRIVATE);

            // Add getter method
            addGetterMethod(classDeclaration, javaType, memberName);

            // Add setter method
            addSetterMethod(classDeclaration, javaType, memberName);
        }

        return classDeclaration;
    }

    private String getJavaType(String xsdType) {
        // Map XML Schema types to Java types
        return switch (xsdType) {
            case "xs:int" -> "int";
            case "xs:double" -> "double";
            case "xs:float" -> "float";
            case "xs:boolean" -> "boolean";
            case "xs:string" -> "String";
            case "xs:byte" -> "byte";
            case "xs:short" -> "short";
            case "xs:long" -> "long";
            case "xs:dateTime" -> "Date";
            default -> "String";
        };
    }

    private void addGetterMethod(ClassOrInterfaceDeclaration classDeclaration, String returnType, String fieldName) {
        MethodDeclaration getterMethod = classDeclaration.addMethod("get" + capitalize(fieldName), Modifier.Keyword.PUBLIC);
        getterMethod.setType(returnType);
        getterMethod.getBody().ifPresent(body -> body.addStatement("return " + fieldName + ";"));
    }

    private void addSetterMethod(ClassOrInterfaceDeclaration classDeclaration, String parameterType, String fieldName) {
        MethodDeclaration setterMethod = classDeclaration.addMethod("set" + capitalize(fieldName), Modifier.Keyword.PUBLIC);
        setterMethod.setType(new VoidType());
        setterMethod.addParameter(parameterType, fieldName);
        setterMethod.getBody().ifPresent(body -> body.addStatement("this." + fieldName + " = " + fieldName + ";"));
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
