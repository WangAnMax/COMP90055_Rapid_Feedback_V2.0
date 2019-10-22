package com.example.feedback;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

import dbclass.Mark;
import dbclass.ProjectInfo;
import dbclass.StudentInfo;
import main.AllFunctions;

public class Activity_Editable_Individual_Report extends AppCompatActivity {
    private int indexOfProject;
    private int indexOfStudent;
    private int indexOfMark;
    private int indexOfGroup;
    private String from;
    private Toolbar mToolbar;
    public static final String FROMREALTIMEEDIT= "realtime_edit";
    public static final String FROMREVIEWEDIT = "review_edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editable_individual_report);
        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        indexOfMark = Integer.parseInt(intent.getStringExtra("indexOfMark"));
        from = intent.getStringExtra("from");
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_editable_individual_report);
        mToolbar.setTitle("Report -- Welcome, " + AllFunctions.getObject().getUsername());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Editable_Individual_Report.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Editable_Individual_Report.this,
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
        Log.d("EEEE", "edit individual report");
        ProjectInfo project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        StudentInfo student = AllFunctions.getObject().getProjectList().get(indexOfProject).getStudentInfo().get(indexOfStudent);
        Mark mark = AllFunctions.getObject().getMarkListForMarkPage().get(indexOfMark);

        Button button_finalReport = findViewById(R.id.button_finalReport_report);
        button_finalReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals(Activity_Display_Mark.FROMREALTIME)) {
                    Intent intent = new Intent(Activity_Editable_Individual_Report.this, Activity_Send_Report_Individual.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfMark", String.valueOf(indexOfMark));
                    intent.putExtra("from", FROMREALTIMEEDIT);
                    startActivity(intent);
                } else if (from.equals(Activity_Display_Mark.FROMREVIEW)) {
                    Intent intent = new Intent(Activity_Editable_Individual_Report.this, Activity_Send_Report_Individual.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfMark", String.valueOf(indexOfMark));
                    intent.putExtra("from", FROMREVIEWEDIT);
                    startActivity(intent);
                }
            }
        });
        if (!AllFunctions.getObject().getProjectList().get(indexOfProject).getAssistant().get(0).equals
                (AllFunctions.getObject().getMyEmail())) {
            button_finalReport.setVisibility(View.INVISIBLE);
        }

        ArrayList<Mark> markList = AllFunctions.getObject().getMarkListForMarkPage();
        for (int i = 0; i < markList.size(); i++) {
            if (markList.get(i).getTotalMark() == -999) {
                button_finalReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Activity_Editable_Individual_Report.this,
                                "Other markers are still marking. Please wait for a moment.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        Button button_editReport_individual = findViewById(R.id.button_edit_report);
        button_editReport_individual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals(Activity_Display_Mark.FROMREALTIME)) {
                    Intent intent = new Intent(Activity_Editable_Individual_Report.this, Activity_Assessment.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", "-999");
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREALTIMEEDIT);
                    AllFunctions.getObject().getProjectList().get(indexOfProject).getStudentInfo().get(indexOfStudent).setMark(mark);
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Display_Mark.FROMREVIEW)) {
                    Intent intent = new Intent(Activity_Editable_Individual_Report.this, Activity_Assessment.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", "-999");
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREVIEWEDIT);
                    AllFunctions.getObject().getProjectList().get(indexOfProject).getStudentInfo().get(indexOfStudent).setMark(mark);
                    startActivity(intent);
                    finish();
                }
            }
        });
        if (!mark.getLecturerEmail().equals(AllFunctions.getObject().getMyEmail())) {
            button_editReport_individual.setVisibility(View.INVISIBLE);
        }
        TextView textView_totalMark = findViewById(R.id.textView_totalMark_report);
        textView_totalMark.setText("Mark:" + (int) mark.getTotalMark() + "%");
        TextView textView_assessorName = findViewById(R.id.textView_assessorName_report);
        textView_assessorName.setText("Marker: " + mark.getLecturerName());
        String htmlString =
                "<html>" +
                        "<body>" +
                        "<h1 style=\"font-weight: normal\">" + project.getProjectName() + "</h1>" +
                        "<hr>" +
                        "<p>" + student.getFirstName() + " " + student.getMiddleName() + " " + student.getSurname() + " --- " + student.getNumber() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Subject</h2>" +
                        "<p>" + project.getSubjectCode() + " --- " + project.getSubjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Project</h2>" +
                        "<p>" + project.getProjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Mark</h2>" +
                        "<p>" + mark.getTotalMark() + "%</p >" +
                        "<h2 style=\"font-weight: normal\">Assessor</h2>" + "<p>";
        for (int i = 0; i < project.getAssistant().size(); i++)
            htmlString = htmlString + project.getAssistant().get(i) + "<br>";
        htmlString = htmlString +
                "</p >" +
                "<br><br><br><hr>" +
                "<div>" +
                "<h2 style=\"font-weight: normal\">MarkedCriteria</h2>" + "<p>";
        for (int i = 0; i < mark.getCriteriaList().size(); i++) {
            htmlString += "<h3 style=\"font-weight: normal\"><span style=\"float:left\">" + mark.getCriteriaList().get(i).getName() + "</span>" +
                    "<span style=\"float:right\">" + "  ---  " + mark.getMarkList().get(i) + "/" + Double.valueOf(mark.getCriteriaList().get(i).getMaximunMark()) + "</span></h3>";
            for (int j = 0; j < mark.getCriteriaList().get(i).getSubsectionList().size(); j++) {
                htmlString += "<p>" + mark.getCriteriaList().get(i).getSubsectionList().get(j).getName() +
                        " : " + mark.getCriteriaList().get(i).getSubsectionList().get(j).getShortTextList().get(0).getLongtext() + "</p >";
            }
            htmlString += "<br>";
        }

        htmlString +=
                "</div>" +
                        "</body>" +
                        "</html>";
        TextView textView_pdfContent = findViewById(R.id.textView_pdfContent_report);
        textView_pdfContent.setText(Html.fromHtml(htmlString));
    }
}
