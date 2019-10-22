package com.example.feedback;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dbclass.ProjectInfo;
import dbclass.StudentInfo;
import main.AllFunctions;

public class Activity_Realtime_Assessment extends AppCompatActivity {
    private Toolbar mToolbar;
    private ListView listView_projects;
    private ListView listView_students;
    private ArrayList<ProjectInfo> projectList;
    private int indexOfProject;
    private MyAdapter myAdapter;
    private TextView textView_duration_title;
    private TextView textView_numCandicateAndMarker;
    public static final String FROMREALTIME = "realtime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_assessment_page);
        initToolbar();
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("EEEE", "new realtime assessment");
        myAdapter.notifyDataSetChanged();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_realtime_assessment);
        mToolbar.setTitle("Assessment -- Welcome, " + AllFunctions.getObject().getUsername());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Realtime_Assessment.this, Activity_Homepage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Realtime_Assessment.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Realtime_Assessment.this,
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Activity_Realtime_Assessment.this, Activity_Homepage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void init() {
        projectList = AllFunctions.getObject().getProjectList();
        MyAdapterDefaultlistView myAdapterDefaultlistView = new MyAdapterDefaultlistView
                (Activity_Realtime_Assessment.this, projectList);
        listView_projects = findViewById(R.id.listView_projects_realtimeAssessment);
        listView_students = findViewById(R.id.listView_students_realtimeAssessment);
        listView_projects.setAdapter(myAdapterDefaultlistView);
        listView_projects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < adapterView.getChildCount(); i++)
                    adapterView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                view.setBackgroundColor(getResources().getColor(R.color.check));
                indexOfProject = position;
                ProjectInfo project = projectList.get(position);
                myAdapter = new MyAdapter(project.getStudentInfo(), Activity_Realtime_Assessment.this);
                listView_students.setAdapter(myAdapter);
                textView_duration_title = findViewById(R.id.textView_duration_realtimeAssessment);
                textView_duration_title.setText("Presentation duration: " + projectList.get(indexOfProject).getDurationMin() + ":" +
                        +projectList.get(indexOfProject).getDurationSec());
                textView_numCandicateAndMarker = findViewById(R.id.textView_numCandidatesMarkers_realtimeAssessment);
                int numStudentHasMarked = 0;
                for (int i = 0; i < projectList.get(indexOfProject).getStudentInfo().size(); i++) {
                    if (projectList.get(indexOfProject).getStudentInfo().get(i).getTotalMark() > 0.0)
                        numStudentHasMarked++;
                }
                textView_numCandicateAndMarker.setText(numStudentHasMarked + " of " +
                        projectList.get(indexOfProject).getStudentInfo().size() + " candidate(s) marked by " +
                        projectList.get(indexOfProject).getAssistant().size() + " marker(s)");
            }
        });
    }

    private void resetStudentListView() {
        listView_students.setAdapter(null);
        textView_duration_title.setText("");
        textView_numCandicateAndMarker.setText("");
    }


    public class MyAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<StudentInfo> studentList;

        public MyAdapter(ArrayList<StudentInfo> studentList, Context context) {
            this.studentList = studentList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return studentList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_student_withbutton, parent, false);

            TextView textView_groupNum = convertView.findViewById(R.id.textView_group_studentswithButton);
            if (studentList.get(position).getGroup() == -999)
                textView_groupNum.setText("");
            else
                textView_groupNum.setText(String.valueOf(studentList.get(position).getGroup()));
            TextView textView_studentID = convertView.findViewById(R.id.textView_studentID_studentsWithButton);
            textView_studentID.setText(studentList.get(position).getNumber());
            TextView textView_studentName = convertView.findViewById(R.id.textView_fullname_studentsWithButton);
            textView_studentName.setText(studentList.get(position).getFirstName() + " " + studentList.get(position).getMiddleName() + " " + studentList.get(position).getSurname());
            TextView textView_studentEmail = convertView.findViewById(R.id.textView_email_studentsWithButton);
            textView_studentEmail.setText(studentList.get(position).getEmail());
            Button button_start = convertView.findViewById(R.id.button_start_studentsWithButton);
            button_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Activity_Realtime_Assessment.this, Activity_Assessment.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfStudent", String.valueOf(position));
                    intent.putExtra("indexOfGroup", String.valueOf(studentList.get(position).getGroup()));
                    intent.putExtra("from", FROMREALTIME);
//                    resetStudentListView();
                    startActivity(intent);
                }
            });
            if (studentList.get(position).getTotalMark() > 0.0) {
                button_start.setVisibility(View.INVISIBLE);
                button_start.setEnabled(false);
                convertView.setEnabled(false);
                listView_students.setItemChecked(position, false);
            }

            if (listView_students.isItemChecked(position))
                convertView.setBackgroundColor(Color.parseColor("#D2EBF7"));
            else
                convertView.setBackgroundColor(Color.TRANSPARENT);
            return convertView;
        }
    }

    public class MyAdapterDefaultlistView extends BaseAdapter {

        private ArrayList<ProjectInfo> mProjectList;
        private Context mContext;

        public MyAdapterDefaultlistView(Context context, ArrayList<ProjectInfo> projectList) {
            this.mProjectList = projectList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mProjectList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_default, parent, false);
            TextView textView_listItem = (TextView) convertView.findViewById(R.id.textView_defaultView);
            textView_listItem.setText(mProjectList.get(position).getProjectName());
            return convertView;
        }
    }

}
