package ru.nadocars.messanger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.nadocars.messanger.utils.CheckNewMessagesService;

//Клас запускает службу для проверки новых сообщений, после перезагрузки телефона
public class AutoStartReceiver extends BroadcastReceiver {

    //метод запускает вышеуказанную службу
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, CheckNewMessagesService.class);
        context.startService(startServiceIntent);
        Log.i("Autostart", "started");
    }

}
