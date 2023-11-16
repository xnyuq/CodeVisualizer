package org.example.PlantUMLParser.Model;

public class ClassRelation {
    private String source;
    private String target;
    private String type;

    public ClassRelation() {
    }

    public ClassRelation(String source, String target, String type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return source + " "
                + type + " "
                + target;
    }
}
