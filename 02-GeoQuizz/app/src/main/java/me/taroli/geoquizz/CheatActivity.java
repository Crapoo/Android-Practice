package me.taroli.geoquizz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Matt on 3/07/15.
 */
public class CheatActivity extends Activity {

    public static  final String EXTRA_ANSWER = "me.taroli.geoquizz.answer_true";
    public static final String EXTRA_SHOWN = "me.taroli.geoquizz.answer_shown";

    private boolean answer;
    private TextView answerTv;
    private Button showAnswer;

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        setAnswerShownResult(false);

        answer = getIntent().getBooleanExtra(EXTRA_ANSWER, false);

        answerTv = (TextView) findViewById(R.id.answer_textview);

        showAnswer = (Button) findViewById(R.id.show_answer_btn);
        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer) {
                    answerTv.setText(R.string.true_btn);
                } else {
                    answerTv.setText(R.string.false_btn);
                }
                setAnswerShownResult(true);
            }
        });
    }
}
