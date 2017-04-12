package ru.nadocars.messanger.ui.contacts;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.domain.Contact;
import ru.nadocars.messanger.ui.DialogFragmentListener;
import ru.nadocars.messanger.ui.navigation.Navigator;
import ru.nadocars.messanger.ui.navigation.NavigatorImpl;

import ru.nadocars.messanger.ui.profile.ProfileActivity;

import ru.nadocars.messanger.ui.settings.IntervalDialogFragment;
import ru.nadocars.messanger.utils.CheckNewMessagesService;

//Отвечает за экран с контактами
public class ContactsActivity extends AppCompatActivity implements ContactsView, SwipeRefreshLayout.OnRefreshListener, DialogFragmentListener {

    private RecyclerView contactsRecyclerView;
    private ContactsPresenter contactsPresenter;
    private ImageView userAvatar;
    private Navigator navigator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ContactsAdapter contactsAdapter;

    //Создание содержимого экрана
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        contactsPresenter = ContactsPresenterImpl.getContactsPresenter();
        contactsPresenter.setView(this);
        navigator = new NavigatorImpl();
        userAvatar = (ImageView) findViewById(R.id.user_avatar);
        Button exitbutton = (Button) findViewById(R.id.exit_button);
        Button settingsButton = (Button) findViewById(R.id.check_interval);

        if (settingsButton != null) {
            settingsButton.setVisibility(View.VISIBLE);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSettings();
                }
            });
        }
        if (userAvatar != null) {
            userAvatar.setVisibility(View.VISIBLE);
            setUserAvatarFromInternalMemory(userAvatar);

            userAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    startActivity(intent);
                }
            });
        }
        if (exitbutton != null) {
            exitbutton.setVisibility(View.VISIBLE);
            exitbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOut();
                }
            });
        }
        contactsRecyclerView = (RecyclerView) findViewById(R.id.contacts_list);
        contactsPresenter.getContactsFromDb(this);
        contactsPresenter.getUserAvatar(this);
        startService(new Intent(this, CheckNewMessagesService.class));
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this);
        }
    }

    //обновить контакты, запускает запрос на сервер
    @Override
    public void onRefresh() {
        if (swipeRefreshLayout != null && contactsPresenter != null && contactsAdapter != null) {
            swipeRefreshLayout.setRefreshing(true);
            contactsPresenter.getContactsFromServer(this);
            contactsAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    //отобразить список контактов
    @Override
    public void showContacts(List<Contact> contacts) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contactsAdapter = new ContactsAdapter(contacts, this, this);
        contactsRecyclerView.setLayoutManager(linearLayoutManager);
        contactsRecyclerView.setAdapter(contactsAdapter);
    }

    //получить и отобразить аватарку диалога
    @Override
    public void getAndSetDialogAvatar(String url, ImageView avatarView) {
        contactsPresenter.getDialogAvatar(this, url, avatarView);
    }

    //отобразить и созхранить аватарку пользователя
    @Override
    public void setAvatar(Bitmap avatar) {
        String pathToAvatar = saveAvatar(avatar);
        savePathToAvatar(pathToAvatar);
        userAvatar.setImageBitmap(avatar);
    }

    //сохранить аватарку диалога
    @Override
    public String saveDialogAvatar(Bitmap avatar) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, "dialogAvatar.jpg");
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

    //вернуть контекст
    @Override
    public Context getContext() {
        return this;
    }

    //перезапуск службы проверки новых сообщений
    @Override
    public void restartDialogCheckService() {
        contactsPresenter.stopDialogsCheckService();
        contactsPresenter.startDialogsCheckService(this);
    }

    //при обновлении экрана запускаеться получение контактов с сервера и служба проверки новых сообщений
    @Override
    protected void onResume() {
        contactsPresenter.getContactsFromServer(this);
        contactsPresenter.startDialogsCheckService(this);
        super.onResume();
    }

    //остановка служби проверки новых сообщений
    @Override
    protected void onPause() {
        contactsPresenter.stopDialogsCheckService();
        super.onPause();
    }

    //очистка базы
    @Override
    protected void onDestroy() {
        contactsPresenter.clearDatabase();
        super.onDestroy();
    }

    //открытие настроек
    private void openSettings() {
        IntervalDialogFragment intervalDialogFragment;
        intervalDialogFragment = new IntervalDialogFragment();
        intervalDialogFragment.show(getFragmentManager(), "SetIntervalDialog");
    }

    //логаут
    private void logOut() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        contactsPresenter.logOut(this, defaultSharedPreferences.getString(SharedPreferencesApi.TOKEN, null));
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.remove(SharedPreferencesApi.LOGIN);
        editor.remove(SharedPreferencesApi.PASSWORD);
        editor.remove(SharedPreferencesApi.AVATAR);
        editor.remove(SharedPreferencesApi.TOKEN);
        editor.apply();
        contactsPresenter.clearDatabase();
        navigator.navigateToLogin(this);
        finish();
    }

    //сохраняет путь к аватарке
    private void savePathToAvatar(String path) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putString(SharedPreferencesApi.AVATAR, path);
        editor.apply();
    }

    //сохраняет аватарку
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

    //отображает аватарку юзера
    private void setUserAvatarFromInternalMemory(ImageView userAvatar) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String pathToUserAvatar = defaultSharedPreferences.getString(SharedPreferencesApi.AVATAR, null);
        Bitmap userAvatarBitmap = null;
        try {
            File file = new File(pathToUserAvatar, "avatar.jpg");
            userAvatarBitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (userAvatar != null && userAvatarBitmap != null) {
            userAvatar.setImageBitmap(userAvatarBitmap);
        }
    }

}
