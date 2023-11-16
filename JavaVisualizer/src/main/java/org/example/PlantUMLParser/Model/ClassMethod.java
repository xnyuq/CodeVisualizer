package org.example.PlantUMLParser.Model;

import org.example.PlantUMLParser.Controller.PlantUMLHelper;

import java.util.ArrayList;
import java.util.List;

public class ClassMethod {
    private String returnType;
    private String modifier = "default";
    private boolean isStatic;
    private boolean isAbstract;
    private String name;
    private List<String> parameters = new ArrayList<>();

    public ClassMethod() {
    }

    public ClassMethod(String returnType, String modifier, boolean isStatic, boolean isAbstract, String name, List<String> parameters) {
        this.returnType = returnType;
        this.modifier = modifier;
        this.isStatic = isStatic;
        this.isAbstract = isAbstract;
        this.name = name;
        this.parameters = parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public boolean getIsStatic() {
        return isStatic;
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public boolean getIsAbstract() {
        return isAbstract;
    }

    public void setIsAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String parameter) {
        this.parameters.add(parameter);
    }
    @Override
    public String toString() {
        return PlantUMLHelper.getModifier(modifier)
                + (isStatic ? "{static} " : "")
                + (isAbstract ? "{abstract } " : "")
                + (returnType==null ? "" : returnType + " ") +
                name
                + "(" + String.join(", ", parameters) + ")";
    }

}
