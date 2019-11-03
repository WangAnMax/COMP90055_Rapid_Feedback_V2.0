/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import main.AllFunctions;
import newdbclass.Project;
import newdbclass.ProjectStudent;
import newdbclass.Student;
import util.ExcelParser;
import util.FileUtils;

public class Activity_Student_Management extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE =
            {"android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"};
    private MyAdapter myAdapter;
    private ArrayList<ProjectStudent> students;
    private ListView listView;
    private int indexOfStudent = -999;
    private int indexOfProject;
    private Project project;
    private String path;
    private int projectId;
    private String studentID;
    private String firstName;
    private String middleName;
    private String surname;
    private String email;
    private String groupNumber;
    private Toolbar mToolbar;
    private AlertDialog dialog;
    private EditText editTextStudentID;
    private EditText editTextGivenname;
    private EditText editTextMiddleName;
    private EditText editTextFamilyname;
    private EditText editTextEmail;
    private EditText editTextGroup;
    private Handler handler;
    private String from;
    private Button saveButton;
    private ArrayList<Student> studentsExcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("index"));
        from = intent.getStringExtra("from");
        init();
        initToolbar();
    }

    public void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 108: //sync success
                        Toast.makeText(Activity_Student_Management.this,
                                "Sync success.", Toast.LENGTH_SHORT).show();
                        init();
                        break;
                    case 109: // sync fail
                        Toast.makeText(Activity_Student_Management.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    case 121:
                        Toast.makeText(getApplicationContext(), "Successfully add a student.", Toast.LENGTH_SHORT).show();
                        String action = msg.obj.toString();
                        if (action.equals("single")) {
                            dialog.dismiss();
                        }
                        AllFunctions.getObject().syncProjectList();
                        Collections.sort(project.getStudentList(), new SortByGroup());
                        break;
                    case 122:
                        Toast.makeText(getApplicationContext(), "Server error. Please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case 123:
                        Toast.makeText(getApplicationContext(), "Fail to add the student " + msg.arg1 + ". Please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case 124:
                        Toast.makeText(getApplicationContext(), "Successfully edit the info of a student.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        AllFunctions.getObject().syncProjectList();
                        Collections.sort(project.getStudentList(), new SortByGroup());
                        break;
                    case 125:
                        Toast.makeText(getApplicationContext(), "Fail to edit the student. Please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case 126:
                        Toast.makeText(getApplicationContext(), "Successfully delete the student.", Toast.LENGTH_SHORT).show();
                        AllFunctions.getObject().syncProjectList();
                        Collections.sort(project.getStudentList(), new SortByGroup());
                        break;
                    case 127:
                        Toast.makeText(getApplicationContext(), "Fail to delete the student. Please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
        saveButton = findViewById(R.id.button_save_student_management);
        if (from.equals(Activity_Assessment_Preparation.FROMPREVIOUSPROJECT)) {
            saveButton.setVisibility(View.INVISIBLE);
        }
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        projectId = project.getId();
        listView = findViewById(R.id.listView_ingroupStudent);
        students = project.getStudentList();
        myAdapter = new MyAdapter(students, this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_project_studetn_group);
        mToolbar.setTitle(project.getName() + " -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
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
                        Toast.makeText(Activity_Student_Management.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Student_Management.this,
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    //button delete click.
    public void deleteStudent(View view) {
        if (listView.getCheckedItemCount() == 1) {
            SparseBooleanArray checkedItemsStudents = listView.getCheckedItemPositions();
            int studentIndex = -1;
            if (checkedItemsStudents != null) {
                for (int i = 0; i < project.getStudentList().size(); i++) {
                    if (checkedItemsStudents.get(i) == true) {
                        studentIndex = i;
                        break;
                    }
                }
                AllFunctions.getObject().deleteStudent(projectId, students.get(studentIndex).getId());
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please choose only 1 student to delete.", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveStudentManagement(View view) {
        Intent intent = new Intent(Activity_Student_Management.this, Activity_Assessment_Preparation.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_student_group, parent, false);
            TextView textView_groupNum = convertView.findViewById(R.id.textView_groupnum_instudentlist);
            if (studentList.get(position).getGroupNumber() == 0) {
                textView_groupNum.setText("");
            } else {
                textView_groupNum.setText(String.valueOf(studentList.get(position).getGroupNumber()));
            }

            TextView textView_studentID = convertView.findViewById(R.id.textView_studentID_instudentlist);
            textView_studentID.setText(studentList.get(position).getStudentNumber() + "");
            TextView textView_studentName = convertView.findViewById(R.id.textView_fullname_instudentlist);
            textView_studentName.setText(studentList.get(position).getFirstName() + " " + studentList.get(position).getMiddleName() + " " + studentList.get(position).getLastName());
            TextView textView_studentEmail = convertView.findViewById(R.id.textView_email_instudentlist);
            textView_studentEmail.setText(studentList.get(position).getEmail());

            if (listView.isItemChecked(position)) {
                convertView.setBackgroundColor(Color.parseColor("#D2EBF7"));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }
    }

    private static final int FILE_SELECT_CODE = 0;

    public void importStudentManagement(View view) {
        try {
            verifyStoragePermissions(Activity_Student_Management.this);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            ex.printStackTrace();
        }
    }

    public void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.READ_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private static final String TAG = "ChooseFile";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Get the path
                    path = FileUtils.getPath(this, uri);
                    readStudentsExcel(project, path);
                    System.out.println("call the readExcel method: " + path);
                    init();
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void readStudentsExcel(Project project, String path) {
        ExcelParser excelParser = new ExcelParser();
        ArrayList<Student> students = new ArrayList<>();
        if (path.endsWith(".xls")) {
            students = excelParser.readXlsStudents(path);
        } else if (path.endsWith(".xlsx")) {
            students = excelParser.readXlsxStudents(path);
        }

        studentsExcel = students;
        AllFunctions.getObject().addStudent(projectId, studentsExcel, "batch");
    }

    //button addStudent click.
    public void addStudent(View v) {

        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Student_Management.this);//获得layoutInflater对象
        final View view = layoutInflater.from(Activity_Student_Management.this).inflate(R.layout.dialog_add_student, null);//获得view对象

        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Student_Management.this);
        builder.setView(view);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setTitle("Add Student");
        dialog.show();

        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        students = project.getStudentList();
        editTextStudentID = view.findViewById(R.id.editText_studentID_addStudent);
        editTextGivenname = view.findViewById(R.id.editText_firstName_addStudent);
        editTextMiddleName = view.findViewById(R.id.editText_middleName_addStudent);
        editTextFamilyname = view.findViewById(R.id.editText_surname_addStudent);
        editTextEmail = view.findViewById(R.id.editText_email_addStudent);
        editTextGroup = view.findViewById(R.id.editText_group_addStudent);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                studentID = editTextStudentID.getText().toString().trim();
                firstName = editTextGivenname.getText().toString().trim();
                middleName = editTextMiddleName.getText().toString().trim();
                surname = editTextFamilyname.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                groupNumber = editTextGroup.getText().toString().trim();
                if (groupNumber.equals("")) {
                    groupNumber = "0";
                }

                String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";

                if (studentID.equals("")) {
                    Toast.makeText(getApplicationContext(), "Student ID cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (firstName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Given name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (surname.equals("")) {
                    Toast.makeText(getApplicationContext(), "Family name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "Please input a valid Email", Toast.LENGTH_SHORT).show();
                } else {
                    if (!AllFunctions.getObject().hasStudent(projectId, Integer.parseInt(studentID))) {
                        ArrayList<Student> studentList = new ArrayList<>();
                        Student student = new Student(firstName, middleName, surname, email,
                                Integer.parseInt(studentID), Integer.parseInt(groupNumber));
                        studentList.add(student);
                        AllFunctions.getObject().addStudent(projectId, studentList, "single");
                    } else {
                        Toast.makeText(getApplicationContext(), "student with ID:" + studentID + " is already exits.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void helpStudentUpload(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Student_Management.this);
        final View view2 = layoutInflater.from(Activity_Student_Management.this).inflate(R.layout.dialog_help, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Student_Management.this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setView(view2);
        dialog.show();
        ImageView imageView = view2.findViewById(R.id.imageView_dialog_help);
        imageView.setBackgroundResource(R.drawable.students);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 1700;
        params.height = 800;
        dialog.getWindow().setAttributes(params);
    }

    public void editStudentInStudentManagement(View v) {

        if (listView.getCheckedItemCount() == 1) {
            SparseBooleanArray checkedItemsStudents = listView.getCheckedItemPositions();
            if (checkedItemsStudents != null) {
                for (int i = 0; i < project.getStudentList().size(); i++) {
                    if (checkedItemsStudents.get(i) == true) {
                        indexOfStudent = i;
                        break;
                    }
                }

                LayoutInflater layoutInflater = LayoutInflater.from(Activity_Student_Management.this);
                View view = layoutInflater.from(Activity_Student_Management.this).inflate(R.layout.dialog_add_student, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Student_Management.this);
                builder.setView(view);
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setTitle("Edit Student");
                dialog.show();

                project = AllFunctions.getObject().getProjectList().get(indexOfProject);
                students = project.getStudentList();
                editTextStudentID = view.findViewById(R.id.editText_studentID_addStudent);
                editTextStudentID.setEnabled(false);
                editTextStudentID.setText(students.get(indexOfStudent).getStudentNumber() + "");
                editTextGivenname = view.findViewById(R.id.editText_firstName_addStudent);
                editTextGivenname.setText(students.get(indexOfStudent).getFirstName());
                editTextMiddleName = view.findViewById(R.id.editText_middleName_addStudent);
                editTextMiddleName.setText(students.get(indexOfStudent).getMiddleName());
                editTextFamilyname = view.findViewById(R.id.editText_surname_addStudent);
                editTextFamilyname.setText(students.get(indexOfStudent).getLastName());
                editTextEmail = view.findViewById(R.id.editText_email_addStudent);
                editTextEmail.setText(students.get(indexOfStudent).getEmail());
                editTextGroup = view.findViewById(R.id.editText_group_addStudent);
                if (students.get(indexOfStudent).getGroupNumber() == 0) {
                    editTextGroup.setText("");
                } else {
                    editTextGroup.setText(students.get(indexOfStudent).getGroupNumber() + "");
                }

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        studentID = editTextStudentID.getText().toString().trim();
                        firstName = editTextGivenname.getText().toString().trim();
                        middleName = editTextMiddleName.getText().toString().trim();
                        surname = editTextFamilyname.getText().toString().trim();
                        email = editTextEmail.getText().toString().trim();
                        groupNumber = editTextGroup.getText().toString().trim();
                        if (groupNumber.equals("")) {
                            groupNumber = "0";
                        }

                        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";

                        if (studentID.equals("")) {
                            Toast.makeText(getApplicationContext(), "StudentID cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if (firstName.equals("")) {
                            Toast.makeText(getApplicationContext(), "FirstName cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if (surname.equals("")) {
                            Toast.makeText(getApplicationContext(), "LastName cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if (!email.matches(emailPattern)) {
                            Toast.makeText(getApplicationContext(), "Please input a valid Email", Toast.LENGTH_SHORT).show();
                        } else {
                            AllFunctions.getObject().editStudent(students.get(indexOfStudent).getId(), Integer.parseInt(studentID),
                                    firstName, middleName, surname, email, Integer.parseInt(groupNumber), projectId);
                        }
                    }
                });

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please choose only 1 student to edit.", Toast.LENGTH_SHORT).show();
        }
    }

    public class SortByGroup implements Comparator {

        public int compare(Object o1, Object o2) {
            ProjectStudent s1 = (ProjectStudent) o1;
            ProjectStudent s2 = (ProjectStudent) o2;
            if (s1.getGroupNumber() > s2.getGroupNumber() && s2.getGroupNumber() == 0) {
                return -1;
            } else if (s1.getGroupNumber() < s2.getGroupNumber() && s1.getGroupNumber() == 0) {
                return 1;
            } else if (s1.getGroupNumber() > s2.getGroupNumber()) {
                return 1;
            } else if (s1.getGroupNumber() == s2.getGroupNumber()) {
                return 1;
            } else return -1;
        }
    }
}