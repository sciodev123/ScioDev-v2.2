package com.redbluekey.sciodev.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.helpers.ConectivityStatus;
import com.redbluekey.sciodev.helpers.HttpClient;
import com.redbluekey.sciodev.helpers.LocalStorage;
import com.redbluekey.sciodev.models.LoginRequest;
import com.redbluekey.sciodev.models.LoginSignupResponse;
import com.redbluekey.sciodev.util.TextInputLayoutValidatorAdapter;

import java.util.List;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

    private static final String TAG = "LoginScreen";

    private static final String
            LOGIN_URL = "http://api.redbluekey.com/api/user/PostUserLogin?loginSource=Android";

    @NotEmpty
    TextInputLayout loginUsernameWrapper;
    @NotEmpty
    TextInputLayout loginPasswordWrapper;

    Validator validator;

    TextView forgotLink;
    TextView loginErrorMessage;
    TextView signupLink;
    ImageButton closeButton;
    Button submitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        if (args != null && args.getBoolean(SignupScreen.IS_FROM_SIGNUP)) {
            final String username = args.getString(SignupScreen.USERNAME);
            final String password = args.getString(SignupScreen.PASSWORD);
            new Runnable() {
                @Override
                public void run() {
                    new HttpRequestLogin().execute(username, password);
                }
            }.run();
        } else {
            setContentView(R.layout.login_screen);
            loginUsernameWrapper = findViewById(R.id.login_usernameWrapper);
            loginPasswordWrapper = findViewById(R.id.login_passwordWrapper);

            loginErrorMessage = findViewById(R.id.login_error_message);

            setOnChangeListener(loginUsernameWrapper);
            setOnChangeListener(loginPasswordWrapper);

            initForgotLink();
            initSubmitLink();
            initSubmitButton();
            initValidator();
            initCloseButton();
        }
    }

    private void initValidator() {
        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutValidatorAdapter());
        validator.setViewValidatedAction(new Validator.ViewValidatedAction() {
            @Override
            public void onAllRulesPassed(View view) {
                if (view instanceof TextInputLayout) {
                    TextInputLayout input = (TextInputLayout) view;
                    if (input.getId() == R.id.login_passwordWrapper) {
                        input.setError(null);
                    } else {
                        input.setErrorEnabled(false);
                    }
                }
            }
        });
    }

    private void setOnChangeListener(final TextInputLayout inputField) {
        inputField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableLoginButton();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void initForgotLink() {
        forgotLink = findViewById(R.id.forgot_password);
        forgotLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotDialog = new Intent(getApplicationContext(), ResetPasswordDialog.class);
                forgotDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(forgotDialog);
            }
        });
    }

    private void initSubmitLink() {
        signupLink = findViewById(R.id.q_new_user);
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupScreen = new Intent(getApplicationContext(), SignupScreen.class);
                signupScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(signupScreen);
            }
        });
    }

    private void initSubmitButton() {
        submitButton = findViewById(R.id.btn_form_login);
        submitButton.setOnClickListener(this);
        disableLoginButton();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void initCloseButton() {
        closeButton = findViewById(R.id.btn_login_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void enableLoginButton() {
        submitButton.setEnabled(true);
        submitButton.setAlpha(1.0f);
        submitButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        submitButton.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void disableLoginButton() {
        submitButton.setEnabled(false);
        submitButton.setAlpha(0.7f);
        submitButton.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        submitButton.setTextColor(getResources().getColor(R.color.colorDarkGrey));
    }

    private void showLoginErrorMessage(String error) {
        loginErrorMessage.setText(error);
        loginErrorMessage.setVisibility(View.VISIBLE);
    }

    private void hideLoginErrorMessage() {
        loginErrorMessage.setText(null);
        loginErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void saveAuthToken(final String token) {
        String username = loginUsernameWrapper != null ?
                loginUsernameWrapper.getEditText().getText().toString() :
                getIntent().getExtras().getString(SignupScreen.USERNAME);
        LocalStorage.saveAuthData(this, token, username);
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(mainActivity);
        Toast.makeText(this, "Welcome back, " + username  + "!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        hideLoginErrorMessage();
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        if (ConectivityStatus.hasInternetConnection(this)) {
            final String username = loginUsernameWrapper.getEditText().getText().toString();
            final String password = loginPasswordWrapper.getEditText().getText().toString();
            new Runnable() {
                @Override
                public void run() {
                    new HttpRequestLogin().execute(username, password);
                }
            }.run();
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        disableLoginButton();
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError(message);
            }
        }
    }

    class HttpRequestLogin extends AsyncTask<String, Void, LoginSignupResponse> {
        @Override
        protected LoginSignupResponse doInBackground(String... params) {
            try {
                return HttpClient.client.postForObject(LOGIN_URL,
                        new LoginRequest(params[0], params[1]), LoginSignupResponse.class);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(LoginSignupResponse response) {
            if (response.isSuccess()) {
                saveAuthToken(response.getMessage());
            } else {
                showLoginErrorMessage(response.getMessage());
            }
        }
    }
}
