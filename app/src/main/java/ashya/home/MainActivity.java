package ashya.home;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showCalculationActivity(View view) {
        final Intent calculationActivityIntent = new Intent(MainActivity.this, CalculationActivity.class);
        startActivity(calculationActivityIntent);
    }

    public void showReadingActivity(View view) {
        final Intent readingActivityIntent = new Intent(MainActivity.this, ReadingActivity.class);
        startActivity(readingActivityIntent);
    }

    public void showListeningActivity(View view) {
        final Intent listeningActivityIntent = new Intent(MainActivity.this, ListeningActivity.class);
        startActivity(listeningActivityIntent);
    }

    public void showMusicActivity(View view) {
        final Intent listeningMusicIntent = new Intent(MainActivity.this, MusicActivity.class);
        startActivity(listeningMusicIntent);
    }

    private void startMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.quest);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    private void stopMusic() {
        mediaPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mediaPlayer) {
            stopMusic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMusic();
    }
}
