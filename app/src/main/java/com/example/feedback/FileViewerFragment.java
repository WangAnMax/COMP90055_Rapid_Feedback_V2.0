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

import dbclass.StudentInfo;
import main.AllFunctions;

//import com.example.feedback.FileViewerAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FileViewerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FileViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileViewerFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = "FileViewerFragment";
    private int indexOfProject;
    private int indexOfStudent;
    private int position;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);
        Button submit = v.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AllFunctions.getObject().submitRecorder();
                } catch (Exception e) {

                }
            }
        });

        ImageButton mfile = (ImageButton) v.findViewById(R.id.file);
        mfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StudentInfo student = AllFunctions.getObject().getProjectList().
                            get(indexOfProject).getStudentInfo().
                            get(indexOfStudent);
                    if (student.getRecordingItem() == null) {
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
