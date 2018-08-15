package com.redbluekey.sciodev.activities;

import android.content.Context;
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
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.helpers.ConectivityStatus;
import com.redbluekey.sciodev.helpers.HttpClient;
import com.redbluekey.sciodev.helpers.LocalStorage;
import com.redbluekey.sciodev.models.LoginSignupResponse;
import com.redbluekey.sciodev.models.UserProfile;
import com.redbluekey.sciodev.util.TextInputLayoutValidatorAdapter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class UpdateProfileScreen extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

    private static final String TAG = "UpdateProfileScreen";

    private static final String
            GET_PROFILE_URL = "http://api.redbluekey.com/api/user/GetUserDetails?username=";
    private static final String
            UPDATE_PROFILE_URL = "http://api.redbluekey.com/api/user/PutUserDetails?detailsSource=Andorid";
    private static final String TOKEN_HEADER_NAME = "AuthenticationToken";

    @NotEmpty
    @Email(message = "That email doesn't look right.")
    TextInputLayout emailWrapper;
    @NotEmpty
    TextInputLayout passwordWrapper;

    private UserProfile userProfile;

    Button submitButton;
    ImageButton closeButton;

    Validator validator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_screen);
        initUpdateForm();
        initValidator();
        getUserProfile();
    }

    private void initUpdateForm() {
        emailWrapper = findViewById(R.id.updateEmailWrapper);
        passwordWrapper = findViewById(R.id.updatePasswordWrapper);
        submitButton = findViewById(R.id.btn_form_update_profile);
        setOnChangeListener(emailWrapper);
        setOnChangeListener(passwordWrapper);
        disableSubmitButton();
        initCloseButton();
        submitButton.setOnClickListener(this);
    }

    private void initValidator() {
        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutValidatorAdapter());
        validator.setViewValidatedAction(new Validator.ViewValidatedAction() {
            @Override
            public void onAllRulesPassed(View view) {
                if (view instanceof TextInputLayout) {
                    ((TextInputLayout) view).setErrorEnabled(false);
                }
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        validator.validate();
    }

    private void setOnChangeListener(final TextInputLayout inputField) {
        inputField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitButton();
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

    private void disableSubmitButton() {
        submitButton.setEnabled(false);
        submitButton.setAlpha(0.7f);
        submitButton.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        submitButton.setTextColor(getResources().getColor(R.color.colorDarkGrey));
    }

    private void enableSubmitButton() {
        submitButton.setEnabled(true);
        submitButton.setAlpha(1.0f);
        submitButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        submitButton.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void initCloseButton() {
        closeButton = findViewById(R.id.btn_update_profile_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void preFillEmail() {
        emailWrapper.getEditText().setText(userProfile.getEmail());
    }

    private void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void getUserProfile() {
        final String[] authData = LocalStorage.getAuthData(this);
        new Runnable() {
            @Override
            public void run() {
                new HttpRequestGetProfile().execute(authData[0], authData[1]);
            }
        }.run();
    }

    @Override
    public void onValidationSucceeded() {
        if (ConectivityStatus.hasInternetConnection(this)) {
        final String email = emailWrapper.getEditText().getText().toString();
        final String password = passwordWrapper.getEditText().getText().toString();
        final String authToken = LocalStorage.getAuthData(this)[0];
            new Runnable() {
                @Override
                public void run() {
                    new HttpRequestUpdateProfile().execute(email, password, authToken);
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
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    class HttpRequestUpdateProfile extends AsyncTask<String, Void, LoginSignupResponse> {
        @Override
        protected LoginSignupResponse doInBackground(String... params) {
            userProfile.setEmail(params[0]);
            userProfile.setPassword(params[1]);
            HttpHeaders headers = new HttpHeaders();
            headers.add(TOKEN_HEADER_NAME, params[2]);
            HttpEntity<UserProfile> entity = new HttpEntity<>(userProfile, headers);
            try {
                ResponseEntity<LoginSignupResponse> response =  HttpClient
                        .client.exchange(UPDATE_PROFILE_URL, HttpMethod.PUT, entity,
                                LoginSignupResponse.class);
                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    return response.getBody();
                } else {
                    LoginSignupResponse badResponse = new LoginSignupResponse();
                    badResponse.setSuccess(false);

                    return badResponse;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(LoginSignupResponse response) {
            if (response.isSuccess()) {
                showSuccessMessage(response.getMessage());
            } else {
                finish();
            }
        }
    }

    class HttpRequestGetProfile extends AsyncTask<String, Void, UserProfile> {
        @Override
        protected UserProfile doInBackground(String... params) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(TOKEN_HEADER_NAME, params[0]);
            HttpEntity entity = new HttpEntity(headers);
            try {
                return HttpClient.client.exchange(GET_PROFILE_URL + params[1], HttpMethod.GET,
                        entity, UserProfile.class).getBody();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserProfile response) {
            if (response != null) {
                userProfile = response;
                preFillEmail();
            } else {
                finish();
            }
        }
    }
}
