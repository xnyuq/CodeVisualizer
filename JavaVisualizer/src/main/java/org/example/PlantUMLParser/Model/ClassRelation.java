package org.example.PlantUMLParser.Model;

import java.util.Objects;

public class ClassRelation {
    private String source;
    private String target;
    private String relation;

    public ClassRelation() {
    }

    public ClassRelation(String source, String target, String type) {
        this.source = source;
        this.target = target;
        this.relation = type;
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

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassRelation that = (ClassRelation) o;
        return Objects.equals(source, that.source) &&
                Objects.equals(target, that.target) &&
                Objects.equals(relation, that.relation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, relation);
    }

    @Override
    public String toString() {
        return source + " "
                + relation + " "
                + target;
    }
}
