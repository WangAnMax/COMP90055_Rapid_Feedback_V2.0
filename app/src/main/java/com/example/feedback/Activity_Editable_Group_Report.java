package com.example.feedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Activity_Editable_Group_Report extends AppCompatActivity {
    private int indexOfProject;
    private int indexOfGroup;
    private int indexOfMark;
    private int indexOfStudent;
    private ArrayList<StudentInfo> studentInfoArrayList;
    private Toolbar mToolbar;
    private String from;
    public static final String FROMREALTIMEEDIT= "realtime_edit";
    public static final String FROMREVIEWEDIT= "review_edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editable_group_report);
        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        ProjectInfo project = AllFunctions.
                getObject().getProjectList().get(indexOfProject);
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        indexOfMark = Integer.parseInt(intent.getStringExtra("indexOfMark"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        from = intent.getStringExtra("from");
        studentInfoArrayList = new ArrayList<StudentInfo>();
        for (int i = 0; i < project.getStudentInfo().size(); i++) {
            if (project.getStudentInfo().get(i).getGroup() == indexOfGroup)
                studentInfoArrayList.add(project.getStudentInfo().get(i));
        }
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_editable_group_report);
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
        Log.d("EEEE", "edit group report");
        ProjectInfo project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        Mark mark = AllFunctions.getObject().getMarkListForMarkPage().get(indexOfMark);

        Button button_finalReport = findViewById(R.id.button_finalReport_groupReport);
        button_finalReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        if (!AllFunctions.getObject().getProjectList().get(indexOfProject).getAssistant().get(0).equals
                (AllFunctions.getObject().getMyEmail())) {
            button_finalReport.setVisibility(View.INVISIBLE);
        }

        Button button_edit = findViewById(R.id.button_edit_groupReport);
        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals(Activity_Display_Mark.FROMREALTIME)) {
                    Intent intent = new Intent(Activity_Editable_Group_Report.this, Activity_Assessment.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREALTIMEEDIT);
                    for (int i = 0; i < project.getStudentInfo().size(); i++) {
                        if (project.getStudentInfo().get(i).getGroup() == indexOfGroup)
                            project.getStudentInfo().get(i).setMark(mark);
                    }
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Display_Mark.FROMREVIEW)) {
                    Intent intent = new Intent(Activity_Editable_Group_Report.this, Activity_Assessment.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREVIEWEDIT);
                    for (int i = 0; i < project.getStudentInfo().size(); i++) {
                        if (project.getStudentInfo().get(i).getGroup() == indexOfGroup)
                            project.getStudentInfo().get(i).setMark(mark);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        });
        if (!mark.getLecturerEmail().equals(AllFunctions.getObject().getMyEmail())) {
            button_edit.setVisibility(View.INVISIBLE);
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
                        "<p>" + "Group: " + indexOfGroup + "</p >" +
                        "<h2 style=\"font-weight: normal\">Subject</h2>" +
                        "<p>" + project.getSubjectCode() + " --- " + project.getSubjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Project</h2>" +
                        "<p>" + project.getProjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Mark Attained</h2>" +
                        "<p>" + mark.getTotalMark() + "%</p >" +
                        "<h2 style=\"font-weight: normal\">Assessor</h2>" + "<p>";
        for (int i = 0; i < project.getAssistant().size(); i++)
            htmlString = htmlString + project.getAssistant().get(i) + "<br>";

        htmlString += "<h2 style=\"font-weight: normal\">Students</h2>" + "<p>";
        for (int i = 0; i < studentInfoArrayList.size(); i++)
            htmlString = htmlString + studentInfoArrayList.get(i).getNumber() + "---" + studentInfoArrayList.get(i).getFirstName() + " " + studentInfoArrayList.get(i).getMiddleName() + " " + studentInfoArrayList.get(i).getSurname() + "<br>";

        htmlString = htmlString +
                "</p >" +
                "<br><br><br><hr>" +
                "<div>";

        htmlString += "<h2 style=\"font-weight: normal\">CommentOnlyCriteria</h2>" + "<p>";
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
        TextView textView_pdfContent = findViewById(R.id.textView_pdfContent_GroupReport);
        textView_pdfContent.setText(Html.fromHtml(htmlString));
    }

}

