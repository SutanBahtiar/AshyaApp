package ashya.home;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ashya.home.constant.GlobalConstant;

public class MusicActivity extends AppCompatActivity {

    private SoundPool soundPool;

    private int doSound;
    private int reSound;
    private int miSound;
    private int faSound;
    private int solSound;
    private int laSound;
    private int siSound;
    private int doDoSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        soundPool = new SoundPool(GlobalConstant.NR_OF_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, 0);

        // Get the resource IDs to identify the sounds and store them in variables
        doSound = soundPool.load(this, R.raw.c_do, 1);
        reSound = soundPool.load(this, R.raw.d_re, 1);
        miSound = soundPool.load(this, R.raw.e_mi, 1);
        faSound = soundPool.load(this, R.raw.f_fa, 1);
        solSound = soundPool.load(this, R.raw.g_sol, 1);
        laSound = soundPool.load(this, R.raw.a_la, 1);
        siSound = soundPool.load(this, R.raw.b_si, 1);
        doDoSound = soundPool.load(this, R.raw.c_do_octave, 1);
    }

    public void playDoSound(View view) {
        soundPool.play(doSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    public void playReSound(View view) {
        soundPool.play(reSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    public void playMiSound(View view) {
        soundPool.play(miSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    public void playFaSound(View view) {
        soundPool.play(faSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    public void playSolSound(View view) {
        soundPool.play(solSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    public void playLaSound(View view) {
        soundPool.play(laSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    public void playSiSound(View view) {
        soundPool.play(siSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    public void playDoDoSound(View view) {
        soundPool.play(doDoSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }
}
