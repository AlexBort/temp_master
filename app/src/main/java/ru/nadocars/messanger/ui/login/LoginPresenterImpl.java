package ru.nadocars.messanger.ui.login;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.asynctasks.UserLoginTask;

//Прослойка между экраном логина и "логикой" приложения
public class LoginPresenterImpl implements LoginPresenter {

    private String TAG = "LoginPresenterImpl";
    private LoginView loginView;
    private UserLoginTask authTask = null;

    private static LoginPresenterImpl loginPresenter = new LoginPresenterImpl();

    private LoginPresenterImpl() {
    }

    public static LoginPresenter getLoginPresenter() {
        return loginPresenter;
    }

    //колбэк
    @Override
    public void setView(LoginView view) {
        loginView = view;
    }


    @Override
    public void attemptLogin(String login, String password, Context context, AppCompatActivity appCompatActivity) {
        if (authTask != null) {
            return;
        }
        if (loginView != null) {
            loginView.setEmailError(null);
            loginView.setPasswordError(null);
        }
        boolean cancel = false;
        if (!(password.length() > 0)) {
            if (loginView != null) {
                loginView.setPasswordError(context.getResources().getString(R.string.error_short_password));
                loginView.focusOnPassword();
            }
            cancel = true;
        }

        if (login.isEmpty()) {
            if (loginView != null) {
                loginView.setEmailError(context.getResources().getString(R.string.error_field_required));
                loginView.focusOnEmail();
            }
            cancel = true;
        } else if (!login.contains("@")) {
            if (loginView != null) {
                loginView.setEmailError(context.getResources().getString(R.string.error_invalid_email));
                loginView.focusOnEmail();
            }
            cancel = true;
        }
        if (cancel) {
            if (loginView != null) {
                loginView.requestFocus();
            }
        } else {
            if (loginView != null) {
                loginView.showProgress(true);
            }}
            authTask = new UserLoginTask(login, password, context, appCompatActivity);
            authTask.execute();

    }


    public void setAuthTask(UserLoginTask authTask) {
        this.authTask = authTask;
    }

    //отобразить спинер
    @Override
    public void showProgress(boolean show) {
        if (loginView != null) {
            loginView.showProgress(show);
        }
    }

    //отобразить ошибку
    @Override
    public void showError(String message) {
        if (loginView != null) {
            loginView.showError(message);
        }

    }

}
