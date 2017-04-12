package ru.nadocars.messanger.ui.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;

import ru.nadocars.messanger.ConstantsApi;
import ru.nadocars.messanger.asynctasks.GetUserLoginTask;
import ru.nadocars.messanger.asynctasks.UpdatePhoneNumberTask;
import ru.nadocars.messanger.asynctasks.UpdateUserLoginTask;
import ru.nadocars.messanger.asynctasks.UserLoginTask;
import ru.nadocars.messanger.ui.Presenter;

/**
 * Created by User on 15.12.2016.
 */

public interface UpdateLoginPresenter extends Presenter<UpdateLoginView> {


    // FOR CURRENT DATA
    void setCurrentMail(String mail);
    void setPersonalBalance (String balance);

    void setLastName (String name) ;
    void setFirstName (String name);
    void setMiddleName (String name);


    void setCurrentPhoneNumber(String phoneNumber);
    String getCurrentPhoneNumber(String phoneNumber);
    String getCurrentMail(String mail);
    void setCurrentUserLogin(Context context);

    void getUserAvatar(Context context);
    void setAvatar(Bitmap avatar);


    // FOR UPDATE DATA
    void updateLogin(String email, String phoneNumber, Context context, AppCompatActivity appCompatActivity);
    void updatePhoneNumber(String email, String phoneNumber, Context context, AppCompatActivity appCompatActivity,String codeSMS);

    void attemptPhoneNumber (String email, String phoneNumber, Context context, AppCompatActivity appCompatActivity,String codeSMS);
    void attemptLogin (String email, String phoneNumber, Context context, AppCompatActivity appCompatActivity);

    void setUpdateUserLoginTask(UpdateUserLoginTask updateUserLoginTask);
    void setUpdatePhoneNumberTask(UpdatePhoneNumberTask updatePhoneNumberTask);
    void setUpdatePhoneNumber();
    void setUpdateMail();
    void getUpdateUser(Context context);
    void showProgress(boolean show);
    void showError(String message);
   void setClickableButton (boolean show) ;

}
