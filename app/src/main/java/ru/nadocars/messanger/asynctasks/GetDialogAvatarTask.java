package ru.nadocars.messanger.asynctasks;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import ru.nadocars.messanger.data.repo.ContactsRepo;
import ru.nadocars.messanger.domain.Contact;

//загружает с сервера аватарку для диалога

public class GetDialogAvatarTask extends AsyncTask<Void, Void, Boolean> {

    private final Context context;
    private final String avatarUrl;
    private final ConnectivityManager connectivityManager;
    private final ImageView avatarView;
    private Bitmap avatar;

    public GetDialogAvatarTask(Context context, ImageView avatarView, String avatarUrl) {
        this.context = context;
        this.avatarView = avatarView;
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.avatarUrl = avatarUrl;
    }


    //загружает с сервера аватарку для диалога
    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isSuccessful = false;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aNetworkInfo : networkInfo) {
            if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                InputStream inputStream;
                try {

                    URL url = new URL(avatarUrl);
                    URLConnection urlConnection = url.openConnection();
                    inputStream = urlConnection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    avatar = BitmapFactory.decodeStream(bufferedInputStream);
                    inputStream.close();
                    isSuccessful = true;
                } catch (IOException e) {
                    Log.d("ImageManager", "Error: " + e);
                }
            }
        }
        return isSuccessful;
    }

    //сохраняет аватарку во внутренней памяти
    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            avatarView.setImageBitmap(avatar);
            ContactsRepo contactsRepo = new ContactsRepo();
            Contact contact = contactsRepo.findContactByUrl(avatarUrl);
            ContextWrapper contextWrapper = new ContextWrapper(context);
            File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
            File fullPathToDialogAvatar = new File(directory, "dialogAvatar" + contact.getId());
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(fullPathToDialogAvatar);
                avatar.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            contact.setAvatarUri(directory.getAbsolutePath());
            saveDialogAvatarUri(contact);
        }
    }

    //сохраняет путь к аватарке в преференсах
    private void saveDialogAvatarUri(Contact contact) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(contact.getAvatarUrl(), contact.getAvatarUri());
        editor.apply();
    }

}