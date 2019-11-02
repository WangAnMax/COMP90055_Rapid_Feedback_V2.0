/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import main.AllFunctions;
import newdbclass.Project;

public class Activity_About extends AppCompatActivity {

    private int durationMin, durationSec, warningMin, warningSec;
    private String projectName, subjectName, subjectCode, projectDes;
    private String index;
    private Project project;
    private Handler handler;
    private AlertDialog dialog;
    private EditText editText_projectName;
    private EditText editText_subjectName;
    private EditText editText_subjectCode;
    private EditText editText_projectDes;
    private Toolbar mToolbar;
    private EditText editText_durationMin;
    private EditText editText_durationSec;
    private EditText editText_warningMin;
    private EditText editText_warningSec;
    private Button button_plus_duration_minutes;
    private Button button_minus_duration_minutes;
    private Button button_plus_duration_seconds;
    private Button button_minus_duration_seconds;
    private Button button_plus_warning_minutes;
    private Button button_minus_warning_minutes;
    private Button button_plus_warning_seconds;
    private Button button_minus_warning_seconds;
    private Button button_next;
    private int totalDurationSec;
    private int totalWarningSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Intent intent = getIntent();
        index = intent.getStringExtra("index");
        init(Integer.parseInt(index));
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_project_about);
        mToolbar.setTitle("Project -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                discardWarning();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_About.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_About.this,
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

    public void discardWarning() {
        AllFunctions.getObject().setHandler(handler); // attention!!!!!!!

        LayoutInflater layoutInflater = LayoutInflater.from(Activity_About.this);
        final View view = layoutInflater.from(Activity_About.this).
                inflate(R.layout.dialog_quit_editing, null);
        TextView warning = view.findViewById(R.id.textView_dialog_query_waring_editing);
        warning.setText("Are you sure to discard all changes ?");
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_About.this);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllFunctions.getObject().syncProjectList();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void bindView() {
        button_plus_duration_minutes = findViewById(R.id.button_addDurationMin_inTimer);
        button_minus_duration_minutes = findViewById(R.id.button_durationMinMinus_Timer);
        button_plus_duration_seconds = findViewById(R.id.button_addDurationSec_inTimer);
        button_minus_duration_seconds = findViewById(R.id.button_durationSecMinus_Timer);
        button_plus_warning_minutes = findViewById(R.id.button_addWarningMin_inTimer);
        button_minus_warning_minutes = findViewById(R.id.button_warningMinMinus_Timer);
        button_plus_warning_seconds = findViewById(R.id.button_addWarningSec_inTimer);
        button_minus_warning_seconds = findViewById(R.id.button_warningSecMinus_Timer);
        button_next = findViewById(R.id.button_next_about);
        button_next.setText(R.string.save_button);
        editText_projectName = findViewById(R.id.editText_projectname_inabout);
        editText_subjectName = findViewById(R.id.editText_subjectname_inabout);
        editText_subjectCode = findViewById(R.id.editText_subjectcode_inabout);
        editText_projectDes = findViewById(R.id.editText_projectdescription_inabout);
        editText_durationMin = findViewById(R.id.editText_durationMin_Timer);
        editText_durationMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    button_plus_duration_minutes.setEnabled(false);
                    button_plus_duration_minutes.setBackgroundResource(R.drawable.ic_add_disabled);
                    button_minus_duration_minutes.setEnabled(false);
                    button_minus_duration_minutes.setBackgroundResource(R.drawable.ic_delete_disabled);
                } else {
                    button_plus_duration_minutes.setEnabled(true);
                    button_plus_duration_minutes.setBackgroundResource(R.drawable.ic_add);
                    button_minus_duration_minutes.setEnabled(true);
                    button_minus_duration_minutes.setBackgroundResource(R.drawable.ic_delete);
                }
            }
        });
        editText_durationSec = findViewById(R.id.editText_durationSec_Timer);
        editText_durationSec.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    button_plus_duration_seconds.setEnabled(false);
                    button_plus_duration_seconds.setBackgroundResource(R.drawable.ic_add_disabled);
                    button_minus_duration_seconds.setEnabled(false);
                    button_minus_duration_seconds.setBackgroundResource(R.drawable.ic_delete_disabled);
                } else {
                    button_plus_duration_seconds.setEnabled(true);
                    button_plus_duration_seconds.setBackgroundResource(R.drawable.ic_add);
                    button_minus_duration_seconds.setEnabled(true);
                    button_minus_duration_seconds.setBackgroundResource(R.drawable.ic_delete);
                }
            }
        });
        editText_warningMin = findViewById(R.id.editText_warningMin_Timer);
        editText_warningMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    button_plus_warning_minutes.setEnabled(false);
                    button_plus_warning_minutes.setBackgroundResource(R.drawable.ic_add_disabled);
                    button_minus_warning_minutes.setEnabled(false);
                    button_minus_warning_minutes.setBackgroundResource(R.drawable.ic_delete_disabled);
                } else {
                    button_plus_warning_minutes.setEnabled(true);
                    button_plus_warning_minutes.setBackgroundResource(R.drawable.ic_add);
                    button_minus_warning_minutes.setEnabled(true);
                    button_minus_warning_minutes.setBackgroundResource(R.drawable.ic_delete);
                }
            }
        });
        editText_warningSec = findViewById(R.id.editText_warningSec_Timer);
        editText_warningSec.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    button_plus_warning_seconds.setEnabled(false);
                    button_plus_warning_seconds.setBackgroundResource(R.drawable.ic_add_disabled);
                    button_minus_warning_seconds.setEnabled(false);
                    button_minus_warning_seconds.setBackgroundResource(R.drawable.ic_delete_disabled);
                } else {
                    button_plus_warning_seconds.setEnabled(true);
                    button_plus_warning_seconds.setBackgroundResource(R.drawable.ic_add);
                    button_minus_warning_seconds.setEnabled(true);
                    button_minus_warning_seconds.setBackgroundResource(R.drawable.ic_delete);
                }
            }
        });
    }

    private void init(int index) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        project = AllFunctions.getObject().getProjectList().get(index);
        bindHandler();
        initToolbar();
        bindView();
        getTimeOfProject();
        mToolbar.setTitle(project.getName());
        editText_projectName.setText(project.getName());
        editText_projectName.setEnabled(false);
        editText_subjectName.setText(project.getSubjectName());
        editText_subjectCode.setText(project.getSubjectCode());
        editText_projectDes.setText(project.getDescription());
        editText_durationMin.setText(String.valueOf(durationMin));
        editText_durationSec.setText(String.valueOf(durationSec));
        editText_warningMin.setText(String.valueOf(warningMin));
        editText_warningSec.setText(String.valueOf(warningSec));
    }

    private void bindHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 108:
                        Toast.makeText(Activity_About.this,
                                "Sync success.", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                        break;
                    case 109:
                        Toast.makeText(Activity_About.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    case 110:
                        Toast.makeText(Activity_About.this,
                                "Successfully update the project.", Toast.LENGTH_SHORT).show();
                        AllFunctions.getObject().syncProjectList();
                        break;
                    case 111:
                        Toast.makeText(Activity_About.this,
                                "Fail to update the project.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };

        AllFunctions.getObject().setHandler(handler);
    }

    public void addDurationMin(View view) {
        durationMin = Integer.parseInt(editText_durationMin.getText().toString());
        durationMin++;
        if (durationMin > 59)
            durationMin = durationMin - 60;
        editText_durationMin.setText(String.valueOf(durationMin));
    }

    public void minusDurationMin(View view) {
        durationMin = Integer.parseInt(editText_durationMin.getText().toString());
        durationMin--;
        if (durationMin < 0)
            durationMin = durationMin + 60;
        editText_durationMin.setText(String.valueOf(durationMin));
    }

    public void addDurationSec(View view) {
        durationSec = Integer.parseInt(editText_durationSec.getText().toString());
        durationSec = durationSec + 5;
        if (durationSec > 59)
            durationSec = durationSec - 60;
        editText_durationSec.setText(String.valueOf(durationSec));
    }

    public void minusDurationSec(View view) {
        durationSec = Integer.parseInt(editText_durationSec.getText().toString());
        durationSec = durationSec - 5;
        if (durationSec < 0)
            durationSec = durationSec + 60;
        editText_durationSec.setText(String.valueOf(durationSec));
    }

    public void addWarningMin(View view) {
        warningMin = Integer.parseInt(editText_warningMin.getText().toString());
        warningMin++;
        if (warningMin > 59)
            warningMin = warningMin - 60;
        editText_warningMin.setText(String.valueOf(warningMin));
    }

    public void minusWarningMin(View view) {
        warningMin = Integer.parseInt(editText_warningMin.getText().toString());
        warningMin--;
        if (warningMin < 0)
            warningMin = warningMin + 60;
        editText_warningMin.setText(String.valueOf(warningMin));
    }

    public void addWarningSec(View view) {
        warningSec = Integer.parseInt(editText_warningSec.getText().toString());
        warningSec = warningSec + 5;
        if (warningSec > 59)
            warningSec = warningSec - 60;
        editText_warningSec.setText(String.valueOf(warningSec));
    }

    public void minusWarningSec(View view) {
        warningSec = Integer.parseInt(editText_warningSec.getText().toString());
        warningSec = warningSec - 5;
        if (warningSec < 0)
            warningSec = warningSec + 60;
        editText_warningSec.setText(String.valueOf(warningSec));
    }

    private void getTimeOfProject() {
        durationMin = project.getDurationMin();
        durationSec = project.getDurationSec();
        warningMin = project.getWarningMin();
        warningSec = project.getWarningSec();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    //save button click
    public void nextAbout(View view) {
        bindHandler();
        projectName = editText_projectName.getText().toString();
        subjectName = editText_subjectName.getText().toString();
        subjectCode = editText_subjectCode.getText().toString();
        projectDes = editText_projectDes.getText().toString();

        if (projectName.equals("")) {
            Toast.makeText(getApplicationContext(), "Project name cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (subjectCode.equals("")) {
            Toast.makeText(getApplicationContext(), "Subject code cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (subjectName.equals("")) {
            Toast.makeText(getApplicationContext(), "Subject name cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            if (checkTimeSetting()) {
                totalDurationSec = 60 * durationMin + durationSec;
                totalWarningSec = 60 * warningMin + warningSec;
                project = AllFunctions.getObject().getProjectList().get(Integer.parseInt(index));
                AllFunctions.getObject().updateProject(projectName, subjectName, subjectCode, projectDes, totalDurationSec, totalWarningSec, project.getId());
            }
        }
    }

    private boolean checkTimeSetting() {
        if (Integer.parseInt(editText_durationMin.getText().toString()) == 0
                && Integer.parseInt(editText_durationSec.getText().toString()) == 0) {
            Toast.makeText(getApplicationContext(), "Duration time cannot be zero", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(editText_warningMin.getText().toString()) == 0
                && Integer.parseInt(editText_warningSec.getText().toString()) == 0) {
            Toast.makeText(getApplicationContext(), "Warning time cannot be zero", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            durationMin = Integer.parseInt(editText_durationMin.getText().toString());
            durationSec = Integer.parseInt(editText_durationSec.getText().toString());
            warningMin = Integer.parseInt(editText_warningMin.getText().toString());
            warningSec = Integer.parseInt(editText_warningSec.getText().toString());
            if (durationMin < 0 || durationMin > 59 || durationSec < 0 || durationSec > 59 || warningMin < 0 || warningMin > 59 || warningSec < 0 || warningSec > 59) {
                Toast.makeText(getApplicationContext(), "Minutes & Seconds must between 0~59", Toast.LENGTH_SHORT).show();
                return false;
            } else if (durationMin < warningMin) {
                Toast.makeText(getApplicationContext(), "Duration time cannot be less that warning time", Toast.LENGTH_SHORT).show();
                return false;
            } else if (durationMin == warningMin && durationSec < warningSec) {
                Toast.makeText(getApplicationContext(), "Duration time cannot be less that warning time", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    public void onBackPressed() {
        discardWarning();
    }
}
