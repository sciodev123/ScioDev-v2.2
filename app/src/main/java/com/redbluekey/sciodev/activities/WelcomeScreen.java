package com.redbluekey.sciodev.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.helpers.LocalStorage;

public class WelcomeScreen extends AppCompatActivity {

    public static String AUTH_TOKEN = "";

    Button btnLogin;
    Button btnSignup;
    Button btnSkip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
        String[] authData = LocalStorage.getAuthData(this);
        AUTH_TOKEN = authData[0];
        if (!AUTH_TOKEN.equals("")) {
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(mainActivity);
            Toast.makeText(this, "Welcome, " + authData[1] + "!", Toast.LENGTH_LONG).show();

        } else {
            initButtons();
        }
    }

    private void initButtons() {
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);
        btnSkip = findViewById(R.id.btn_skip_for_now);

        /*
        Skip button click listener
         */
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mainActivity);
            }
        });
        /*
        Login button click listener
         */
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginScreen = new Intent(getApplicationContext(), LoginScreen.class);
                loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(loginScreen);
            }
        });
        /*
        Signup button click listener
         */
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupScreen = new Intent(getApplicationContext(), SignupScreen.class);
                signupScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(signupScreen);
            }
        });
    }
}
