/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Project;
import newdbclass.ProjectStudent;
import util.RecordingDatabaseHelper;

public class FileViewerFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = "FileViewerFragment";
    private int indexOfProject;
    private int indexOfStudent;
    private int indexOfGroup;
    private int position;
    private ProjectStudent student;
    private Project project;
    private RecordingDatabaseHelper mDatabase;
    private static RecordingItem recordingItem;
    private ArrayList<ProjectStudent> studentInfoArrayList;

    public static FileViewerFragment newInstance(int position) {
        FileViewerFragment f = new FileViewerFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        Activity_Record_Voice activity = (Activity_Record_Voice) getActivity();
        indexOfProject = activity.getIndexOfProject();
        indexOfStudent = activity.getIndexOfStudent();
        indexOfGroup = activity.getIndexOfGroup();
        student = AllFunctions.getObject().getProjectList().
                get(indexOfProject).getStudentList().
                get(indexOfStudent);
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        studentInfoArrayList = new ArrayList<ProjectStudent>();
        for (int i = 0; i < project.getStudentList().size(); i++) {
            if (project.getStudentList().get(i).getGroupNumber() == indexOfGroup)
                studentInfoArrayList.add(project.getStudentList().get(i));
        }
        mDatabase = new RecordingDatabaseHelper(getContext(), "RecordingStore", null, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);
        Button submit = v.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    for (int i = 0; i < studentInfoArrayList.size(); i++) {
                        AllFunctions.getObject().submitRecorder(project.getId(), studentInfoArrayList.get(i).getId(), student.getRecordingItem().getFilePath());
                    }
                } catch (Exception e) {

                }
            }
        });

        ImageButton mfile = (ImageButton) v.findViewById(R.id.file);
        mfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String email = student.getEmail();
                    recordingItem = new RecordingItem();
                    student.setRecordingItem(mDatabase.getItemAt(project.getSubjectCode(), email, recordingItem));
                    if (student.getRecordingItem() == null) {
                        Toast.makeText(getActivity(), "there is no file of your record", Toast.LENGTH_LONG).show();
                    } else if (student.getRecordingItem().getName() == null) {
                        Toast.makeText(getActivity(), "there is no file of your record", Toast.LENGTH_LONG).show();
                    } else {
                        PlaybackFragment playbackFragment =
                                new PlaybackFragment().newInstance(student.getRecordingItem());

                        FragmentTransaction transaction = ((FragmentActivity) getActivity())
                                .getSupportFragmentManager()
                                .beginTransaction();

                        playbackFragment.show(transaction, "dialog_playback");
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "exception", e);
                }
            }
        });
        return v;
    }
}