/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Project;
import newdbclass.ProjectStudent;

public class Activity_Record_Voice extends AppCompatActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    int indexOfProject;
    int indexOfStudent;
    int indexOfGroup;
    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_voice);
        pager = findViewById(R.id.pager);
        pager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabs = findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 10);
        } else {
            Toast.makeText(this, "Start to record the audio", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Successfully get record audio permission", Toast.LENGTH_SHORT).show();
            } else {
                //User denied Permission.
                Toast.makeText(this, "Fail to get record audio permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // set two get method. it is convenient for acquire two index in the fragment
    public int getIndexOfProject() {
        return indexOfProject;
    }

    public int getIndexOfStudent() {
        return indexOfStudent;
    }

    public int getIndexOfGroup() {
        return indexOfGroup;
    }

    public class MyAdapter extends FragmentPagerAdapter {
        private String[] titles = {getString(R.string.tab_title_record), getString(R.string.tab_title_saved_recordings)};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    return RecordFragement.newInstance(position);
                }
                case 1: {
                    return FileViewerFragment.newInstance(position);
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
