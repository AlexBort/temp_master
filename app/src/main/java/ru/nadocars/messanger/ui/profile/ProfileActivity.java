package ru.nadocars.messanger.ui.profile;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.api.SharedPreferencesApi;

public class ProfileActivity extends AppCompatActivity implements UpdateLoginView {

    private static final String TAG = "ProfileActivity";
    UpdateLoginPresenter updateLoginPresenter;


    EditText editPhoneNumber;
    EditText editMail;



    public static AppCompatActivity appCompatActivityContext;


    TextView statusUserView;
    TextView balanceTextView;
    ImageView avatarImageView;

    TextView lastNameView;
    TextView middleNameView;
    TextView firstNameView;

    SharedPreferences defaultSharedPreferences;
    SharedPreferences.Editor editor;

    View focusView;
    private ConnectivityManager connectivityManager;
    private String codeSMS;
    private String updatePhoneNumber;
    private String updateEmail;
    private String phoneNumber;

    private String currentPhoneNumber;
    private String currentEmail;

    Button buttonUpdate;
    TextView textSMS;
    EditText editSMS;
    Button buttonSendSMS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        appCompatActivityContext = (AppCompatActivity) getContext();

        editPhoneNumber = (EditText) findViewById(R.id.edit_phone_number);

        updateLoginPresenter = UpdateLoginPresenterImpl.getUpdateLoginPresenter();
        updateLoginPresenter.setView(this);

        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        textSMS = (TextView) findViewById(R.id.code_SMS_text);
        editSMS = (EditText) findViewById(R.id.edit_code_sms);
        buttonSendSMS = (Button) findViewById(R.id.button_send_code);

        buttonUpdate = (Button) findViewById(R.id.update_button);
        editMail = (EditText) findViewById(R.id.mail_edit);
        avatarImageView = (ImageView) findViewById(R.id.image_profile);


        statusUserView = (TextView) findViewById(R.id.status_user); // передаем в зависимости


        firstNameView = (TextView) findViewById(R.id.first_name);
        middleNameView = (TextView) findViewById(R.id.middle_name);
        lastNameView = (TextView) findViewById(R.id.last_name);

        balanceTextView = (TextView) findViewById(R.id.count_value);


