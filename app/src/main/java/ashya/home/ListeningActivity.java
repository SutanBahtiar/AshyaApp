package ashya.home;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ashya.home.constant.GlobalConstant;

public class ListeningActivity extends AppCompatActivity {

    public static final int MAX_QUESTION = GlobalConstant.MAX_QUESTION;

    // view
    private EditText answerEditText;
    private TextView answerTextView;

    // progress
    private List<ImageView> imageViews;

    private Toast toastMessage;
    private TextToSpeech textToSpeech;
    private SoundPool soundPool;

    private String soundText;
    private int score;
    private int totalQuestion;
    private int trueSound;
    private int falseSound;
    private boolean isRightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

        answerEditText = findViewById(R.id.editTextAnswer);
        answerTextView = findViewById(R.id.textViewAnswer);

        final ImageView star1ImageView = findViewById(R.id.star1ImageView);
        final ImageView star2ImageView = findViewById(R.id.star2ImageView);
        final ImageView star3ImageView = findViewById(R.id.star3ImageView);
        final ImageView star4ImageView = findViewById(R.id.star4ImageView);
        final ImageView star5ImageView = findViewById(R.id.star5ImageView);
        final ImageView star6ImageView = findViewById(R.id.star6ImageView);
        final ImageView star7ImageView = findViewById(R.id.star7ImageView);
        final ImageView star8ImageView = findViewById(R.id.star8ImageView);
        final ImageView star9ImageView = findViewById(R.id.star9ImageView);
        final ImageView star10ImageView = findViewById(R.id.star10ImageView);

        imageViews = new ArrayList<>();
        imageViews.add(star1ImageView);
        imageViews.add(star2ImageView);
        imageViews.add(star3ImageView);
        imageViews.add(star4ImageView);
        imageViews.add(star5ImageView);
        imageViews.add(star6ImageView);
        imageViews.add(star7ImageView);
        imageViews.add(star8ImageView);
        imageViews.add(star9ImageView);
        imageViews.add(star10ImageView);

        soundPool = new SoundPool(GlobalConstant.NR_OF_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, 0);

        // Get the resource IDs to identify the sounds and store them in variables
        trueSound = soundPool.load(this, R.raw.applauses, 1);
        falseSound = soundPool.load(this, R.raw.laughing, 1);

        // Restores the 'state' of the app upon screen rotation:
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("ScoreKey");
            totalQuestion = savedInstanceState.getInt("TotalQuestionKey");
        } else {
            score = 0;
            totalQuestion = 0;
        }

        isRightAnswer = false;
        setRandom();

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(new Locale("in_ID"));
                }
            }
        });
    }

    public void setSpeak(View view) {
        textToSpeech.speak(soundText, TextToSpeech.QUEUE_FLUSH, null);
        answerTextView.setText(GlobalConstant.EMPTY_TEXT);
        answerTextView.setVisibility(View.GONE);
        clearAnswer();
        stopSound();
    }

    public void setAnswer(View view) {
        final String textResult = answerEditText.getText().toString().toLowerCase();
        if (textResult.equals(GlobalConstant.EMPTY_TEXT)) {
            return;
        }

        checkAnswer(textResult);
        setNext();
    }

    private void checkAnswer(String textResult) {
        final String textRead = soundText.toLowerCase();

        if (textResult.trim().equals(textRead)) {

            if (toastMessage != null) {
                toastMessage.cancel();
            }

            toastMessage = Toast.makeText(ListeningActivity.this, R.string.correct_toast, Toast.LENGTH_SHORT);
            toastMessage.show();

            playSoundTrue();
            setScore();
            isRightAnswer = true;
        } else {
            toastMessage = Toast.makeText(ListeningActivity.this, R.string.incorrect_toast, Toast.LENGTH_LONG);
            toastMessage.show();

            playSoundFalse();
            isRightAnswer = false;
        }

        answerTextView.setText(textRead);
        answerTextView.setVisibility(View.VISIBLE);
        setProgress(isRightAnswer, totalQuestion);
    }

    private void setNext() {
        totalQuestion = totalQuestion + 1;

        if (totalQuestion >= MAX_QUESTION) {
            AlertDialog.Builder alert = new AlertDialog.Builder(ListeningActivity.this);
            alert.setTitle("Selesai");
            alert.setCancelable(false);
            alert.setMessage("Nilai kamu " + score);
            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
        } else {
            setRandom();
        }
    }

    private void setRandom() {
        final Random random = new Random();
        soundText = getString(GlobalConstant.TEXT_LIST[random.nextInt(GlobalConstant.TEXT_LIST.length)]);
    }

    private void setScore() {
        score = score + 1;
    }

    private void clearAnswer() {
        answerEditText.setText(GlobalConstant.EMPTY_TEXT);
    }

    private void playSoundFalse() {
        soundPool.play(falseSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    private void playSoundTrue() {
        soundPool.play(trueSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    private void stopSound() {
        soundPool.stop(trueSound);
        soundPool.stop(falseSound);
    }

    private void setProgress(final boolean isRightAnswer,
                             final int totalQuestion) {
        setStarColor(isRightAnswer, imageViews.get(totalQuestion));
    }

    private void setStarColor(final boolean isRightAnswer,
                              final ImageView imageView) {
        imageView.setColorFilter(ContextCompat.getColor(this, isRightAnswer ?
                R.color.yellow :
                R.color.red));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != textToSpeech) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (null != soundPool) {
            soundPool.release();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ScoreKey", score);
        outState.putInt("TotalQuestionKey", totalQuestion);
    }
}
