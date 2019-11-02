/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import assessment.Activity_Realtime_Assessment;
import assessment.Activity_Review_Report;
import main.AllFunctions;
import pl.droidsonroids.gif.GifImageView;

public class Activity_Homepage extends AppCompatActivity {

    private GifImageView gifImageView;
    private Toolbar mToolbar;
    private Handler handler;
    private String to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        initToolbar();
        init();
        gifImageView = findViewById(R.id.geogif);
    }

    public void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 108:
                        Toast.makeText(Activity_Homepage.this,
                                "Sync success.", Toast.LENGTH_SHORT).show();
                        if (to.equals("part1")) {
                            Intent intent = new Intent(Activity_Homepage.this, Activity_Assessment_Preparation.class);
                            startActivity(intent);
                        } else if (to.equals("part2")) {
                            Intent intent = new Intent(Activity_Homepage.this, Activity_Realtime_Assessment.class);
                            startActivity(intent);
                        } else if (to.equals("part3")) {
                            Intent intent = new Intent(Activity_Homepage.this, Activity_Review_Report.class);
                            startActivity(intent);
                        }
                        break;
                    case 109:
                        Toast.makeText(Activity_Homepage.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
    }

    public void onNewIntent(Intent intent) {
        initToolbar();
        init();
    }

    public void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_homepage);
        mToolbar.setTitle("Rapid Feedback -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Homepage.this, Activity_Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Homepage.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Homepage.this,
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

    public void toPart1(View view) {
        to = "part1";
        AllFunctions.getObject().syncProjectList();
    }

    public void toPart2(View view) {
        to = "part2";
        AllFunctions.getObject().syncProjectList();
    }

    public void toPart3(View view) {
        to = "part3";
        AllFunctions.getObject().syncProjectList();
    }

    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("hasBackPressed", true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
