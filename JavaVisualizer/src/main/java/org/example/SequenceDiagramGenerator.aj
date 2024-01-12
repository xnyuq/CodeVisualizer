package org.example;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public aspect SequenceDiagramGenerator {
//
//    private static final String PATH = "sequence.txt";
//    private static final String NULL_CLASS = "null_class";
//    private static boolean check = true;
//
//    private ArrayList<String> umlsrc = new ArrayList<>();
//    private Stack<String> stack;
//    private int callDepth = 0;
//    private static HashSet<MethodInfoWithDepth> traversedMethodBefore = new HashSet<>();
//    private static HashSet<MethodInfoWithDepth> traversedMethodAfter = new HashSet<>();
//
//    SequenceDiagramGenerator() {
//        initialize();
//    }
//
//    private void initialize() {
//        umlsrc.add("@startuml");
//        umlsrc.add("autonumber");
//        umlsrc.add("actor user #red");
//        stack = new Stack<>();
//        stack.push(NULL_CLASS);
//    }
//    pointcut traced() : !within(org.example.SequenceDiagramGenerator) && (execution(public * *.*(..)));
//
//    before() : traced() {
//        handleBefore(thisJoinPoint);
//    }
//
//    after()returning(Object r): traced(){
//        handleAfter(r, thisJoinPoint);
//        if (check) {
//            System.out.println("Executing: " + thisJoinPoint);
//            System.out.println("Call from: " + thisEnclosingJoinPointStaticPart);
//            check = false;
//        }
//    }
//
//    private void handleBefore(JoinPoint joinPoint) {
//        String currentClassName = getClassName(joinPoint);
//        String funcInfo = getMethodInfo(joinPoint);
//        boolean skip = false;
//
//        String prevClassName = stack.peek();
//        stack.push(currentClassName);
//
//        MethodInfoWithDepth methodInfoWithDepth = new MethodInfoWithDepth(prevClassName + funcInfo, callDepth);
//        if (traversedMethodBefore.contains(methodInfoWithDepth)) {
//            skip = true;
//        }
//        traversedMethodBefore.add(methodInfoWithDepth);
//
//        if (!skip) {
//            if (prevClassName.equals(NULL_CLASS)) {
//                umlsrc.add("user -> " + currentClassName + ": " + funcInfo);
//            } else {
//                umlsrc.add(prevClassName + " -> " + currentClassName + ": " + funcInfo);
//            }
//
//            umlsrc.add("activate " + currentClassName);
//        }
//
//        callDepth++;
//    }
//
//    private String getClassName(JoinPoint jointPoint) {
//        try {
//            return jointPoint.getThis().getClass().getName();
//        } catch (NullPointerException e) {
//            return jointPoint.getSignature().getDeclaringTypeName();
//        }
//    }
//
//    private String getMethodInfo(JoinPoint joinPoint) {
//        String funcName = joinPoint.getSignature().getName();
//        String returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType().getSimpleName();
//        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
//        Class[] paramTypes = ((CodeSignature) joinPoint.getSignature()).getParameterTypes();
//
//        StringBuilder methodInfo = new StringBuilder("(");
//        for (int i = 0; i < paramNames.length; i++) {
//            if (i > 0) {
//                methodInfo.append(", ");
//            }
//            methodInfo.append(paramNames[i]).append(" : ").append(paramTypes[i].getSimpleName());
//        }
//        methodInfo.append(") : ").append(returnType);
//
//        return funcName + methodInfo.toString();
//    }
//
//    private void handleAfter(Object r, JoinPoint joinPoint) {
//
//        String funcInfo = getMethodInfo(joinPoint);
//        boolean skip = false;
//
//        String className = getClassName(joinPoint);
//        callDepth--;
//
//        stack.pop();
//        String lastClass = stack.peek();
//
//        if (traversedMethodAfter.contains(new MethodInfoWithDepth(lastClass + funcInfo, callDepth))) {
//            skip = true;
//        }
//        traversedMethodAfter.add(new MethodInfoWithDepth(lastClass + funcInfo, callDepth));
//
//        if (!skip) {
//            String sequenceInfo = buildSequenceInfo(className, lastClass, r);
//            if (sequenceInfo != null)
//                umlsrc.add(sequenceInfo);
//            umlsrc.add("deactivate " + className);
//        }
//
//        if (callDepth == 0) {
//            umlsrc.add("@enduml");
//            writeToFile();
//            generateUML();
//            initialize(); // Reset for the next sequence
//        }
//    }
//
//    private String buildSequenceInfo(String className, String lastClass, Object returnValue) {
//        String sequenceInfo = null;
//        if (lastClass.equals(NULL_CLASS)) {
//            sequenceInfo = "user <-- " + className;
//        } else if (!className.equals(lastClass)) {
//            sequenceInfo = lastClass + " <-- " + className;
//        }
//
//        if (sequenceInfo != null && returnValue != null) {
//            sequenceInfo += ":" + returnValue.getClass().getName();
//        }
//        return sequenceInfo;
//    }
//
//    private void writeToFile() {
//        try (PrintWriter fileWriter = new PrintWriter(PATH)) {
//            for (String line : umlsrc) {
//                fileWriter.println(line);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void generateUML() {
//        try {
//            File umlSource = new File(PATH);
//            SourceFileReader sourceReader = new SourceFileReader(umlSource);
//            List<GeneratedImage> images = sourceReader.getGeneratedImages();
//            File sequenceDiagram = images.get(0).getPngFile();
//            System.out.println("Sequence diagram generated at: " + sequenceDiagram.getAbsolutePath());
//
//            Desktop desktop = Desktop.getDesktop();
//            desktop.open(sequenceDiagram);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private class MethodInfoWithDepth {
//        String methodInfo;
//        int depth;
//
//        public MethodInfoWithDepth(String methodInfo, int depth) {
//            this.methodInfo = methodInfo;
//            this.depth = depth;
//        }
//        @Override
//        public boolean equals(Object o) {
//            if (o == this) {
//                return true;
//            }
//            if (!(o instanceof MethodInfoWithDepth)) {
//                return false;
//            }
//            MethodInfoWithDepth other = (MethodInfoWithDepth) o;
//            return this.methodInfo.equals(other.methodInfo) && this.depth == other.depth;
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(methodInfo, depth);
//        }
//
//    }
}

