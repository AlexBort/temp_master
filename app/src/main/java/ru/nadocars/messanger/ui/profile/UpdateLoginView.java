package ru.nadocars.messanger.ui.profile;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by User on 15.12.2016.
 */

public interface UpdateLoginView {

    void setCurrentMail (String mail);
    void setPersonalBalance (String balance);
    void setAvatar(Bitmap avatar);

    void setLastName (String name) ;
    void setFirstName (String name);
    void setMiddleName (String name);

    void setCurrentPhoneNumber (String phoneNumber);


    void focusOnEmail();
    void focusOnPhoneNumber();

    void showProgress(final boolean show);
    void setEmailError(String message);
  //  void setClickableButton (final boolean show);
    void setPhoneNumberError(String message);

    void requestFocus();
    void showNoInternetError();
    Context getContext();
    void showError(String message);

}
