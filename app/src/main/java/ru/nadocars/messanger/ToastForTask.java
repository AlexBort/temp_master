package ru.nadocars.messanger;

import android.util.Log;
import android.widget.Toast;

import ru.nadocars.messanger.ui.profile.ProfileActivity;

/**
 * Created by User on 27.12.2016.
 */

public class ToastForTask {

    public static void printToast(final String TAG, final String textForToast) {
        ProfileActivity.appCompatActivityContext.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ProfileActivity.appCompatActivityContext, textForToast, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "run: " + "ЗАХОДИТ В ЭТОТ БЛОК");
            }
        });

    }

}
