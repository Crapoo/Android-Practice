package me.taroli.geoquizz;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class QuizzActivity extends ActionBarActivity {

    private Button falseBtn;
    private Button trueBtn;
    private Button nextBtn;
    private TextView questionTv;

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

        nextBtn = (Button) findViewById(R.id.next_button);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        setQuestion();

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
        if (userAnswer == answer)
            res = R.string.correct_answer;
        else
            res = R.string.wrong_answer;

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
