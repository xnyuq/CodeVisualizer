package org.example.PlantUMLParser.Model;

import org.example.PlantUMLParser.Controller.PlantUMLHelper;

import java.util.List;

public class JavaClass {
    private String type;
    private String modifier = "default";
    private String packageName;
    private String name;
    private List<ClassField> fields;
    private List<ClassMethod> methods;

    public JavaClass() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClassField> getFields() {
        return fields;
    }

    public void setFields(List<ClassField> fields) {
        this.fields = fields;
    }

    public List<ClassMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<ClassMethod> methods) {
        this.methods = methods;
    }


    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
    @Override
    public String toString() {
        // to do
        StringBuilder classString = new StringBuilder();
        classString.append(PlantUMLHelper.getModifier(this.modifier));
        classString.append(this.type).append(" ").append(this.name).append(" {\n");
        for (ClassField field : this.fields) {
            classString.append(field.toString()).append("\n");
        }
        for (ClassMethod method : this.methods) {
            classString.append(method.toString()).append("\n");
        }
        classString.append("}");
        return classString.toString();
    }

}
