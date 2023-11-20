package org.example.PlantUMLParser.Model;

import org.example.PlantUMLParser.Controller.PlantUMLHelper;

public class ClassField {
    private String type;
    private String modifier = "default";
    private String name;
    private boolean isStatic;

    public ClassField() {
    }

    public ClassField(String type, String modifier, String name, boolean isStatic) {
        this.type = type;
        this.modifier = modifier;
        this.name = name;
        this.isStatic = isStatic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }
    @Override
    public String toString() {
        return PlantUMLHelper.getModifier(modifier)
                + (isStatic ? "{static} " : "")
                + type + " "
                + name;
    }
}
