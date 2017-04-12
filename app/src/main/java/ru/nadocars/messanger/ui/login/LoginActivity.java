package ru.nadocars.messanger.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.ui.web_site.NadocarsRegistration;

//Клас для работы с экраном логина
public class LoginActivity extends AppCompatActivity implements LoginView {

    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;
    private View focusView;
    private LoginPresenter loginPresenter;
    private CheckBox rememberMeCheckBox;
    private String TAG = "LoginActivity";

    //Настройка отображения при создании
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ImageView userAvatar = (ImageView) findViewById(R.id.user_avatar);
        Button exitbutton = (Button) findViewById(R.id.exit_button);
        Button settingsButton = (Button) findViewById(R.id.check_interval);
        TextView textRegistrationSite = (TextView) findViewById(R.id.registration_in_site);
        if (settingsButton != null) {
            settingsButton.setVisibility(View.INVISIBLE);
        }
        if (userAvatar != null) {
            userAvatar.setVisibility(View.INVISIBLE);
        }
        if (exitbutton != null) {
            exitbutton.setVisibility(View.INVISIBLE);
        }
        emailView = (AutoCompleteTextView) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.remember_me_checkbox);
        Button emailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        loginPresenter = LoginPresenterImpl.getLoginPresenter(); //
        loginPresenter.setView(this);

        if (emailSignInButton != null) {
            emailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }


        // SITE REGISTRATION!
        String emphasizeText = "<u>Регистрация</u>";
        Spanned transformText = android.text.Html.fromHtml(emphasizeText);
        textRegistrationSite.setText(transformText);

        textRegistrationSite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NadocarsRegistration.class);
                startActivity(intent);
            }
        });


    }

    //отображает прогрес (спинер)
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //ошибка ввода имейла
    @Override
    public void setEmailError(String message) {
        emailView.setError(message);
    }

    //ошибка ввода пароля
    @Override
    public void setPasswordError(String message) {
        passwordView.setError(message);
    }

    @Override
    public void focusOnEmail() {
        focusView = emailView;
    }

    @Override
    public void focusOnPassword() {
        focusView = passwordView;
    }

    @Override
    public void requestFocus() {
        focusView.requestFocus();
    }

    //ошибка при отсутствии интернета
    @Override
    public void showNoInternetError() {
        Toast.makeText(this, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        Log.e(TAG, "showNoInternetError: " + "заходит в этот МЕТОД");
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        loginPresenter.setView(null);
    }

    //попытка логина
    private void attemptLogin() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putBoolean(SharedPreferencesApi.AUTO_LOGIN, rememberMeCheckBox.isChecked());
        editor.apply();


        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        loginPresenter.attemptLogin(email, password, this, this);
    }

}

