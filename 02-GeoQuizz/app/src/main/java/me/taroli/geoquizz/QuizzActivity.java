package me.taroli.geoquizz;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class QuizzActivity extends Activity {

    private static final String TAG = "QuizzActivity";
    private static final String INDEX = "index";
    private static final String ISCHEATER = "isCheater";

    private Button falseBtn;
    private Button trueBtn;
    private Button nextBtn;
    private Button cheatBtn;
    private TextView questionTv;
    private boolean isCheater;

    private TrueFalse[] questions = new TrueFalse[]{
            new TrueFalse(R.string.question_africa, true),
            new TrueFalse(R.string.question_americas, true),
            new TrueFalse(R.string.question_asia, true),
            new TrueFalse(R.string.question_mideast, false),
            new TrueFalse(R.string.question_oceans, true)
    };

    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_quizz);

        trueBtn = (Button) findViewById(R.id.true_btn);
        trueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAnswer(true);
            }
        });

        falseBtn = (Button) findViewById(R.id.false_btn);
        falseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAnswer(false);
            }
        });

        nextBtn = (Button) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheater = false;
                nextQuestion();
            }
        });
        questionTv = (TextView) findViewById(R.id.question_textview);
        questionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        cheatBtn = (Button) findViewById(R.id.cheat_btn);
        cheatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QuizzActivity.this, CheatActivity.class);
                i.putExtra(CheatActivity.EXTRA_ANSWER,
                        questions[currentIndex].isTrueQuestion());
                startActivityForResult(i, 0);
            }
        });

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(INDEX, 0);
            isCheater = savedInstanceState.getBoolean(ISCHEATER, false);
        }

        setQuestion();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putInt(INDEX, currentIndex);
        outState.putBoolean(ISCHEATER, isCheater);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        isCheater = data.getBooleanExtra(CheatActivity.EXTRA_SHOWN, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void setQuestion() {
        int question = questions[currentIndex].getQuestion();
        questionTv.setText(question);
    }

    private void nextQuestion() {
        currentIndex = (currentIndex + 1) % questions.length;
        setQuestion();
    }

    private void verifyAnswer(boolean userAnswer) {
        boolean answer = questions[currentIndex].isTrueQuestion();
        int res;
        if (isCheater) {
            res = R.string.judging;
        } else {
            if (userAnswer == answer) {
                res = R.string.correct_answer;
            } else {
                res = R.string.wrong_answer;
            }
        }
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
        nextQuestion();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quizz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
