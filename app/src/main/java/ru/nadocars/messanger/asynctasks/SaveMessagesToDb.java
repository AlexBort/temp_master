package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import ru.nadocars.messanger.data.repo.MessagesRepo;
import ru.nadocars.messanger.domain.Message;

public class SaveMessagesToDb extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private List<Message> messages;

    public SaveMessagesToDb(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    //сохраняет сообщения в базу
    @Override
    protected Boolean doInBackground(Void... params) {
        MessagesRepo messagesRepo = new MessagesRepo();
        messagesRepo.insertAll(messages, context);
        return false;
    }


}
