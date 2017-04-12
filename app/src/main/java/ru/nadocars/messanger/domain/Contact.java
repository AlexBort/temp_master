package ru.nadocars.messanger.domain;

//Обьект которй представляет "контакт"
public class Contact {

    private String avatarUrl;
    private String avatarUri;
    private String name;
    private String timeOfLastMessage;
    private int numberOfNewMessages;
    private int dialogId;
    private int id;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeOfLastMessage() {
        return timeOfLastMessage;
    }

    public void setTimeOfLastMessage(String timeOfLastMessage) {
        this.timeOfLastMessage = timeOfLastMessage;
    }

    public int getNumberOfNewMessages() {
        return numberOfNewMessages;
    }

    public void setNumberOfNewMessages(int numberOfNewMessages) {
        this.numberOfNewMessages = numberOfNewMessages;
    }

    public int getDialogId() {
        return dialogId;
    }

    public void setDialogId(int dialogId) {
        this.dialogId = dialogId;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
