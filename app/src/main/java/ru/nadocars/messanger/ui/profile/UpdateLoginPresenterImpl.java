package ru.nadocars.messanger.ui.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.asynctasks.GetUserAvatarProfileTask;
import ru.nadocars.messanger.asynctasks.GetUserAvatarTask;
import ru.nadocars.messanger.asynctasks.GetUserLoginTask;
import ru.nadocars.messanger.asynctasks.UpdatePhoneNumberTask;
import ru.nadocars.messanger.asynctasks.UpdateUserLoginTask;

/**
 * Created by User on 15.12.2016.
 */

public class UpdateLoginPresenterImpl implements UpdateLoginPresenter {


    private UpdateLoginView updateLoginView;
    private UpdateUserLoginTask updateUserLoginTask = null;
    private UpdatePhoneNumberTask updatePhoneNumberTask = null;
    private static UpdateLoginPresenterImpl updateLoginPresenter = new UpdateLoginPresenterImpl();

    private UpdateLoginPresenterImpl() {
    }

    public static UpdateLoginPresenter getUpdateLoginPresenter() {
        return updateLoginPresenter;
    }


    // ======>>>>>> GET USER LOGIN....<<<<<=============
    @Override
    public void setCurrentMail(String mail) {
        updateLoginView.setCurrentMail(mail);
    }

    @Override
    public void setCurrentPhoneNumber(String phoneNumber) {
        updateLoginView.setCurrentPhoneNumber(phoneNumber);
    }

    @Override
    public void setLastName(String name) {
        updateLoginView.setLastName(name);
    }

    @Override
    public void setFirstName(String name) {
        updateLoginView.setFirstName(name);
    }

    @Override
    public void setMiddleName(String name) {
        updateLoginView.setMiddleName(name);
    }

    @Override
    public void setPersonalBalance(String balance) {
        updateLoginView.setPersonalBalance(balance);
    }

    @Override
    public String getCurrentPhoneNumber(String phoneNumber) { // use in GetUserLoginTask
        return null;
    }

    @Override
    public String getCurrentMail(String mail) {
        return null;
    } // use in GetUserLoginTask


    @Override
    public void getUserAvatar(Context context) {
        GetUserAvatarProfileTask getUserAvatarProfileTask = new GetUserAvatarProfileTask(context, this);
        getUserAvatarProfileTask.execute();
    }

    @Override
    public void setAvatar(Bitmap avatar) {
        updateLoginView.setAvatar(avatar);
    }

    @Override
    public void setCurrentUserLogin(Context context) {
        GetUserLoginTask getUserLoginTask = new GetUserLoginTask(this, context);
        getUserLoginTask.execute();
    }  // ======>>>>>> GET USER LOGIN END==========<<<<<=============


    @Override
    public void setUpdateUserLoginTask(UpdateUserLoginTask updateUserLoginTask) {
        this.updateUserLoginTask = updateUserLoginTask;
    }

    @Override
    public void updateLogin(String email, String phoneNumber, Context context, AppCompatActivity appCompatActivity) {

        updateUserLoginTask = new UpdateUserLoginTask(email, phoneNumber, context, appCompatActivity);
        updateUserLoginTask.execute();
    }

    @Override
    public void setUpdatePhoneNumberTask(UpdatePhoneNumberTask updatePhoneNumberTask) {
        this.updatePhoneNumberTask = updatePhoneNumberTask;
    }


    @Override
    public void attemptPhoneNumber(String email, String phoneNumber, Context context, AppCompatActivity appCompatActivity, String codeSMS) {
        if (updatePhoneNumberTask != null) {
            return;
        }

        if (updateLoginView != null) {
            updateLoginView.setEmailError(null);
            updateLoginView.setPhoneNumberError(null);
        }
        boolean cancel = false;

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (updateLoginView != null) {

                if (email.isEmpty()) {
                    updateLoginView.setEmailError(context.getResources().getString(R.string.empty_field_mail));
                    updateLoginView.focusOnEmail();
                } else {
                    updateLoginView.setEmailError(context.getResources().getString(R.string.error_field_mail));
                    updateLoginView.focusOnEmail();
                }
            }
            cancel = true;
        }

        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            if (updateLoginView != null) {
                if (phoneNumber.isEmpty()) {
                    updateLoginView.setPhoneNumberError(context.getResources().getString(R.string.empty_field_phone));
                    updateLoginView.focusOnPhoneNumber();
                } else {
                    updateLoginView.setPhoneNumberError(context.getResources().getString(R.string.error_field_phone));
                    updateLoginView.focusOnPhoneNumber();
                }
            }
            cancel = true;
        }

        if (cancel) {
            if (updateLoginView != null) {
                updateLoginView.requestFocus();
            }
        } else {
            if (updateLoginView != null) {
                updateLoginView.showProgress(true);
            }
        }
    }

    @Override
    public void attemptLogin(String email, String phoneNumber, Context context, AppCompatActivity appCompatActivity) {
        if (updateUserLoginTask != null) {
            return;
        }
        if (updateLoginView != null) {
            updateLoginView.setEmailError(null);
            updateLoginView.setPhoneNumberError(null);
        }
        boolean cancel = false;

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (updateLoginView != null) {

                if (email.isEmpty()) {
                    updateLoginView.setEmailError(context.getResources().getString(R.string.empty_field_mail));
                    updateLoginView.focusOnEmail();
                } else {
                    updateLoginView.setEmailError(context.getResources().getString(R.string.error_field_mail));
                    updateLoginView.focusOnEmail();
                }
            }
            cancel = true;
        }

        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            if (updateLoginView != null) {
                if (phoneNumber.isEmpty()) {
                    updateLoginView.setPhoneNumberError(context.getResources().getString(R.string.empty_field_phone));
                    updateLoginView.focusOnPhoneNumber();
                } else {
                    updateLoginView.setPhoneNumberError("Введите номер телефона в формате +7 (111) 222-33-44");
                    updateLoginView.focusOnPhoneNumber();
                }
            }
            cancel = true;
        }

        if (cancel) {
            if (updateLoginView != null) {
                updateLoginView.requestFocus();
            }
        } else {
            if (updateLoginView != null) {
                updateLoginView.showProgress(true);
            }
        }


    }

    @Override
    public void updatePhoneNumber(String email, String phoneNumber, Context context, AppCompatActivity appCompatActivity, String codeSMS) {

        updatePhoneNumberTask = new UpdatePhoneNumberTask(email, phoneNumber, context, appCompatActivity, codeSMS);
        updatePhoneNumberTask.execute();
    }


    @Override
    public void setView(UpdateLoginView view) {
        updateLoginView = view;
    }


    @Override
    public void setUpdateMail() {

    }

    @Override
    public void setUpdatePhoneNumber() {

    }

    @Override
    public void getUpdateUser(Context context) {

    }


    @Override
    public void showProgress(boolean show) {
        if (updateLoginView != null) {
            updateLoginView.showProgress(show);
        }
    }

    @Override
    public void showError(String message) {
        if (updateLoginView != null) {
            updateLoginView.showError(message);
        }
    }

    @Override
    public void setClickableButton(boolean show) {
        //   updateLoginView.setClickableButton(show);
    }
}
