/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.feedback.Activity_Record_Voice;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import main.AllFunctions;
import newdbclass.ProjectStudent;
import util.RecordingDatabaseHelper;

public class RecordingService extends Service {

    private static final String LOG_TAG = "RecordingService";
    private String mFileName = null;
    private String mFilePath = null;
    private MediaRecorder mRecorder = null;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private RecordingDatabaseHelper mDatabase;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;
    private int indexOfProject;
    private int indexOfStudent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new RecordingDatabaseHelper(this, "RecordingStore", null, 1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }

    public void startRecording() {
        setFileNameAndPath();
        //set parameters for voice
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(192000);

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {

        }
    }

    public void setFileNameAndPath() {
        File f;
        do {
            mFileName = AllFunctions.getObject().getProjectList().
                    get(indexOfProject).getSubjectCode() + "_" +
                    AllFunctions.getObject().getProjectList().
                            get(indexOfProject).getStudentList().
                            get(indexOfStudent).getFirstName() + ".mp4";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/feedback/" + mFileName;
            fileIsExists(mFilePath);
            f = new File(mFilePath);
        } while (f.exists() && !f.isDirectory());
    }

    public void stopRecording() {
        mRecorder.stop();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.release();
//        Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();

        //remove notification
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        String project = AllFunctions.getObject().getProjectList().
                get(indexOfProject).getSubjectCode();
        ProjectStudent student = AllFunctions.getObject().getProjectList().
                get(indexOfProject).getStudentList().
                get(indexOfStudent);
        String email = student.getEmail();
        RecordingItem item = new RecordingItem();
        item.setFilePath(mFilePath);
        item.setName(mFileName);
        item.setLength((int) mElapsedMillis);
        student.setRecordingItem(item);
        //if exist in the database delete the file;
        //mDatabase.deleteRecording(project,email);
        mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis, email, project);
        mRecorder = null;
    }

    private void startTimer() {
        mTimer = new Timer();
        mIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;
                if (onTimerChangedListener != null)
                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
                NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mgr.notify(1, createNotification());
            }
        };
        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
    }

    //TODO:
    private Notification createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_mic_white_36dp)
                        .setContentTitle(getString(R.string.notification_recording))
                        .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
                        .setOngoing(true);

        mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
                new Intent[]{new Intent(getApplicationContext(), Activity_Record_Voice.class)}, 0));

        return mBuilder.build();
    }
}
