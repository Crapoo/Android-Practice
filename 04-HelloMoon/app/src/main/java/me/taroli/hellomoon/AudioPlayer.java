package me.taroli.hellomoon;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Matt on 13/07/15.
 */
public class AudioPlayer {
    private MediaPlayer player;

    public void stop() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void play(Context c) {
        stop();

        player = MediaPlayer.create(c, R.raw.one_small_step);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });

        player.start();
    }
}
