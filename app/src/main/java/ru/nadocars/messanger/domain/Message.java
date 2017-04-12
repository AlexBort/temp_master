package ru.nadocars.messanger.domain;

//Обьект который представляет "сообщение"
public class Message {

    private int id;
    private int messageId;
    private String author;
    private String message;
    private int time;
    private int isNewMessage;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsNewMessage() {
        return isNewMessage;
    }

    public void setIsNewMessage(int isNewMessage) {
        this.isNewMessage = isNewMessage;
    }
}
