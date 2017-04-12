package ru.nadocars.messanger.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.domain.Contact;
import ru.nadocars.messanger.ui.dialog.DialogActivity;
import ru.nadocars.messanger.utils.TimeConverter;

//Клас обрабатывает отображение и взаимодействие со списком контактов
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> implements View.OnClickListener {

    private List<Contact> contacts;
    private ContactsView contactsView;
    private Bitmap defaultAvatar;
    private TimeConverter timeConverter;
    private Context context;

    public ContactsAdapter(List<Contact> contacts, Context context, ContactsView contactsView) {
        this.contacts = contacts;
        this.contactsView = contactsView;
        this.context = context;
        timeConverter = new TimeConverter();
        defaultAvatar = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    //вызываеться при отображении каждого контакта
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        contactViewHolder.contactName.setText(contacts.get(i).getName());
        ImageView dialogAvatar = contactViewHolder.avatarImageView;
        dialogAvatar.setImageBitmap(defaultAvatar);
        if (contacts.get(i).getAvatarUri() == null) {
            contactsView.getAndSetDialogAvatar(contacts.get(i).getAvatarUrl(), dialogAvatar);
        } else {
            File file = new File(contacts.get(i).getAvatarUri(), "dialogAvatar" + contacts.get(i).getId());
            Bitmap dialogAvatarBitmap = null;
            try {
                dialogAvatarBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (dialogAvatarBitmap != null) {
                contactViewHolder.avatarImageView.setImageBitmap(dialogAvatarBitmap);
            }
        }
        String timestamp = contacts.get(i).getTimeOfLastMessage();
        long timeInLongFormat = Long.parseLong(timestamp);
        String time = timeConverter.convertTime(timeInLongFormat);
        contactViewHolder.lastMessageTime.setText(time);
        contactViewHolder.dialogId.setText(String.valueOf(contacts.get(i).getDialogId()));
        if (contacts.get(i).getNumberOfNewMessages() > 0) {
            contactViewHolder.numberOfNewMessages.setText(String.valueOf(contacts.get(i).getNumberOfNewMessages()));
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            Vibrator vibrator;
            switch (audioManager.getRingerMode()) {
                case AudioManager.RINGER_MODE_VIBRATE:
                    vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                    break;
            }
        } else {
            contactViewHolder.numberOfNewMessages.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts_list, viewGroup, false);
        itemView.setOnClickListener(this);
        return new ContactViewHolder(itemView);
    }

    //вызываеться при клике на контакт
    @Override
    public void onClick(View v) {
        ImageView avatarImageView = (ImageView) v.findViewById(R.id.contacts_avatar);
        Bitmap dialogAvatar = ((BitmapDrawable) avatarImageView.getDrawable()).getBitmap();
        String dialogAvatarPath = contactsView.saveDialogAvatar(dialogAvatar);
        int dialogId = Integer.valueOf(String.valueOf(((TextView) v.findViewById(R.id.dialog_id)).getText()));
        Intent intent = new Intent(v.getContext(), DialogActivity.class);
        intent.putExtra("dialogId", dialogId);
        intent.putExtra("dialogAvatarPath", dialogAvatarPath);
        v.getContext().startActivity(intent);
    }

    //класс создает в памяти обьекты для отображения контакта.
    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView contactName;
        protected ImageView avatarImageView;
        protected TextView lastMessageTime;
        protected TextView numberOfNewMessages;
        protected TextView dialogId;

        public ContactViewHolder(View v) {
            super(v);
            contactName = (TextView) v.findViewById(R.id.contact_name);
            avatarImageView = (ImageView) v.findViewById(R.id.contacts_avatar);
            lastMessageTime = (TextView) v.findViewById(R.id.last_message_time);
            numberOfNewMessages = (TextView) v.findViewById(R.id.new_messages);
            dialogId = (TextView) v.findViewById(R.id.dialog_id);
        }
    }

}