package ru.nadocars.messanger.ui.prelogin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.data.db.DBHelper;
import ru.nadocars.messanger.data.db.DatabaseManager;
import ru.nadocars.messanger.ui.navigation.Navigator;
import ru.nadocars.messanger.ui.navigation.NavigatorImpl;

//отвечает за экран автологина (самый первый)
public class PreLoginActivity extends AppCompatActivity implements PreLoginView {

    PreLoginPresenter preLoginPresenter;
    Navigator navigator;

    //вызываеться при создании
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login);
        preLoginPresenter = PreLoginPresenterImpl.getPreLoginPresenter();
        preLoginPresenter.setView(this);
        navigator = new NavigatorImpl();
        DatabaseManager.initializeInstance(new DBHelper(this));
        attemptAutoLogin();
    }

    //попытка логина
    private void attemptAutoLogin() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean autoLogin = defaultSharedPreferences.getBoolean(SharedPreferencesApi.AUTO_LOGIN, false);
        if (autoLogin) {
            String login = defaultSharedPreferences.getString(SharedPreferencesApi.LOGIN, null);
            String password = defaultSharedPreferences.getString(SharedPreferencesApi.PASSWORD, null);
            if (login != null && password != null) {
                preLoginPresenter.attemptAutoLogin(login, password, this, this);
            } else {
                navigator.navigateToLogin(this);
            }
        } else {
            navigator.navigateToLogin(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        preLoginPresenter.setView(null);
        finish();
    }
}
