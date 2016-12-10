package de.hwr_berlin.quizapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by oruckdeschel on 16.03.2016.
 */
public class GameElements {

    private static final String TAG = GameElements.class.getSimpleName();

    private static MediaPlayer correctSoundPlayer;
    private static MediaPlayer wrongSoundPlayer;
    private static MediaPlayer buttonClickSoundPlayer;
    private static Vibrator vibrator;

    public enum Sound {
        CORRECT,
        WRONG,
        BUTTON_CLICK,
    }

    public enum VibrateState {
        LONG,
        SHORT,
    }

    public GameElements(Context context){
        correctSoundPlayer = MediaPlayer.create(context, R.raw.correct);
        wrongSoundPlayer = MediaPlayer.create(context, R.raw.wrong);
        buttonClickSoundPlayer = MediaPlayer.create(context, R.raw.selection);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void playSound(Sound sound) {
        switch (sound) {
            case CORRECT:
                correctSoundPlayer.start();
                break;
            case WRONG:
                wrongSoundPlayer.start();
                break;
            case BUTTON_CLICK:
                buttonClickSoundPlayer.start();
                break;
            default:
                break;
        }
    }

    // For more information on how to vibrate with pattern: http://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate
    public void vibrate(VibrateState state) {
        if (vibrator == null) {
            // We already detected that the device is not able to vibrate -> vibrator is null!
            return;
        }

        if (! vibrator.hasVibrator()) {
            Log.v(TAG, "Device cannot vibrate.");
            // Set vibrator null so we don't spam the LogCat with the same message. [and to save memory ;)]
            vibrator = null;
            return;
        }

        switch(state) {
            case LONG:
                vibrator.vibrate(new long[]{0, 30, 60, 30}, -1);
                break;
            case SHORT:
                vibrator.vibrate(30);
                 break;
        }
    }

}
