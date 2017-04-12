package ru.nadocars.messanger.ui.web_site;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import ru.nadocars.messanger.R;

public class NadocarsRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nadocars_registration);

        WebView webRegistrationView = (WebView) findViewById(R.id.web_registration_view);
        String urlRegistration = "https://nadocars.ru/registration/";

        webRegistrationView.loadUrl(urlRegistration);

        // наверное надо будет сделать еше кнопкой назад возможность вернуться в LoginActivity
        // а также поставить какой-то ProgressBar
        // делать также надо проверку Интернета


    }
}
