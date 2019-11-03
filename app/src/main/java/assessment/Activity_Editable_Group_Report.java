/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package assessment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedback.Activity_Login;
import com.example.feedback.R;

import java.math.BigDecimal;
import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Assessment;
import newdbclass.ExpandedComment;
import newdbclass.Project;
import newdbclass.ProjectStudent;
import newdbclass.Remark;
import newdbclass.SelectedComment;

public class Activity_Editable_Group_Report extends AppCompatActivity {
    private int indexOfProject;
    private int indexOfGroup;
    private int indexOfMark;
    private int indexOfStudent;
    private ArrayList<ProjectStudent> studentInfoArrayList;
    private Toolbar mToolbar;
    private String from;
    public static final String FROMREALTIMEEDIT = "realtime_edit";
    public static final String FROMREVIEWEDIT = "review_edit";
    private Project project;
    private Remark remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editable_group_report);
        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        Project project = AllFunctions.
                getObject().getProjectList().get(indexOfProject);
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        indexOfMark = Integer.parseInt(intent.getStringExtra("indexOfMark"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        from = intent.getStringExtra("from");
        studentInfoArrayList = new ArrayList<ProjectStudent>();
        for (int i = 0; i < project.getStudentList().size(); i++) {
            if (project.getStudentList().get(i).getGroupNumber() == indexOfGroup)
                studentInfoArrayList.add(project.getStudentList().get(i));
        }
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_editable_group_report);
        mToolbar.setTitle("Report -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Editable_Group_Report.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Editable_Group_Report.this,
                                Activity_Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }


    private void init() {
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        ProjectStudent student = AllFunctions.getObject().getProjectList().get(indexOfProject).getStudentList().get(indexOfStudent);

        ArrayList<Remark> remarkList = student.getRemarkList();
        remark = remarkList.get(indexOfMark);

        Button button_finalReport = findViewById(R.id.button_finalReport_groupReport);
        button_finalReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < studentInfoArrayList.size(); i++) {
                    AllFunctions.getObject().sendFinalResult(project.getId(), studentInfoArrayList.get(i).getId(), getAverageMark(remarkList), getFinalRemark(student.getRemarkList()));
                }
                if (from.equals(Activity_Display_Mark.FROMREALTIME)) {
                    Intent intent = new Intent(Activity_Editable_Group_Report.this, Activity_Send_Report_Group.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("indexMark", String.valueOf(indexOfMark));
                    intent.putExtra("from", FROMREALTIMEEDIT);
                    startActivity(intent);
                } else if (from.equals(Activity_Display_Mark.FROMREVIEW)) {
                    Intent intent = new Intent(Activity_Editable_Group_Report.this, Activity_Send_Report_Group.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("indexMark", String.valueOf(indexOfMark));
                    intent.putExtra("from", FROMREVIEWEDIT);
                    startActivity(intent);
                }
            }
        });
        if (AllFunctions.getObject().getProjectList().get(indexOfProject).getPrincipalId() != AllFunctions.getObject().getId()) {
            button_finalReport.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < remarkList.size(); i++) {
            if (getTotalMark(remarkList.get(i)) < 0) {
                button_finalReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Activity_Editable_Group_Report.this,
                                "Other markers are still marking. Please wait for a moment.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        Button button_edit = findViewById(R.id.button_edit_groupReport);
        button_edit.setVisibility(View.INVISIBLE);

        TextView textView_totalMark = findViewById(R.id.textView_totalMark_report);
        textView_totalMark.setText("Mark: " + String.format("%.2f", getTotalMark(remark)) + "%");
        TextView textView_assessorName = findViewById(R.id.textView_assessorName_report);

        String lecturerName = "";
        for (int i = 0; i < project.getMarkerList().size(); i++) {
            if (project.getMarkerList().get(i).getId() == remark.getId()) {
                lecturerName = project.getMarkerList().get(i).getFirstName() + " "
                        + project.getMarkerList().get(i).getMiddleName() + " "
                        + project.getMarkerList().get(i).getLastName();
            }
        }
        textView_assessorName.setText("Marker: " + lecturerName);
        String htmlString =
                "<html>" +
                        "<body>" +
                        "<h1 style=\"font-weight: normal\">" + project.getName() + "</h1>" +
                        "<hr>" +
                        "<p>" + "Group: " + indexOfGroup + "</p >" +
                        "<h2 style=\"font-weight: normal\">Subject</h2>" +
                        "<p>" + project.getSubjectCode() + " --- " + project.getSubjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Project</h2>" +
                        "<p>" + project.getName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Mark Attained</h2>" +
                        "<p>" + String.format("%.2f", getTotalMark(remark)) + "%</p >";

        htmlString += "<h2 style=\"font-weight: normal\">Students</h2>" + "<p>";
        for (int i = 0; i < studentInfoArrayList.size(); i++)
            htmlString = htmlString + studentInfoArrayList.get(i).getStudentNumber() + "---" + studentInfoArrayList.get(i).getFirstName() + " " + studentInfoArrayList.get(i).getMiddleName() + " " + studentInfoArrayList.get(i).getLastName() + "<br>";

        htmlString = htmlString +
                "</p >" +
                "<br><br><br><hr>" +
                "<div>";

        htmlString += "<h2 style=\"font-weight: normal\">Grading Criteria</h2>" + "<p>";
        for (int i = 0; i < remark.getAssessmentList().size(); i++) {
            htmlString += "<h3 style=\"font-weight: normal\"><span style=\"float:left\">" + getCriterionName(remark.getAssessmentList().get(i)) + "</span>" +
                    "<span style=\"float:right\">" + "  ---  " + remark.getAssessmentList().get(i).getScore() + "/" + getCriterionMaxMark(remark.getAssessmentList().get(i)) + "</span></h3>";
            for (int j = 0; j < remark.getAssessmentList().get(i).getSelectedCommentList().size(); j++) {
                htmlString += "<p>" + getFieldName(remark.getAssessmentList().get(i).getSelectedCommentList().get(j)) +
                        " : " + getExCommentName(remark.getAssessmentList().get(i).getSelectedCommentList().get(j)) + "</p >";
            }
            htmlString += "<br>";
        }

        htmlString += "<h3 style=\"font-weight: normal\"><span style=\"float:left\">" + "Remark" + "</span></h3>";
        if (remark.getText() == null) {
            htmlString += "<p>" + "No remark." + "</p >";
        } else {
            htmlString += "<p>" + remark.getText() + "</p >";
        }

        htmlString +=
                "</div>" +
                        "</body>" +
                        "</html>";
        TextView textView_pdfContent = findViewById(R.id.textView_pdfContent_GroupReport);
        textView_pdfContent.setText(Html.fromHtml(htmlString));
    }

    public double getTotalMark(Remark remark) {
        Double sum = 0.0;

        double totalWeight = 0;
        int totalWeighting = 0;

        for (int j = 0; j < project.getCriterionList().size(); j++) {
            totalWeight = totalWeight + project.getCriterionList().get(j).getMaximumMark();
        }
        totalWeighting = Double.valueOf(totalWeight).intValue();

        for (int m = 0; m < remark.getAssessmentList().size(); m++) {
            sum = sum + remark.getAssessmentList().get(m).getScore();
        }
        sum = sum * (100.0 / totalWeighting);

        return sum;
    }

    public String getCriterionName(Assessment assessment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            if (project.getCriterionList().get(i).getId() == assessment.getCriterionId()) {
                return project.getCriterionList().get(i).getName();
            }
        }
        return "";
    }

    public double getCriterionMaxMark(Assessment assessment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            if (project.getCriterionList().get(i).getId() == assessment.getCriterionId()) {
                return project.getCriterionList().get(i).getMaximumMark();
            }
        }
        return 0;
    }

    public String getFieldName(SelectedComment selectedComment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            for (int j = 0; j < project.getCriterionList().get(i).getFieldList().size(); j++) {
                if (project.getCriterionList().get(i).getFieldList().get(j).getId() == selectedComment.getFieldId()) {
                    return project.getCriterionList().get(i).getFieldList().get(j).getName();
                }
            }
        }
        return "";
    }

    public String getExCommentName(SelectedComment selectedComment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            for (int j = 0; j < project.getCriterionList().get(i).getFieldList().size(); j++) {
                for (int k = 0; k < project.getCriterionList().get(i).getFieldList().get(j).getCommentList().size(); k++) {
                    for (int m = 0; m < project.getCriterionList().get(i).getFieldList().get(j).getCommentList().get(k).getExpandedCommentList().size(); m++) {
                        ExpandedComment expandedComment = project.getCriterionList().get(i).getFieldList().get(j).getCommentList().get(k).getExpandedCommentList().get(m);
                        if (expandedComment.getId() == selectedComment.getExCommentId()) {
                            return expandedComment.getText();
                        }
                    }
                }
            }
        }
        return "";
    }

    public double getAverageMark(ArrayList<Remark> remarkList) {
        double sumMark = 0;
        double markers = remarkList.size();
        for (int i = 0; i < markers; i++) {
            sumMark += getTotalMark(remarkList.get(i));
        }
        double avgMark = sumMark / markers;
        BigDecimal bigDecimal = new BigDecimal(avgMark);
        avgMark = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        return avgMark;
    }

    public String getFinalRemark(ArrayList<Remark> remarkList) {
        Remark remark = new Remark();
        for (int i = 0; i < remarkList.size(); i++) {
            if (remarkList.get(i).getId() == project.getPrincipalId()) {
                remark = remarkList.get(i);
            }
        }

        String finalRemark = "";
        for (int i = 0; i < remark.getAssessmentList().size(); i++) {
            finalRemark += "Critrion" + (i + 1) + ": " + getCriterionName(remark.getAssessmentList().get(i));
            finalRemark += "\n";
            for (int k = 0; k < remark.getAssessmentList().get(i).getSelectedCommentList().size(); k++) {
                finalRemark += getFieldName(remark.getAssessmentList().get(i).getSelectedCommentList().get(k)) +
                        " : " + getExCommentName(remark.getAssessmentList().get(i).getSelectedCommentList().get(k)) + "\n";
            }
        }
        return finalRemark;
    }
}

