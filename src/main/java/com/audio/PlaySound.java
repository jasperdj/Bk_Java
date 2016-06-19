package com.audio;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by a623557 on 27-5-2016.
 */
public class PlaySound {
    static PlaySound instance = null;
    String type = "1";

    protected PlaySound(String type) { this.type = type; }

    static public PlaySound getInstance(String type) {
        if (instance == null) instance = new PlaySound(type);
        return instance;
    }

    public void play() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("tones/tone"+type+".wav");
        AudioStream audioStream = new AudioStream(in);
        AudioPlayer.player.start(audioStream);
    }
}