//package com.library;
//
//import net.sourceforge.plantuml.GeneratedImage;
//import net.sourceforge.plantuml.SourceFileReader;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.reflect.CodeSignature;
//import org.aspectj.lang.reflect.MethodSignature;
//
//import java.awt.*;
//        import java.io.*;
//        import java.util.*;
//        import java.util.List;
//
//public aspect SequenceDiagramGenerator {
//
//    private static boolean generated = false;
//    private static final String PATH = "sequence.txt";
//    private static final String NULL_CLASS = "null_class";
//    private static boolean check = true;
//
//    private ArrayList<String> umlsrc = new ArrayList<>();
//    private Stack<String> stack;
//    private int callDepth = 0;
//    private static HashSet<MethodInfoWithDepth> traversedMethodBefore = new HashSet<>();
//    private static HashSet<MethodInfoWithDepth> traversedMethodAfter = new HashSet<>();
//
//    SequenceDiagramGenerator() {
//        if (!generated) {
//            initialize();
//        }
//
//    }
//
//    private void initialize() {
//        umlsrc.add("@startuml");
//        umlsrc.add("autonumber");
//        umlsrc.add("actor user #red");
//        stack = new Stack<>();
//        stack.push(NULL_CLASS);
//    }
//    pointcut traced() : !within(com.library.SequenceDiagramGenerator)
//            && !within(com.library.entity.Configuration)
//            && (execution(* com.library.services.*.*(..))
//            || execution(* com.library.entity.*.*(..))
//            || execution(* com.library.dto.*.*(..))
//            || execution(* com.library.repository.*.*(..)));
//
//    before() : traced() {
//        if (!generated)
//            handleBefore(thisJoinPoint);
//    }
//
//    after()returning(Object r): traced(){
//        if (!generated) {
//            handleAfter(r, thisJoinPoint);
//            if (check) {
//                System.out.println("Executing: " + thisJoinPoint);
//                System.out.println("Call from: " + thisEnclosingJoinPointStaticPart);
//                check = false;
//            }
//        }
//    }
//
//    private void handleBefore(JoinPoint joinPoint) {
//        String currentClassName = getClassName(joinPoint);
//        String funcInfo = getMethodInfo(joinPoint);
//        boolean skip = false;
//
//        String prevClassName = stack.peek();
//        stack.push(currentClassName);
//
//        MethodInfoWithDepth methodInfoWithDepth = new MethodInfoWithDepth(prevClassName + funcInfo, callDepth);
//        if (traversedMethodBefore.contains(methodInfoWithDepth)) {
//            skip = true;
//        }
//        traversedMethodBefore.add(methodInfoWithDepth);
//
//        if (!skip) {
//            if (prevClassName.equals(NULL_CLASS)) {
//                umlsrc.add("user -> " + currentClassName + ": " + funcInfo);
//            } else {
//                umlsrc.add(prevClassName + " -> " + currentClassName + ": " + funcInfo);
//            }
//
//            umlsrc.add("activate " + currentClassName);
//        }
//
//        callDepth++;
//    }
//
//    private String getClassName(JoinPoint jointPoint) {
//        try {
//            return jointPoint.getThis().getClass().getName();
//        } catch (NullPointerException e) {
//            return jointPoint.getSignature().getDeclaringTypeName();
//        }
//    }
//
//    private String getMethodInfo(JoinPoint joinPoint) {
//        String funcName = joinPoint.getSignature().getName();
//        String returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType().getSimpleName();
//        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
//        Class[] paramTypes = ((CodeSignature) joinPoint.getSignature()).getParameterTypes();
//
//        StringBuilder methodInfo = new StringBuilder("(");
//        for (int i = 0; i < paramNames.length; i++) {
//            if (i > 0) {
//                methodInfo.append(", ");
//            }
//            methodInfo.append(paramNames[i]).append(" : ").append(paramTypes[i].getSimpleName());
//        }
//        methodInfo.append(") : ").append(returnType);
//
//        return funcName + methodInfo.toString();
//    }
//
//    private void handleAfter(Object r, JoinPoint joinPoint) {
//
//        String funcInfo = getMethodInfo(joinPoint);
//        boolean skip = false;
//
//        String className = getClassName(joinPoint);
//        callDepth--;
//
//        stack.pop();
//        String lastClass = stack.peek();
//
//        if (traversedMethodAfter.contains(new MethodInfoWithDepth(lastClass + funcInfo, callDepth))) {
//            skip = true;
//        }
//        traversedMethodAfter.add(new MethodInfoWithDepth(lastClass + funcInfo, callDepth));
//
//        if (!skip) {
//            String sequenceInfo = buildSequenceInfo(className, lastClass, r);
//            if (sequenceInfo != null)
//                umlsrc.add(sequenceInfo);
//            umlsrc.add("deactivate " + className);
//        }
//
//        if (callDepth == 0) {
//            umlsrc.add("@enduml");
//            writeToFile();
//            generateUML();
//            generated = true;
//            //initialize(); // Reset for the next sequence
//        }
//    }
//
//    private String buildSequenceInfo(String className, String lastClass, Object returnValue) {
//        String sequenceInfo = null;
//        if (lastClass.equals(NULL_CLASS)) {
//            sequenceInfo = "user <-- " + className;
//        } else if (!className.equals(lastClass)) {
//            sequenceInfo = lastClass + " <-- " + className;
//        }
//
//        if (sequenceInfo != null && returnValue != null) {
//            sequenceInfo += ":" + returnValue.getClass().getName();
//        }
//        return sequenceInfo;
//    }
//
//    private void writeToFile() {
//        try (PrintWriter fileWriter = new PrintWriter(PATH)) {
//            for (String line : umlsrc) {
//                fileWriter.println(line);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void generateUML() {
//        try {
//            File umlSource = new File(PATH);
//            SourceFileReader sourceReader = new SourceFileReader(umlSource);
//            List<GeneratedImage> images = sourceReader.getGeneratedImages();
//            File sequenceDiagram = images.get(0).getPngFile();
//            System.out.println("Sequence diagram generated at: " + sequenceDiagram.getAbsolutePath());
//            openImage(sequenceDiagram.getAbsolutePath());
//
//            // Desktop desktop = Desktop.getDesktop();
//            // desktop.open(sequenceDiagram);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void openImage(String wslPath) throws IOException {
//        ProcessBuilder processBuilder = new ProcessBuilder("shotwell", wslPath);
//        Process process = processBuilder.start();
//
//        // Wait for the process to finish
//        //process.waitFor();
//    }
//
//    private class MethodInfoWithDepth {
//        String methodInfo;
//        int depth;
//
//        public MethodInfoWithDepth(String methodInfo, int depth) {
//            this.methodInfo = methodInfo;
//            this.depth = depth;
//        }
//        @Override
//        public boolean equals(Object o) {
//            if (o == this) {
//                return true;
//            }
//            if (!(o instanceof MethodInfoWithDepth)) {
//                return false;
//            }
//            MethodInfoWithDepth other = (MethodInfoWithDepth) o;
//            return this.methodInfo.equals(other.methodInfo) && this.depth == other.depth;
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(methodInfo, depth);
//        }
//
//    }
//}
