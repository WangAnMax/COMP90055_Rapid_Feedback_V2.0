package util;

public class CustomisedCriteria {
    private String criteria;
    private String subSection;
    private String shortText;
    private String longText;
    private int grade;

    @Override
    public String toString() {
        return "CustomisedCriteria{" +
                "criteria='" + criteria + '\'' +
                ", subSection='" + subSection + '\'' +
                ", shortText='" + shortText + '\'' +
                ", longText='" + longText + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }

    public CustomisedCriteria() {
        criteria = "";
        subSection = "";
        shortText = "";
        longText = "";
        grade = 0;
    }

    public String getCriteria() {
        return criteria;
    }

    public String getSubSection() {
        return subSection;
    }

    public String getShortText() {
        return shortText;
    }

    public String getLongText() {
        return longText;
    }

    public int getGrade() {
        return grade;
    }

    public void setSubSection(String subSection) {
        this.subSection = subSection;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public void setLongText(String longText) {
        this.longText = longText;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

}
