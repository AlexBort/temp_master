package ru.nadocars.messanger.asynctasks;

import android.os.AsyncTask;

import java.util.List;

import ru.nadocars.messanger.data.repo.MessagesRepo;
import ru.nadocars.messanger.domain.Message;
import ru.nadocars.messanger.ui.dialog.DialogView;

public class GetMessagesFromDbTask extends AsyncTask<Void, Void, Boolean> {

    private final DialogView dialogView;
    private List<Message> messages;

    public GetMessagesFromDbTask(DialogView dialogView) {
        this.dialogView = dialogView;
    }

    //получает все сообщения с внутренней базы
    @Override
    protected Boolean doInBackground(Void... params) {
        MessagesRepo messagesRepo = new MessagesRepo();
        messages = messagesRepo.findAllMessages();
        return false;
    }

    //отображает сообщения
    @Override
    protected void onPostExecute(final Boolean success) {
        if (messages.size() > 0) {
            dialogView.showDialog(messages);
        }
    }

}
