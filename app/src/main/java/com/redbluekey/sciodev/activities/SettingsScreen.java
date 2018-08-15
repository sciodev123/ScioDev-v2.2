package com.redbluekey.sciodev.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.helpers.LocalStorage;

public class SettingsScreen extends Activity {

    ImageButton btnBack;
    TextView emailPassword;
    TextView logout;
    TextView signup;
    TextView login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initActions();
    }

    private void initActions() {
        btnBack = findViewById(R.id.btn_settings_back);
        emailPassword = findViewById(R.id.email_password_settings);
        logout = findViewById(R.id.logout_settings);
        signup = findViewById(R.id.signup_settings);
        login = findViewById(R.id.login_settings);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String username = LocalStorage.getAuthData(this)[1];
        if (!username.equals("")) {
            signup.setVisibility(View.GONE);
            login.setVisibility(View.GONE);

            emailPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent updateProfileScreen = new Intent(getApplicationContext(),
                            UpdateProfileScreen.class);
                    updateProfileScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(updateProfileScreen);
                }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });
        } else {
            emailPassword.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signupScreen = new Intent(getApplicationContext(), SignupScreen.class);
                    signupScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(signupScreen);
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginScreen = new Intent(getApplicationContext(), LoginScreen.class);
                    loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(loginScreen);
                }
            });
        }
    }

    private void logout() {
        LocalStorage.clearData(this);
        finish();
        Toast.makeText(this, "Successfully logged out!", Toast.LENGTH_SHORT).show();
    }
}
