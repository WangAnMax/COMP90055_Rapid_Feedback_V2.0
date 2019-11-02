/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package assessment;

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

import com.example.feedback.Activity_Homepage;
import com.example.feedback.Activity_Login;
import com.example.feedback.R;

import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Project;
import newdbclass.ProjectStudent;

public class Activity_Review_Report extends AppCompatActivity {
    private ListView listView_projects;
    private ListView listView_students;
    private ArrayList<Project> projectList;
    private int indexOfProject;
    private Toolbar mToolbar;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_report);
        initToolbar();
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_review_report);
        mToolbar.setTitle("Report -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Review_Report.this, Activity_Homepage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Review_Report.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Review_Report.this,
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
        Intent intent = new Intent(Activity_Review_Report.this, Activity_Homepage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        myAdapter.notifyDataSetChanged();
    }

    public void init() {
        projectList = AllFunctions.getObject().getProjectList();
        MyAdapterDefaultlistView myAdapterDefaultlistView = new MyAdapterDefaultlistView
                (Activity_Review_Report.this, projectList);
        listView_projects = findViewById(R.id.listView_projects_reviewReport);
        listView_students = findViewById(R.id.listView_students_reviewReport);
        listView_projects.setAdapter(myAdapterDefaultlistView);
        listView_projects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < adapterView.getChildCount(); i++)
                    adapterView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                view.setBackgroundColor(getResources().getColor(R.color.check));
                indexOfProject = position;
                Project project = projectList.get(position);
                myAdapter = new MyAdapter(project.getStudentList(), Activity_Review_Report.this);
                listView_students.setAdapter(myAdapter);
                listView_students.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    public class MyAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<ProjectStudent> studentList;

        public MyAdapter(ArrayList<ProjectStudent> studentList, Context context) {
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

            TextView textView_studentID = convertView.findViewById(R.id.textView_studentID_studentsWithButton);
            textView_studentID.setText(studentList.get(position).getStudentNumber() + "");
            TextView textView_studentGroup = convertView.findViewById(R.id.textView_group_studentswithButton);
            if (studentList.get(position).getGroupNumber() == 0)
                textView_studentGroup.setText("");
            else
                textView_studentGroup.setText(String.valueOf(studentList.get(position).getGroupNumber()));
            //test mark Num
            textView_studentID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Total Mark of " + studentList.get(position).getFirstName() + " is " + studentList.get(position).getFinalScore(), Toast.LENGTH_SHORT).show();
                }
            });
            TextView textView_studentName = convertView.findViewById(R.id.textView_fullname_studentsWithButton);
            textView_studentName.setText(studentList.get(position).getFirstName() + " " + studentList.get(position).getMiddleName() + " " + studentList.get(position).getLastName());
            TextView textView_studentEmail = convertView.findViewById(R.id.textView_email_studentsWithButton);
            textView_studentEmail.setText(studentList.get(position).getEmail());
            Button button_viewReport = convertView.findViewById(R.id.button_start_studentsWithButton);
            button_viewReport.setText("report");

            button_viewReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Activity_Review_Report.this, Activity_Display_Mark.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfStudent", String.valueOf(position));
                    intent.putExtra("indexOfGroup", String.valueOf(studentList.get(position).getGroupNumber()));
                    intent.putExtra("from", "review");
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    public class MyAdapterDefaultlistView extends BaseAdapter {

        private ArrayList<Project> mProjectList;
        private Context mContext;

        public MyAdapterDefaultlistView(Context context, ArrayList<Project> projectList) {
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
            textView_listItem.setText(mProjectList.get(position).getName());
            return convertView;
        }
    }
}
