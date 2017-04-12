package ru.nadocars.messanger.ui.login;

import android.content.Context;

public interface LoginView {




    void showProgress(final boolean show);
    void setEmailError(String message);
    void setPasswordError(String message);
    void focusOnEmail();
    void focusOnPassword();
    void requestFocus();
    void showNoInternetError();
    Context getContext();
    void showError(String message);

}
