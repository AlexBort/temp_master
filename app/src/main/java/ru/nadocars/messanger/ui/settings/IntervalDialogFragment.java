package ru.nadocars.messanger.ui.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.nadocars.messanger.R;
import ru.nadocars.messanger.api.SharedPreferencesApi;
import ru.nadocars.messanger.ui.DialogFragmentListener;
import ru.nadocars.messanger.utils.CheckNewMessagesService;

//Отвечает за окно где вводиться интервал обновления
public class IntervalDialogFragment extends DialogFragment {

    DialogFragmentListener dialogFragmentListener;

    //создает окно ввода интервала
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final List<String> posibleIntervalValues = new ArrayList<>();
        for (int i = 1; i <= 60; i++ ) {
            posibleIntervalValues.add(String.valueOf(i));
        }
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.fragment_interval_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.set_interval_dialog_title);
        builder.setView(view);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText intervalEditText = (EditText) ((AlertDialog) dialog).findViewById(R.id.interval);
                        String interval = null;
                        if (intervalEditText != null) {
                            interval = String.valueOf(intervalEditText.getText());
                        }
                        if (interval != null && posibleIntervalValues.contains(interval)) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(SharedPreferencesApi.CHECK_NEW_MESSAGES_INTERVAL, interval);
                            editor.apply();
                            getActivity().stopService(new Intent(getActivity(), CheckNewMessagesService.class));
                            getActivity().startService(new Intent(getActivity(), CheckNewMessagesService.class));
                            if (dialogFragmentListener != null) {
                                dialogFragmentListener.restartDialogCheckService();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Ошибка. Неправильное значение", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        Dialog dialog = builder.create();
        EditText interval = (EditText) view.findViewById(R.id.interval);
        if (interval != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            interval.setText(sharedPreferences.getString(SharedPreferencesApi.CHECK_NEW_MESSAGES_INTERVAL, "10"));
        }
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        dialogFragmentListener = (DialogFragmentListener) activity;
        super.onAttach(activity);
    }
}
