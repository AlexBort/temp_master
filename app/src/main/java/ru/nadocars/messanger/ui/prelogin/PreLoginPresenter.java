package ru.nadocars.messanger.ui.prelogin;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import ru.nadocars.messanger.ui.Presenter;

public interface PreLoginPresenter extends Presenter<PreLoginView> {

    public void attemptAutoLogin(String login, String password, Context context, AppCompatActivity appCompatActivity);

}
