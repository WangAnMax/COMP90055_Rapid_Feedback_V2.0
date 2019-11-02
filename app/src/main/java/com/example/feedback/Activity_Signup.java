/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import main.AllFunctions;
import widget.LoginVideoView;

public class Activity_Signup extends Activity {

    Handler handler;
    private LoginVideoView mLoginVideoView;
    private EditText editText_FirstName;
    private EditText editText_MiddleName;
    private EditText editText_LastName;
    private EditText editText_email;
    private EditText editText_password;
    private EditText editText_passwordConfirm;
    private TextView textView_Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 101: //Sign Up failed.
                        Toast.makeText(Activity_Signup.this,
                                "Server error. Please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case 102:
                        Toast.makeText(Activity_Signup.this,
                                "The email address is already occupied. Please try another one.", Toast.LENGTH_SHORT).show();
                        break;
                    case 103: //means Sign Up successfully and go to login page
                        Toast.makeText(getApplicationContext(), "Sign Up successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Signup.this, Activity_Login.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
    }

    private void init() {
        textView_Login = findViewById(R.id.textView_login_insignup);
        textView_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Signup.this, Activity_Login.class);
                startActivity(intent);
                finish();
            }
        });

        mLoginVideoView = (LoginVideoView) findViewById(R.id.videoview1);
        mLoginVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.media));
        mLoginVideoView.start();
        mLoginVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mLoginVideoView.start();
            }
        });

        editText_FirstName = findViewById(R.id.editText_firstName_insignup);
        editText_MiddleName = findViewById(R.id.editText_middleName_insignup);
        editText_LastName = findViewById(R.id.editText_lastname_insignup);
        editText_email = findViewById(R.id.editText_email_insignup);
        editText_password = findViewById(R.id.editText_password_insignup);
        editText_passwordConfirm = findViewById(R.id.editText_confirmPassword_insignup);
    }

    public void signup(View view) {
        String firstName = editText_FirstName.getText().toString();
        String middleName = editText_MiddleName.getText().toString();
        String lastName = editText_LastName.getText().toString();
        String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();
        String passwordConfirm = editText_passwordConfirm.getText().toString();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";
        if (firstName.equals("")) {
            Toast.makeText(getApplicationContext(), "First name cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (lastName.equals("")) {
            Toast.makeText(getApplicationContext(), "Last name cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (password.equals("")) {
            Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (!(password.equals(passwordConfirm))) {
            Toast.makeText(getApplicationContext(), "Two passwords don't match", Toast.LENGTH_SHORT).show();
        } else if (email.matches(emailPattern)) {
            AllFunctions.getObject().register(firstName, middleName, lastName, email, password);
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Email format", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearFirstName(View view) {
        editText_FirstName.setText("");
    }

    public void clearMiddleName(View view) {
        editText_MiddleName.setText("");
    }

    public void clearLastName(View view) {
        editText_LastName.setText("");
    }

    public void clearEmail(View view) {
        editText_email.setText("");
    }

    public void clearPassword(View view) {
        editText_password.setText("");
    }

    public void clearConfirmedPassword(View view) {
        editText_passwordConfirm.setText("");
    }

    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("hasBackPressed", true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
