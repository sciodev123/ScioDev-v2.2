package com.redbluekey.sciodev.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.helpers.ConectivityStatus;
import com.redbluekey.sciodev.helpers.HttpClient;
import com.redbluekey.sciodev.models.LoginSignupResponse;
import com.redbluekey.sciodev.models.SignupRequest;
import com.redbluekey.sciodev.util.TextInputLayoutValidatorAdapter;

import java.util.List;

public class SignupScreen extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

    private static final String TAG = "SignupScreen";

    public static final String IS_FROM_SIGNUP = "isFromSignup";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private static final String
            SIGNUP_URL = "http://api.redbluekey.com/api/user/PostUserSignup?signupSource=Android";

    @NotEmpty
    TextInputLayout usernameWrapper;
    @NotEmpty
    TextInputLayout passwordWrapper;
    @Email(message = "That email doesn't look right.")
    TextInputLayout emailWrapper;

    Button submitButton;
    ImageButton closeButton;

    TextView loginLink;
    TextView termsPrivacyContent;

    Validator validator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);
        initSignupForm();
        initValidator();
        initTermsPrivacyContent();
    }

    private void initSignupForm() {
        usernameWrapper = findViewById(R.id.usernameWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
        emailWrapper = findViewById(R.id.emailWrapper);
        submitButton = findViewById(R.id.btn_form_signup);
        setOnChangeListener(usernameWrapper);
        setOnChangeListener(passwordWrapper);
        setOnChangeListener(emailWrapper);
        disableSignupButton();
        initCloseButton();
        initLoginLink();
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
                enableSignupButton();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void disableSignupButton() {
        submitButton.setEnabled(false);
        submitButton.setAlpha(0.7f);
        submitButton.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        submitButton.setTextColor(getResources().getColor(R.color.colorDarkGrey));
    }

    private void enableSignupButton() {
        submitButton.setEnabled(true);
        submitButton.setAlpha(1.0f);
        submitButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        submitButton.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void initCloseButton() {
        closeButton = findViewById(R.id.btn_signup_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initLoginLink() {
        loginLink = findViewById(R.id.q_have_an_account);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginScreen = new Intent(getApplicationContext(), LoginScreen.class);
                loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(loginScreen);
            }
        });
    }

    private void initTermsPrivacyContent() {
        termsPrivacyContent = findViewById(R.id.terms_privacy_content);
        termsPrivacyContent.setMovementMethod(LinkMovementMethod.getInstance());
        termsPrivacyContent.setText(
                addClickableParts(getString(R.string.terms_privacy_content)),
                TextView.BufferType.SPANNABLE);
    }

    /*
    !!! Note that if you change your text in resources, you should reimplement this method
     */
    private SpannableStringBuilder addClickableParts(String content) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        String[] clickableItems = {"Terms", "Privacy Policy", "Content Policy"};

        for (final String clickableItem : clickableItems) {
            int startIndex = content.indexOf(clickableItem.charAt(0));
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    showClickedItem(clickableItem);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(getResources().getColor(R.color.colorPrimary));
                }
            }, startIndex, startIndex + clickableItem.length(), 0);
        }

        return ssb;
    }

    private void showClickedItem(String clickedItem) {
        Toast.makeText(this, clickedItem, Toast.LENGTH_LONG).show();
    }

    private void showSignupErrorMessage(String error) {
        usernameWrapper.setErrorEnabled(true);
        usernameWrapper.setError(error);
    }

    private void goToLogin() {
        Intent loginScreen = new Intent(getApplicationContext(), LoginScreen.class);
        loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginScreen.putExtra(IS_FROM_SIGNUP, true);
        loginScreen.putExtra(USERNAME, usernameWrapper.getEditText().getText().toString());
        loginScreen.putExtra(PASSWORD, passwordWrapper.getEditText().getText().toString());
        getApplicationContext().startActivity(loginScreen);
    }

    @Override
    public void onValidationSucceeded() {
        if (ConectivityStatus.hasInternetConnection(this)) {
        final String username = usernameWrapper.getEditText().getText().toString();
        final String password = passwordWrapper.getEditText().getText().toString();
        final String email = emailWrapper.getEditText().getText().toString();
            new Runnable() {
                @Override
                public void run() {
                    new HttpRequestSignup().execute(username, password, email);
                }
            }.run();
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        disableSignupButton();
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

    class HttpRequestSignup extends AsyncTask<String, Void, LoginSignupResponse> {
        @Override
        protected LoginSignupResponse doInBackground(String... params) {
            try {
                return HttpClient.client.postForObject(SIGNUP_URL,
                        new SignupRequest(params[0], params[1], params[2]), LoginSignupResponse.class);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(LoginSignupResponse response) {
            if (response.isSuccess()) {
                goToLogin();
            } else {
                showSignupErrorMessage(response.getMessage());
            }
        }
    }
}
