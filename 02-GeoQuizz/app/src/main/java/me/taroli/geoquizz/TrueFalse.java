package me.taroli.geoquizz;

/**
 * Created by Matt on 1/07/15.
 */
public class TrueFalse {
    private int question;
    private boolean trueQuestion;

    public TrueFalse(int question, boolean trueQuestion) {
        this.question = question;
        this.trueQuestion = trueQuestion;
    }

    public int getQuestion() {
        return question;
    }

    public boolean isTrueQuestion() {
        return trueQuestion;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public void setTrueQuestion(boolean trueQuestion) {
        this.trueQuestion = trueQuestion;
    }
}
