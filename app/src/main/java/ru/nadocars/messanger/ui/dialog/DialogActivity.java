package ru.nadocars.messanger.ui.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import ru.nadocars.messanger.ConstantsApi;
import ru.nadocars.messanger.R;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.domain.Message;
import ru.nadocars.messanger.ui.DialogFragmentListener;
import ru.nadocars.messanger.ui.navigation.Navigator;
import ru.nadocars.messanger.ui.navigation.NavigatorImpl;
import ru.nadocars.messanger.ui.settings.IntervalDialogFragment;

//Клас для работы с экраном с сообщениями
public class DialogActivity extends AppCompatActivity implements DialogView, DialogFragmentListener {

    private DialogPresenter dialogPresenter;
    private Navigator navigator;
    private EditText message;
    private int dialogId;
    private ListView messagesListView;
    protected ImageView avatarImageView;
    private SwipyRefreshLayout swipeRefreshLayout;
    private DialogAdapter dialogAdapter;

    //Конфигурирование при создании
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Intent intent = getIntent();
        dialogId = intent.getIntExtra("dialogId", -1);
        String dialogAvatarPath = intent.getStringExtra("dialogAvatarPath");
        dialogPresenter = DialogPresenterImpl.getDialogPresenter();
        dialogPresenter.setView(this);
        message = (EditText) findViewById(R.id.enter_message);
        navigator = new NavigatorImpl();
        Button sendMessageButton = (Button) findViewById(R.id.send_message_button);
        if (sendMessageButton != null) {
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                }
            });
        }
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String pathToUserAvatar = defaultSharedPreferences.getString(SharedPreferencesApi.AVATAR, null);
        ImageView userAvatarView = (ImageView) findViewById(R.id.user_avatar);
        Button exitbutton = (Button) findViewById(R.id.exit_button);
        Button settingsButton = (Button) findViewById(R.id.check_interval);
        Bitmap userAvatar = null;
        try {
            File file = new File(pathToUserAvatar, "avatar.jpg");
            userAvatar = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (settingsButton != null) {
            settingsButton.setVisibility(View.VISIBLE);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSettings();
                }
            });
        }
        if (userAvatarView != null) {
            userAvatarView.setVisibility(View.VISIBLE);
            if (userAvatar != null) {
                userAvatarView.setImageBitmap(userAvatar);
            }
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


        messagesListView = (ListView) findViewById(R.id.messages_list_view);
        avatarImageView = (ImageView) findViewById(R.id.dialog_avatar);
        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
        if (avatarImageView != null) {
            avatarImageView.setImageBitmap(avatar);
        }

        final Context context = this;

        swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.custom_swipe_refresh_layout);

        if (swipeRefreshLayout != null && dialogPresenter != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(SwipyRefreshLayoutDirection direction) {
                    swipeRefreshLayout.setRefreshing(true);
                    Log.d("Activity", "Refresh triggered at " + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                    dialogPresenter.getMessagesFromServer(context, dialogId);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        File file = new File(dialogAvatarPath, "dialogAvatar.jpg");
        Bitmap dialogAvatar = null;
        try {
            dialogAvatar = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (dialogAvatar != null && avatarImageView != null) {
            avatarImageView.setImageBitmap(dialogAvatar);
        }
        dialogPresenter.getMessagesFromDb(this, dialogId);
        dialogPresenter.getMessagesFromServer(this, dialogId);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    //отобразить диалог
    @Override
    public void showDialog(List<Message> messages) {
        dialogAdapter = new DialogAdapter(this, android.R.layout.simple_list_item_1, messages, dialogId);
        messagesListView.setAdapter(dialogAdapter);
        messagesListView.setSelection(messagesListView.getAdapter().getCount() - 1);
        messagesListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        for (Message message : messages) {
            if (message.getIsNewMessage() == 1) {
                AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                Vibrator vibrator;
                switch (audioManager.getRingerMode()) {
                    case AudioManager.RINGER_MODE_VIBRATE:
                        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(500);
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(500);
                        break;
                }
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    //показать ошибку ввода
    @Override
    public void showErrorWrongInput(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    //перезапуск служби проверки сообщений
    @Override
    public void restartDialogCheckService() {
        dialogPresenter.stopMessagesCheckService();
        dialogPresenter.startMessagesCheckService(this, dialogId);
    }

    //получение широковещательного сообщение о необходимости обновить диалог
    @Override
    protected void onResume() {
        dialogPresenter.startMessagesCheckService(this, dialogId);
        IntentFilter intentFilter = new IntentFilter(ConstantsApi.ACTION_UPDATE_DIALOG);
        LocalBroadcastManager.getInstance(this).registerReceiver(newMessageBroadcastReceiver, intentFilter);
        super.onResume();
    }

    //остановить службу проверки сообщений
    @Override
    protected void onPause() {
        dialogPresenter.setView(null);
        dialogPresenter.stopMessagesCheckService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newMessageBroadcastReceiver);
        super.onPause();
    }

    //почистить базу в конце
    @Override
    protected void onDestroy() {
        dialogPresenter.clearDatabase();
        super.onDestroy();
    }

    //отправить сообщение на сервер
    private void sendMessage() {
        dialogPresenter.sendMessage(this, String.valueOf(message.getText()), dialogId);
        message.setText("");
    }

    //открыть настройки
    private void openSettings() {
        IntervalDialogFragment intervalDialogFragment;
        intervalDialogFragment = new IntervalDialogFragment();
        intervalDialogFragment.show(getFragmentManager(), "SetIntervalDialog");
    }

    //логаут
    private void logOut() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        dialogPresenter.logOut(this, defaultSharedPreferences.getString(SharedPreferencesApi.TOKEN, null));
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.remove(SharedPreferencesApi.LOGIN);
        editor.remove(SharedPreferencesApi.PASSWORD);
        editor.remove(SharedPreferencesApi.AVATAR);
        editor.remove(SharedPreferencesApi.TOKEN);
        editor.apply();
        dialogPresenter.clearDatabase();
        navigator.navigateToLogin(this);
        finish();
    }

    //клас для приема широковещательных сообщений
    private BroadcastReceiver newMessageBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int receivedDialogId = intent.getIntExtra("dialogId", -1);
            if (dialogId == receivedDialogId) {
                dialogPresenter.getMessagesFromServer(getApplicationContext(), dialogId);
            }
        }
    };

}
