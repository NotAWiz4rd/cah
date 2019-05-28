package com.afms.cahgame.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {
    private int id;
    private String owner;
    private String message;

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public Message(int id, String owner, String message) {
        this.id = id;
        this.owner = owner;
        this.message = message;
    }

    protected Message(Parcel source) {
        id = source.readInt();
        owner = source.readString();
        message = source.readString();
    }

    public  Message() {
        this.id = -1;
        this.owner = "";
        this.message = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(owner);
        dest.writeString(message);
    }
}
