package com.redbluekey.sciodev.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.helpers.ConectivityStatus;
import com.redbluekey.sciodev.helpers.HttpClient;
import com.redbluekey.sciodev.models.LoginSignupResponse;
import com.redbluekey.sciodev.util.TextInputLayoutValidatorAdapter;

import java.util.List;

public class ResetPasswordDialog extends AppCompatActivity implements View.OnClickListener,
        Validator.ValidationListener {

    private static final String TAG = "ResetPasswordDialog";

    private static final String
            RESET_PASSWORD_URL = "http://api.redbluekey.com/api/user/PostUserForgotPassword?usernameForgottenPassword=";

    @NotEmpty
    TextInputLayout forgotPasswordUsername;

    Button btnSubmit;
    Button btnCancel;

    Validator validator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_dialog);

        initUsernameInput();
        initSubmitButton();
        initCancelButton();
        initValidator();
    }

    @Override
    public void onClick(View v) {
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        if (ConectivityStatus.hasInternetConnection(getApplicationContext())) {
            final String username = forgotPasswordUsername.getEditText().getText().toString();
            new Runnable() {
                @Override
                public void run() {
                    new HttpRequestResetPassword().execute(username);
                }
            }.run();
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        disableSubmitButton();
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError(message);
            }
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
                    input.setErrorEnabled(false);
                }
            }
        });
    }

    private void initSubmitButton() {
        btnSubmit = findViewById(R.id.btn_email_me);
        btnSubmit.setOnClickListener(this);
        disableSubmitButton();
    }

    private void initCancelButton() {
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUsernameInput() {
        forgotPasswordUsername = findViewById(R.id.forgot_dialog_usernameWrapper);
        forgotPasswordUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableLoginButton();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    disableSubmitButton();
                }
            }
        });
    }

    private void enableLoginButton() {
        btnSubmit.setEnabled(true);
        btnSubmit.setAlpha(1.0f);
        btnSubmit.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnSubmit.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void disableSubmitButton() {
        btnSubmit.setEnabled(false);
        btnSubmit.setAlpha(0.7f);
        btnSubmit.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnSubmit.setTextColor(getResources().getColor(R.color.colorDarkGrey));
    }

    private void showMessageToUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    class HttpRequestResetPassword extends AsyncTask<String, Void, LoginSignupResponse> {
        @Override
        protected LoginSignupResponse doInBackground(String... params) {
            try {
                return HttpClient.client.postForObject(RESET_PASSWORD_URL + params[0], null, LoginSignupResponse.class);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(LoginSignupResponse response) {
            if (response.isSuccess()) {
                showMessageToUser(response.getMessage());
            } else {
                finish();
            }
        }
    }
}
