package ru.nadocars.messanger.ui.login;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import ru.nadocars.messanger.asynctasks.UserLoginTask;
import ru.nadocars.messanger.ui.Presenter;

public interface LoginPresenter extends Presenter<LoginView> {

    void attemptLogin(String login, String password, Context context, AppCompatActivity appCompatActivity);
    void setAuthTask(UserLoginTask authTask);
    void showProgress(boolean show);
    void showError(String message);



}
