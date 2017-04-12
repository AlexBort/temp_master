package ru.nadocars.messanger.ui.dialog;

import android.content.Context;

import java.util.List;

import ru.nadocars.messanger.data.json.MessagesResponse;
import ru.nadocars.messanger.ui.Presenter;

public interface DialogPresenter extends Presenter<DialogView> {

    void getMessagesFromDb(Context context, int dialogId);
    void getMessagesFromServer(Context context, int dialogId);
    void logOut(Context context, String token);
    void showMessages(List<MessagesResponse> messages);
    void startMessagesCheckService(Context context, int dialogId);
    void stopMessagesCheckService();
    void sendMessage(Context context, String message, int dialogId);
    void showErrorWrongInput(String errorMessage);
    void clearDatabase();

}
