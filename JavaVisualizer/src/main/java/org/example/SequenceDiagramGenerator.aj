package org.example;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.example.PlantUMLParser.Controller.PlantUMLHelper.renderPlantUMLAsPNG;

public aspect SequenceDiagramGenerator {

    private static final String PATH = "sequence.txt";
    private static final String NULL_CLASS = "null_class";

    private ArrayList<String> umlsrc = new ArrayList<>();
    private Stack<String> stack;
    private int callDepth;

    SequenceDiagramGenerator() {
        initialize();
    }

    private void initialize() {
        umlsrc.add("@startuml");
        umlsrc.add("autonumber");
        stack = new Stack<>();
        stack.push(NULL_CLASS);
    }

    pointcut traced() : !within(org.example.SequenceDiagramGenerator) && (execution(public * *.*(..)));

    before() : traced() {
        handleBefore(thisJoinPoint);
    }

    after()returning(Object r): traced(){
        handleAfter(r, thisJoinPoint);
    }

    private void handleBefore(JoinPoint joinPoint) {
        String className = getClassName(joinPoint);
        String funcInfo = getMethodInfo(joinPoint);
        String lastClass = stack.peek();

        if (lastClass.equals(NULL_CLASS)) {
            umlsrc.add("[-> " + className + ": " + funcInfo);
        } else {
            umlsrc.add(lastClass + " -> " + className + ": " + funcInfo);
        }

        stack.push(className);
        umlsrc.add("activate " + className);
        callDepth++;
    }

    private String getClassName(JoinPoint jointPoint) {
        try {
            return jointPoint.getThis().getClass().getName();
        } catch (NullPointerException e) {
            return jointPoint.getSignature().getDeclaringTypeName();
        }
    }

    private String getMethodInfo(JoinPoint joinPoint) {
        String funcName = joinPoint.getSignature().getName();
        String returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType().getSimpleName();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Class[] paramTypes = ((CodeSignature) joinPoint.getSignature()).getParameterTypes();

        StringBuilder methodInfo = new StringBuilder("(");
        for (int i = 0; i < paramNames.length; i++) {
            if (i > 0) {
                methodInfo.append(", ");
            }
            methodInfo.append(paramNames[i]).append(" : ").append(paramTypes[i].getSimpleName());
        }
        methodInfo.append(") : ").append(returnType);

        return funcName + methodInfo.toString();
    }

    private void handleAfter(Object r, JoinPoint joinPoint) {
        String className = getClassName(joinPoint);
        callDepth--;

        stack.pop();
        String lastClass = stack.peek();
        String sequenceInfo = buildSequenceInfo(className, lastClass, r);

        umlsrc.add(sequenceInfo);
        umlsrc.add("deactivate " + className);

        if (callDepth == 0) {
            umlsrc.add("@enduml");
            writeToFile();
            generateUML();
            initialize(); // Reset for the next sequence
        }
    }

    private String buildSequenceInfo(String className, String lastClass, Object returnValue) {
        String sequenceInfo;
        if (lastClass.equals(NULL_CLASS)) {
            sequenceInfo = "[<--";
        } else {
            sequenceInfo = lastClass + " <-- ";
        }
        sequenceInfo += className;

        if (returnValue != null) {
            sequenceInfo += ":" + returnValue.getClass().getName();
        }
        return sequenceInfo;
    }

    private void writeToFile() {
        try (PrintWriter fileWriter = new PrintWriter(PATH)) {
            for (String line : umlsrc) {
                fileWriter.println(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void generateUML() {
        try {
            File umlSource = new File(PATH);
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
}
