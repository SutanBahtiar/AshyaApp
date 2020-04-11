package ashya.home;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.Random;

import ashya.home.constant.GlobalConstant;

public class CalculationActivity extends AppCompatActivity {

    private static final String[] OPERATORS = {GlobalConstant.SUBTRACT, GlobalConstant.ADD};
    private static final int ANSWER_HELPER = 0;
    private static final int ANSWER_A1 = 1;
    private static final int ANSWER_A2 = 2;
    private static final int TWO = 10;
    private static final String ZERO = "0";

    // view
    private TextView paramA1TextView;
    private TextView paramA2TextView;
    private TextView paramB1TextView;
    private TextView paramB2TextView;
    private TextView operatorTextView;
    private EditText answerEditTextHelper;
    private EditText answerEditTextA1;
    private EditText answerEditTextA2;
    private SoundPool soundPool;

    // progress
    private List<ImageView> imageViews;

    private Toast toastMessage;

    private int score;
    private int totalQuestion;
    private int trueSound;
    private int falseSound;
    private boolean isRightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);

        // initialize view
        paramA1TextView = findViewById(R.id.textViewParamA1);
        paramA2TextView = findViewById(R.id.textViewParamA2);
        paramB1TextView = findViewById(R.id.textViewParamB1);
        paramB2TextView = findViewById(R.id.textViewParamB2);
        operatorTextView = findViewById(R.id.textViewOperator);
        answerEditTextHelper = findViewById(R.id.editTextAnswerHelper);
        answerEditTextA1 = findViewById(R.id.editTextAnswerA1);
        answerEditTextA2 = findViewById(R.id.editTextAnswerA2);

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

        soundPool = new SoundPool(GlobalConstant.NR_OF_SIMULTANEOUS_SOUNDS,
                AudioManager.STREAM_MUSIC, 0);

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

        // init
        setRandom();

        answerEditTextA1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAnswer(ANSWER_A1);
            }
        });

        answerEditTextA2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals(GlobalConstant.EMPTY_TEXT)) {
                    answerEditTextA1.requestFocus();
                }
            }
        });

        answerEditTextA2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAnswer(ANSWER_A2);
                stopSound();
            }
        });

        answerEditTextHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAnswer(ANSWER_HELPER);
            }
        });
    }

    public void setNext(View view) {
        clearAnswer();
        setRandom();
        stopSound();
    }

    public void setAnswer(View view) {
        final String answerA1 = answerEditTextA1.getText().toString();
        final String answerA2 = answerEditTextA2.getText().toString();
        final String answerString = answerA1 + answerA2;

        if (answerString.equals(GlobalConstant.EMPTY_TEXT)) {
            return;
        }

        final int answer = Integer.parseInt(answerString);

        // Can cancel the Toast message if there is one on screen and a new answer
        // has been submitted.
        if (toastMessage != null) {
            toastMessage.cancel();
        }

        if (checkAnswer(answer)) {
            toastMessage = Toast.makeText(CalculationActivity.this,
                    R.string.correct_toast, Toast.LENGTH_SHORT);
            toastMessage.show();
            setScore();
            isRightAnswer = true;
            playSoundTrue();
        } else {
            toastMessage = Toast.makeText(CalculationActivity.this,
                    R.string.incorrect_toast, Toast.LENGTH_LONG);
            toastMessage.show();
            isRightAnswer = false;
            playSoundFalse();
        }

        setProgress(isRightAnswer, totalQuestion);
        setNextStep();
    }

    private boolean checkAnswer(final int answer) {
        final String operator = operatorTextView.getText().toString();
        final int param1 = getParam1();
        final int param2 = getParam2();

        if (operator.equals(GlobalConstant.ADD)) {
            return param1 + param2 == answer;
        }

        if (operator.equals(GlobalConstant.SUBTRACT)) {
            return param1 - param2 == answer;
        }

        return false;
    }

    private void setNextStep() {
        totalQuestion = totalQuestion + 1;

        if (totalQuestion >= GlobalConstant.MAX_QUESTION) {
            AlertDialog.Builder alert = new AlertDialog.Builder(CalculationActivity.this);
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
            clearAnswer();
            setRandom();
        }
    }

    private void setRandom() {
        setRandomOperator();
        setRandomQuestion();

        answerEditTextA2.requestFocus();
    }

    private void setRandomOperator() {
        final int number = new Random().nextInt(2);
        operatorTextView.setText(OPERATORS[number]);
    }

    private void setScore() {
        score = score + 1;
    }

    private void setRandomQuestion() {
        final Random random = new Random();

        int numberParamA1 = random.nextInt(2);
        int numberParamA2 = random.nextInt(10);
        final int numberParam1 = (numberParamA1 * TWO) + numberParamA2;

        int numberParamB1 = random.nextInt(2);
        int numberParamB2 = random.nextInt(10);
        final int numberParam2 = (numberParamB1 * TWO) + numberParamB2;

        if (operatorTextView.getText().equals(GlobalConstant.SUBTRACT)
                && numberParam1 < numberParam2) {
            numberParamA1 = numberParamB1;
            numberParamA2 = numberParamB2;
            numberParamB1 = numberParamA1;
            numberParamB2 = numberParamA2;
        }

        paramA1TextView.setText(numberParamA1 == 0 ? GlobalConstant.EMPTY_TEXT : String.valueOf(numberParamA1));
        paramA2TextView.setText(String.valueOf(numberParamA2));
        paramB1TextView.setText(numberParamB1 == 0 ? GlobalConstant.EMPTY_TEXT : String.valueOf(numberParamB1));
        paramB2TextView.setText(String.valueOf(numberParamB2));
    }

    private int getParam1() {
        final String paramA1 = paramA1TextView.getText().toString();
        final String paramA2 = paramA2TextView.getText().toString();
        final String param1String = getParamValue(paramA1) + getParamValue(paramA2);
        return Integer.parseInt(param1String);
    }

    private int getParam2() {
        final String paramB1 = paramB1TextView.getText().toString();
        final String paramB2 = paramB2TextView.getText().toString();
        final String param2String = getParamValue(paramB1) + getParamValue(paramB2);
        return Integer.parseInt(param2String);
    }

    private String getParamValue(String param) {
        return param.equals(GlobalConstant.EMPTY_TEXT) ? ZERO : param;
    }

    private void clearAnswer() {
        clearAnswer(ANSWER_A2);
        clearAnswer(ANSWER_A1);
        clearAnswer(ANSWER_HELPER);
    }

    private void clearAnswer(int answerNumber) {
        if (answerNumber == ANSWER_A1) {
            answerEditTextA1.setText(GlobalConstant.EMPTY_TEXT);
        } else if (answerNumber == ANSWER_A2) {
            answerEditTextA2.setText(GlobalConstant.EMPTY_TEXT);
        } else if (answerNumber == ANSWER_HELPER) {
            answerEditTextHelper.setText(GlobalConstant.EMPTY_TEXT);
        }
    }

    private void stopSound() {
        if (isRightAnswer) {
            soundPool.stop(trueSound);
        } else {
            soundPool.stop(falseSound);
        }

        isRightAnswer = false;
    }

    private void playSoundFalse() {
        soundPool.play(falseSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
    }

    private void playSoundTrue() {
        soundPool.play(trueSound, GlobalConstant.LEFT_VOLUME, GlobalConstant.RIGHT_VOLUME,
                0, GlobalConstant.NO_LOOP, GlobalConstant.NORMAL_PLAY_RATE);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ScoreKey", score);
        outState.putInt("TotalQuestionKey", totalQuestion);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != soundPool) {
            soundPool.release();
        }
    }
}
