package com.kiewic.nanochat;

/**
 * Created by gilberto on 11/14/15.
 */
public class ChatMessage {
    private String name;
    private String text;
    private String image;

    public ChatMessage() {
        // necessary for Firebase's deserializer
    }

    public ChatMessage(String name, String text, String image) {
        this.name = name;
        this.text = text;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }
}
