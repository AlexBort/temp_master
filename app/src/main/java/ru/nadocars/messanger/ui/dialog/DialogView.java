package ru.nadocars.messanger.ui.dialog;

import android.content.Context;

import java.util.List;

import ru.nadocars.messanger.domain.Message;

public interface DialogView {

    void showDialog(List<Message> messages);
    Context getContext();
    void showErrorWrongInput(String errorMessage);
}
