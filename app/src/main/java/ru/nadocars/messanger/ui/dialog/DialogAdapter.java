package ru.nadocars.messanger.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.asynctasks.MessageReadedTask;
import ru.nadocars.messanger.domain.Message;
import ru.nadocars.messanger.utils.TimeConverter;

//Клас для создания/взаимодействия со списком сообщений
public class DialogAdapter extends ArrayAdapter<Message> {

    private final Context context;
    private final List<Message> messages;
    private final TimeConverter timeConverter;
    private int dialogId;

    public DialogAdapter(Context context, int resource, List<Message> messages, int dialogId) {
        super(context, resource, messages);
        this.context = context;
        this.messages = messages;
        this.dialogId = dialogId;
        timeConverter = new TimeConverter();
    }

    //Отображение сообщения
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.messeges_list, parent, false);
        TextView authorTextView = (TextView) rowView.findViewById(R.id.message_author);
        TextView timeTextView = (TextView) rowView.findViewById(R.id.message_time);
        TextView messageTextView = (TextView) rowView.findViewById(R.id.message);

        if (messages.get(position).getIsNewMessage() == 1) {
            MessageReadedTask messageReadedTask = new MessageReadedTask(context, messages.get(position).getMessageId(), dialogId);
            messageReadedTask.execute();
        }

        authorTextView.setText(messages.get(position).getAuthor());
        timeTextView.setText(timeConverter.convertTime(messages.get(position).getTime()));
        messageTextView.setText(messages.get(position).getMessage());
        return rowView;
    }

}
