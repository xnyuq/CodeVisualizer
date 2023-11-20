package org.example.PlantUMLParser.Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PlantUMLDiag {
    public List<JavaClass> classes = new ArrayList<>();
    public List<ClassRelation> relations = new ArrayList<>();
    public HashSet<String> classNames = new HashSet<>();
    public PlantUMLDiag() {
    }
    public void addClass(JavaClass javaClass) {
        classes.add(javaClass);
    }
    public void addRelation(ClassRelation relation) {
        relations.add(relation);
    }
    public void addClassName(String className) {
        classNames.add(className);
    }

    public boolean containsClassName(String className) {
        return classNames.contains(className);
    }

    public String toString() {
        StringBuilder umlString = new StringBuilder();
        umlString.append("@startuml\n");
        for (JavaClass javaClass : classes) {
            umlString.append(javaClass.toString()).append("\n");
        }
        for (ClassRelation relation : relations) {
            umlString.append(relation.toString()).append("\n");
        }
        umlString.append("@enduml");
        return umlString.toString();

    }

    public void toFile(String filePath) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            try {
                file.getParentFile().mkdirs();
                System.out.println("Directory is created!");
            } catch(Exception e) {
                System.out.println("Failed to create directory!");
            }
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("Failed to create file!");
            }
        }
        String content = this.toString();
        // write to file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
