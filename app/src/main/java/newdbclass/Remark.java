/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package newdbclass;

import java.util.ArrayList;

public class Remark {

    private int id;            // one of the primary key, user cannot change it here
    private String text;      // marker-made remark which is different from selected comments
    private ArrayList<Assessment> assessmentList = new ArrayList<Assessment>();

    public Remark() {

    }

    public Remark(int markerId, String text) {
        this.id = markerId;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<Assessment> getAssessmentList() {
        return assessmentList;
    }

    public void setAssessmentList(ArrayList<Assessment> assessmentList) {
        this.assessmentList = assessmentList;
    }
}


