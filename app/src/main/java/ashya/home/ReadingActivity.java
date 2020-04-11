package ashya.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ashya.home.constant.GlobalConstant;

public class ReadingActivity extends AppCompatActivity {

    public static final int MAX_QUESTION = GlobalConstant.MAX_QUESTION;

    // view
    private TextView readTextView;
    private TextView resultTextView;
    private Toast toastMessage;
    private SoundPool soundPool;

    // progress
    private List<ImageView> imageViews;

    private int score;
    private int totalQuestion;
    private int trueSound;
    private int falseSound;
    private boolean isRightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        readTextView = findViewById(R.id.textViewRead);
        resultTextView = findViewById(R.id.textViewResult);

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

        initSound();

        // Restores the 'state' of the app upon screen rotation:
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("ScoreKey");
            totalQuestion = savedInstanceState.getInt("TotalQuestionKey");
        } else {
            score = 0;
            totalQuestion = 0;
        }

        setRandom();
        isRightAnswer = false;

        final ImageView micImageView = findViewById(R.id.imageViewMic);
        micImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSpeechInput();
            }
        });

        resultTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSpeechInput();
            }
        });
    }

    private void initSound() {
        soundPool = new SoundPool(GlobalConstant.NR_OF_SIMULTANEOUS_SOUNDS,
                AudioManager.STREAM_MUSIC, 0);

        // Get the resource IDs to identify the sounds and store them in variables
        trueSound = soundPool.load(this, R.raw.applauses, 1);
        falseSound = soundPool.load(this, R.raw.laughing, 1);
    }

    private void setRandom() {
        final Random random = new Random();
        readTextView.setText(GlobalConstant.TEXT_LIST[random.nextInt(GlobalConstant.TEXT_LIST.length)]);
    }

    private void getSpeechInput() {
        if (!resultTextView.getText().toString().equals(getString(R.string.result))) {
            return;
        }

        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID");

        if (null != intent.resolveActivity(getPackageManager())) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    public void setNext(View view) {
        clearAnswer();
        setRandom();
        stopSound();
        isRightAnswer = false;
    }

    private void setNextStep() {
        totalQuestion = totalQuestion + 1;

        if (totalQuestion >= MAX_QUESTION) {
            AlertDialog.Builder alert = new AlertDialog.Builder(ReadingActivity.this);
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
        }
    }

    private void checkSpeech() {
        final String textResult = resultTextView.getText().toString();
        final String textRead = readTextView.getText().toString().toLowerCase();
        if (textResult.equals(textRead)) {

            if (toastMessage != null) {
                toastMessage.cancel();
            }

            toastMessage = Toast.makeText(ReadingActivity.this, R.string.correct_toast, Toast.LENGTH_SHORT);
            toastMessage.show();

            setScore();
            playSoundTrue();
            isRightAnswer = true;
        } else {
            toastMessage = Toast.makeText(ReadingActivity.this, R.string.incorrect_toast, Toast.LENGTH_LONG);
            toastMessage.show();
            playSoundFalse();
            isRightAnswer = false;
        }

        resultTextView.setVisibility(View.VISIBLE);
        setProgress(isRightAnswer, totalQuestion);
    }

    private void setScore() {
        score = score + 1;
    }

    private void clearAnswer() {
        resultTextView.setText(R.string.result);
    }

    private void playSoundFalse() {
        soundPool.play(falseSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    private void playSoundTrue() {
        Log.d("========ashya====", "playSoundTrue: ");
        soundPool.play(trueSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    private void stopSound() {
        if (isRightAnswer) {
            soundPool.stop(trueSound);
        } else {
            soundPool.stop(falseSound);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 10:
//                if (resultCode == RESULT_OK && null != data) {
//                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    resultTextView.setText(result.get(0));
//                    checkSpeech();
//                    setNextStep();
//                }
//        }

        if (null == soundPool) {
            initSound();
        }

        if (requestCode == 10) {
            if (resultCode == RESULT_OK && null != data) {
                Log.d("========ashya====", "resultTextView: " + soundPool);
                final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Log.d("========ashya====", "resultTextView1: " + soundPool);
                resultTextView.setText(result.get(0).toLowerCase());
                Log.d("========ashya====", "resultTextView2: " + soundPool);
                checkSpeech();
                Log.d("========ashya====", "checkSpeech: " + soundPool);
                setNextStep();
                Log.d("========ashya====", "setNextStep: ");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ScoreKey", score);
        outState.putInt("TotalQuestionKey", totalQuestion);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("========ashya====", "onPause: ");
//        if (null != soundPool) {
//            soundPool.release();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("========ashya====", "onResume: ");
        initSound();
    }
}
