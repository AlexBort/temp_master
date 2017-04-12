package ru.nadocars.messanger.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

//конвертирует время
public class TimeConverter {

    //конвертирует время в нужный формат
    public String convertTime(long time) {
        Date date = new Date(time * 1000);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.format(date);
    }

}