        updateLoginPresenter.setCurrentUserLogin(this);


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switcherUpdate();
            }
        });


        // ПРИ УСЛОВИИ ИЗМЕНЕНИЯ НОМЕРА!
        buttonSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSMS.setVisibility(View.GONE);
                editSMS.setVisibility(View.GONE);
                buttonSendSMS.setVisibility(View.GONE);

                buttonUpdate.setVisibility(View.VISIBLE);

                String updateNumber = editPhoneNumber.getText().toString();
                String updateMail = editMail.getText().toString();

                editor = defaultSharedPreferences.edit();
                editor.putString(SharedPreferencesApi.CURRENT_NUMBER, updateNumber);
                editor.putString(SharedPreferencesApi.CURRENT_MAIL, updateMail);
                editor.apply();

          //  Toast.makeText(ProfileActivity.this, updateMail, Toast.LENGTH_SHORT).show();


                updatePhoneNumber();

            }
        });

    }

    @Override
    public void setCurrentMail(String mail) {
        editMail.setText(mail);
        currentEmail = editMail.getText().toString();
        editor = defaultSharedPreferences.edit();
        editor.putString(SharedPreferencesApi.CURRENT_MAIL, currentEmail);
        editor.apply();

    }

    @Override
    public void setAvatar(Bitmap avatar) {
        String pathToAvatar = saveAvatar(avatar);
        savePathToAvatar(pathToAvatar);
        avatarImageView.setImageBitmap(avatar);
    }

    @Override
    public void setFirstName(String name) {
        firstNameView.setText(name);
    }

    @Override
    public void setMiddleName(String name) {
        middleNameView.setText(name);
    }

    @Override
    public void setLastName(String name) {
        lastNameView.setText(name);
    }

    @Override
    public void setPersonalBalance(String balance) {
        balanceTextView.setText(balance);
    }

    @Override
    public void setCurrentPhoneNumber(String phoneNumber) {
        editPhoneNumber.setText(phoneNumber);
        currentPhoneNumber = editPhoneNumber.getText().toString();
        editor = defaultSharedPreferences.edit();
        editor.putString(SharedPreferencesApi.CURRENT_NUMBER, currentPhoneNumber);
        editor.apply();
    }


    @Override
    public void focusOnEmail() {
        focusView = editMail;
    }

    @Override
    public void focusOnPhoneNumber() {
        focusView = editPhoneNumber;
    }

    @Override
    public void requestFocus() {
        focusView.requestFocus();
    }


    @Override
    public void showProgress(boolean show) {

    }


    @Override
    public void setEmailError(String message) {
        editMail.setError(message);

    }

    @Override
    public void setPhoneNumberError(String message) {
        editPhoneNumber.setError(message);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateLoginPresenter.setView(null);
    }

    @Override
    public void showNoInternetError() {
        Toast.makeText(this, getResources().getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean firstRequestUpdate() {

        String email = editMail.getText().toString();
        phoneNumber = editPhoneNumber.getText().toString();

        Log.e(TAG, "update mail: " + email);
        Log.e(TAG, "update phone: " + phoneNumber);

        updateLoginPresenter.attemptLogin(email, phoneNumber, this, this);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() ||
                !Patterns.PHONE.matcher(phoneNumber).matches()) {
            updateLoginPresenter.attemptLogin(email, phoneNumber, this, this);
            return false;
        } else {
            updateLoginPresenter.attemptLogin(email, phoneNumber, this, this);
            updateLoginPresenter.updateLogin(email, phoneNumber, this, this);

            editor = defaultSharedPreferences.edit();
            editor.putString(SharedPreferencesApi.CURRENT_NUMBER, phoneNumber);
            editor.putString(SharedPreferencesApi.CURRENT_MAIL, email);
            editor.apply();


            return true;
        }

    }

    private void updatePhoneNumber() {

        Log.e(TAG, "updatePhoneNumber: " + updateEmail);
        Log.e(TAG, "updatePhoneNumber: codeSMS, КОТОРЫЙ ПЕРЕДАЮ В UpdatePhoneNumberTask " + editSMS.getText().toString());

        updateLoginPresenter.attemptPhoneNumber(editMail.getText().toString(), editPhoneNumber.getText().toString(), this, this, editSMS.getText().toString());
        Log.e(TAG, "updatePhoneNumber: " + editPhoneNumber.getText().toString());
        Log.e(TAG, "updatePhoneNumber: " + editMail.getText().toString());

        if (editSMS.getText().toString().equals("")) {
            Toast.makeText(appCompatActivityContext, appCompatActivityContext.getResources().getString(R.string.error_code), Toast.LENGTH_SHORT).show();

        } else {
            updateLoginPresenter.attemptPhoneNumber(editMail.getText().toString(), editPhoneNumber.getText().toString(), this, this, editSMS.getText().toString());
            updateLoginPresenter.updatePhoneNumber(editMail.getText().toString(), editPhoneNumber.getText().toString(), this, this, editSMS.getText().toString());
            Log.e(TAG, "updatePhoneNumber: " + editSMS.getText().toString());
        }


    }

    private String getCodeSMS() {
        codeSMS = defaultSharedPreferences.getString(SharedPreferencesApi.CODE_SMS, null);
        Log.e(TAG, "getCodeSMS: ProfileActivity " + codeSMS);
        return codeSMS;
    }

    private void switcherUpdate() {
        currentPhoneNumber = defaultSharedPreferences.getString(SharedPreferencesApi.CURRENT_NUMBER, null);
        currentEmail = defaultSharedPreferences.getString(SharedPreferencesApi.CURRENT_MAIL, null);

   //     Toast.makeText(appCompatActivityContext, currentPhoneNumber, Toast.LENGTH_SHORT).show();

        if (currentEmail.equals(editMail.getText().toString()) && currentPhoneNumber.equals(editPhoneNumber.getText().toString())) {
            Toast.makeText(appCompatActivityContext, "Вы не изменили данные полей", Toast.LENGTH_SHORT).show();
        }


        if (!currentPhoneNumber.equals(editPhoneNumber.getText().toString())) {
            //  Toast.makeText(appCompatActivityContext, "ЗАШЛО В ОБНОВЛЕНИЕ НОМЕРА", Toast.LENGTH_SHORT).show();
            onClickUpdatePhoneNumber();

        } else if (!currentEmail.equals(editMail.getText().toString())) {
            //  Toast.makeText(appCompatActivityContext, "ЗАШЛО В ОБНОВЛЕНИЕ МЕЙЛА", Toast.LENGTH_SHORT).show();
            onClickUpdateMail();
        }


    }


    private void onClickUpdateMail() {

        currentEmail = defaultSharedPreferences.getString(SharedPreferencesApi.CURRENT_MAIL, null);
        updateEmail = editMail.getText().toString();

        Log.e(TAG, "onClickUpdateMail: CURRENT" + currentEmail);
        Log.e(TAG, "onClickUpdateMail: UPDATE" + updateEmail);

        if (!isOnline(this)) {
            Toast.makeText(appCompatActivityContext, "Интернет отсутствует", Toast.LENGTH_SHORT).show();
        }

        /*if (currentEmail.equals(editMail.getText().toString())) {
            Toast.makeText(appCompatActivityContext, "Вы не изменили адрес почты на новый", Toast.LENGTH_SHORT).show();
        }*/

        if (!currentEmail.equals(editMail.getText().toString())) {
            //  Toast.makeText(appCompatActivityContext, "EQUALS правильно срабатывает!!", Toast.LENGTH_SHORT).show();
            firstRequestUpdate();
        }


        Log.e(TAG, "onClick: " + updateEmail);
    }

    private void onClickUpdatePhoneNumber() {

        currentPhoneNumber = defaultSharedPreferences.getString(SharedPreferencesApi.CURRENT_NUMBER, null);
        updatePhoneNumber = editPhoneNumber.getText().toString();
        Log.e(TAG, "onClickUpdatePhoneNumber: CURRENT" + currentPhoneNumber);
        Log.e(TAG, "onClickUpdatePhoneNumber: UPDATE" + updatePhoneNumber);

        if (!isOnline(this)) {
            Toast.makeText(appCompatActivityContext, "Интернет отсутствует", Toast.LENGTH_SHORT).show();
        }

       /* else if (currentPhoneNumber.equals(updatePhoneNumber)) {
            Toast.makeText(appCompatActivityContext, "Вы не изменили номер телефона на новый", Toast.LENGTH_SHORT).show();
        }*/

        if (!currentPhoneNumber.equals(updatePhoneNumber) && firstRequestUpdate()) { // FIRST_REQUEST
            Log.e(TAG, "onClickUpdatePhoneNumber: CURRENT" + currentPhoneNumber);
            Log.e(TAG, "onClickUpdatePhoneNumber: UPDATE" + updatePhoneNumber);


            buttonUpdate.setVisibility(View.GONE);
            textSMS.setVisibility(View.VISIBLE);

            buttonSendSMS.setVisibility(View.VISIBLE); // =====>>> дальше КЛИКАЕМ на эту кнопку

            // edit_SMS
            editSMS.setVisibility(View.VISIBLE);
            String codeSMS = getCodeSMS();
            Log.e(TAG, "onClickGetCodeSMS: " + codeSMS);
            editSMS.setText(codeSMS);


        }


    }

    private boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();


    }

    private String saveAvatar(Bitmap avatar) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, "avatar.jpg");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(myPath);
            avatar.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    private void savePathToAvatar(String path) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putString(SharedPreferencesApi.AVATAR, path);
        editor.apply();
    }


}
