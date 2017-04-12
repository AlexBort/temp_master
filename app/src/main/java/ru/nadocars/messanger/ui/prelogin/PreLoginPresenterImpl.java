package ru.nadocars.messanger.ui.prelogin;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import ru.nadocars.messanger.asynctasks.UserLoginTask;

//отвечает за автологин если стояла галочка запомнить меня
public class PreLoginPresenterImpl implements PreLoginPresenter {

    private PreLoginView preLoginView;

    private static PreLoginPresenterImpl preLoginPresenter = new PreLoginPresenterImpl();

    private PreLoginPresenterImpl() {
    }

    public static PreLoginPresenter getPreLoginPresenter() {
        return preLoginPresenter;
    }

    @Override
    public void setView(PreLoginView view) {
        preLoginView = view;
    }

    //попытка логина
    @Override
    public void attemptAutoLogin(String login, String password, Context context, AppCompatActivity appCompatActivity) {
        UserLoginTask userLoginTask = new UserLoginTask(login, password, context, appCompatActivity);
        userLoginTask.execute();
    }
}
