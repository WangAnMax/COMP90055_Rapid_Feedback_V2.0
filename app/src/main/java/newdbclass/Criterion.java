/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package newdbclass;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Criterion {

    private int id;     // set to 0 when add criterion, include when update criterion
    private String name;
    private double weight;
    private double maximumMark;
    private double markIncrement; // either be 0.25, 0.5 or 1
    private ArrayList<Field> fieldList = new ArrayList<Field>();

    public Criterion(int id, String name, double weight, double maximumMark, double markIncrement) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.maximumMark = maximumMark;
        this.markIncrement = markIncrement;
    }

    public Criterion() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getMaximumMark() {
        return maximumMark;
    }

    public void setMaximumMark(double maximumMark) {
        this.maximumMark = maximumMark;
    }

    public double getMarkIncrement() {
        return markIncrement;
    }

    public void setMarkIncrement(double markIncrement) {
        this.markIncrement = markIncrement;
    }

    public ArrayList<Field> getFieldList() {
        return fieldList;
    }

    public void setFieldList(ArrayList<Field> fieldList) {
        this.fieldList = fieldList;
    }

    @Override
    public boolean equals(Object anObject) {
        if (!(anObject instanceof Criterion)) {
            return false;
        }
        Criterion otherMember = (Criterion) anObject;
        return otherMember.getName().equals(getName());
    }
}
