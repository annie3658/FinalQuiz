package annie.com.generalknowledgequiz.controller;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import annie.com.generalknowledgequiz.R;
import annie.com.generalknowledgequiz.databases.DatabaseEnAccess;
import annie.com.generalknowledgequiz.databases.DatabaseRoAccess;
import annie.com.generalknowledgequiz.model.User;
import annie.com.generalknowledgequiz.model.Question;

/**
 * Created by Annie on 12/05/2017.
 */

public class QuizActivity extends AppCompatActivity {



    private List<Question> mAllQuestions, mQuestionsList;
    private Question mCurrentQuestion;
    private TextView mQuestion, mNoOfQuestions, mShowAnswer, mTimer;
    private RadioButton mOptionA, mOptionB, mOptionC, mOptionD, mAnswer;
    private RadioGroup mRadioGroup;
    private Button mNextBtn, mShowAnswerBtn, mFiftyBtn, mCancelBtn;
    private int mObtainedScore, mQuestionId, mAnsweredQuestions;
    private boolean mDefaultLanguage = false, mPressedFiftyBtn, mPressedShowAnswerBtn;
    private DatabaseEnAccess mDatabaseEnglish;
    private DatabaseRoAccess mDatabaseRomanian;
    private String mCorrectToast, mIncorrectToast, mSelectAnOptionToast, mEnterNameToast, mUsernameInput, mSelectedLanguage, mSelectedDifficulty;
    private CountDownTimer mCountDownTimer;
    private long mMillisUntilFinished, mInterval;
    private final Bundle mBundle = new Bundle();
    private MediaPlayer mp,buttonSound;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);
        mObtainedScore = 0;
        mQuestionId = 0;
        mMillisUntilFinished = 22000;
        mInterval = 1000;
        mp=MediaPlayer.create(this, R.raw.clock_tick);
        buttonSound=MediaPlayer.create(this,R.raw.button);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        mDatabaseUsers = database.getReference("usersScores");

        Bundle extras = getIntent().getExtras();
        final String language = extras.getString("Language");
        final String difficulty = extras.getString("Difficulty");

        mSelectedLanguage = language;
        mSelectedDifficulty = difficulty;
        mDatabaseEnglish = DatabaseEnAccess.getInstance(this);
        mDatabaseRomanian = DatabaseRoAccess.getInstance(this);

        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        switch (language) {
            case "English":
                setEnglish();
                break;
            case "Romanian":
                setRomanian();
                break;
            default:
                setEnglish();
        }


        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animTranslate);
                buttonSound.start();
                mAnswer = (RadioButton) findViewById(mRadioGroup.getCheckedRadioButtonId());
                if (mAnswer != null) {
                    if (mCurrentQuestion.getAnswer().equals(mAnswer.getText())) {
                        mObtainedScore++;
                        Toast.makeText(QuizActivity.this, mCorrectToast, Toast.LENGTH_SHORT).show();
                        mAnswer.setTextColor(Color.parseColor("#ff99cc00"));


                    } else {
                        mAnswer.setTextColor(Color.parseColor("#ff0000"));
                        Toast.makeText(QuizActivity.this, mIncorrectToast, Toast.LENGTH_SHORT).show();
                    }
                    if (mQuestionId < mQuestionsList.size()) {
                        mCurrentQuestion = mQuestionsList.get(mQuestionId);
                        disableButtons();
                        delaySetQuestion();
                        delayTimer();

                    } else {
                        mCountDownTimer.cancel();
                        mp.stop();
                        disableButtons();
                        createBundle(mBundle, language,mSelectedDifficulty);
                        delayAlertDialog();
                    }
                    mp.pause();
                    mp.seekTo(0);
                } else {
                    Toast.makeText(QuizActivity.this, mSelectAnOptionToast, Toast.LENGTH_SHORT).show();
                }
                mRadioGroup.clearCheck();
            }
        });

        mShowAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                buttonSound.start();
                mShowAnswer.setText(mCurrentQuestion.getAnswer());
                mPressedShowAnswerBtn = true;
                mShowAnswerBtn.setEnabled(false);
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                mp.stop();
                buttonSound.start();
                Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mFiftyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                buttonSound.start();
                mRadioGroup.clearCheck();
                Random random = new Random();
                mPressedFiftyBtn = true;
                int i = 0, usedNumber = 0;
                int correctAnswer = getCorrectAnswerButton();
                while (i < 2) {
                    int number = random.nextInt(4 - 1 + 1) + 1;

                    if (number != correctAnswer && number != usedNumber) {

                        switch (number) {
                            case 1:
                                mOptionA.setEnabled(false);
                                usedNumber = number;
                                break;
                            case 2:
                                mOptionB.setEnabled(false);
                                usedNumber = number;
                                break;
                            case 3:
                                mOptionC.setEnabled(false);
                                usedNumber = number;
                                break;
                            case 4:
                                mOptionD.setEnabled(false);
                                usedNumber = number;
                                break;
                        }

                        i++;
                    }

                }
                mFiftyBtn.setEnabled(false);

            }
        });


        mCountDownTimer = new CountDownTimer(mMillisUntilFinished, mInterval) {
            @Override
            public void onTick(long millisUntilFinished) {

                long millis = millisUntilFinished;
                String time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                if(time.equals("00:21"))
                {
                    mp.start();
                }
                mTimer.setText(time);
            }

            @Override
            public void onFinish() {
                Log.i("onFinish", "entered onFinish()\n\n");
                mAnswer = (RadioButton) findViewById(mRadioGroup.getCheckedRadioButtonId());
                if (mAnswer != null) {
                    if (mCurrentQuestion.getAnswer().equals(mAnswer.getText())) {
                        mObtainedScore++;
                        Log.i("onFinish->mAnswer!=null", "correct mAnswer\n\n");
                        Toast.makeText(QuizActivity.this, mCorrectToast, Toast.LENGTH_SHORT).show();
                        mAnswer.setTextColor(Color.parseColor("#ff99cc00"));


                    } else {
                        Log.i("onFinish->mAnswer!=null", "incorrect mAnswer\n\n");
                        mAnswer.setTextColor(Color.parseColor("#ff0000"));
                        Toast.makeText(QuizActivity.this, mIncorrectToast, Toast.LENGTH_SHORT).show();
                    }
                    if (mQuestionId < mQuestionsList.size()) {
                        Log.i("questionID", String.valueOf(mQuestionId) + "\n");
                        mCurrentQuestion = mQuestionsList.get(mQuestionId);
                        delaySetQuestion();
                        delayTimer();
                        Log.i("questionID", String.valueOf(mQuestionId) + "\n");

                    } else {

                        createBundle(mBundle, language,mSelectedDifficulty);
                        delayAlertDialog();

                    }

                } else {
                    Log.i("onFinish->mAnswer==null", "null mAnswer\n\n");
                    if (mQuestionId < mQuestionsList.size()) {
                        mCurrentQuestion = mQuestionsList.get(mQuestionId);
                        setQuestionsView();
                        mCountDownTimer.start();
                        Log.i("onFinish()", "restart time\n\n");
                    } else {
                        mCountDownTimer.cancel();
                        createBundle(mBundle, language, mSelectedDifficulty);
                        disableButtons();
                        alertDialog();
                    }
                }
                mRadioGroup.clearCheck();

            }
        };
        mCountDownTimer.start();
        Log.i("startTime_1st", "start initial time\n\n");
    }


    public void setEnglish() {
        mCorrectToast = "Correct";
        mIncorrectToast = "Incorrect";
        mSelectAnOptionToast = "Please select an answer";
        mEnterNameToast = "Please enter a name";
        mDefaultLanguage = false;
        mDatabaseEnglish.open();

        switch (mSelectedDifficulty) {
            case "easy":
                mAllQuestions = mDatabaseEnglish.getDifficultyEnglishQuestions(mSelectedDifficulty);
                Collections.shuffle(mAllQuestions);
                mQuestionsList = new ArrayList<Question>(mAllQuestions.subList(0, 10));
                break;
            case "medium":
                mAllQuestions = mDatabaseEnglish.getEnglishQuestions();
                Collections.shuffle(mAllQuestions);
                mQuestionsList = new ArrayList<Question>(mAllQuestions.subList(0, 10));
                break;
            case "hard":
                mAllQuestions = mDatabaseEnglish.getDifficultyEnglishQuestions(mSelectedDifficulty);
                Collections.shuffle(mAllQuestions);
                mQuestionsList = new ArrayList<Question>(mAllQuestions.subList(0, 10));
                break;
            default:
                mAllQuestions = mDatabaseEnglish.getDifficultyEnglishQuestions(mSelectedDifficulty);
                Collections.shuffle(mAllQuestions);
                mQuestionsList = new ArrayList<Question>(mAllQuestions.subList(0, 10));
        }

        init();
        mCurrentQuestion = mQuestionsList.get(mQuestionId);
        setQuestionsView();
        mDatabaseEnglish.close();

    }

    public void setRomanian() {
        mCorrectToast = "Corect";
        mIncorrectToast = "Greșit";
        mSelectAnOptionToast = "Selectați un răspuns";
        mEnterNameToast = "Introduceti un nume vă rog";
        mDefaultLanguage = true;
        mDatabaseRomanian.open();

        switch (mSelectedDifficulty) {
            case "easy":
                mAllQuestions = mDatabaseRomanian.getDifficultyRomanianQuestions(mSelectedDifficulty);
                Collections.shuffle(mAllQuestions);
                mQuestionsList = new ArrayList<Question>(mAllQuestions.subList(0, 10));
                break;
            case "medium":
                mAllQuestions = mDatabaseRomanian.getRomanianQuestions();
                Collections.shuffle(mAllQuestions);
                mQuestionsList = new ArrayList<Question>(mAllQuestions.subList(0, 10));
                break;
            case "hard":
                mAllQuestions = mDatabaseRomanian.getDifficultyRomanianQuestions(mSelectedDifficulty);
                Collections.shuffle(mAllQuestions);
                mQuestionsList = new ArrayList<Question>(mAllQuestions.subList(0, 10));
                break;
            default:
                mAllQuestions = mDatabaseRomanian.getDifficultyRomanianQuestions(mSelectedDifficulty);
                Collections.shuffle(mAllQuestions);
                mQuestionsList = new ArrayList<Question>(mAllQuestions.subList(0, 10));
        }

        mDefaultLanguage = true;
        init();
        mCurrentQuestion = mQuestionsList.get(mQuestionId);
        setQuestionsView();
        mDatabaseRomanian.close();
    }

    public void init() {
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mShowAnswer = (TextView) findViewById(R.id.textViewAnsweredShown);
        mNoOfQuestions = (TextView) findViewById(R.id.tvNoOfQuestions);
        mQuestion = (TextView) findViewById(R.id.tvQuestion);
        mTimer = (TextView) findViewById(R.id.textViewTimer);
        mOptionA = (RadioButton) findViewById(R.id.optionARadioBtn);
        mOptionB = (RadioButton) findViewById(R.id.optionBRadioBtn);
        mOptionC = (RadioButton) findViewById(R.id.optionCRadioBtn);
        mOptionD = (RadioButton) findViewById(R.id.optionDRadioBtn);
        mNextBtn = (Button) findViewById(R.id.nextBtn);
        mFiftyBtn = (Button) findViewById(R.id.fiftyBtn);
        mShowAnswerBtn = (Button) findViewById(R.id.showAnswerBtn);
        mCancelBtn = (Button) findViewById(R.id.cancelButton);

    }

    private void setQuestionsView() {
        Log.i("setQuestion", "entered setQuestion()\n\n");
        mOptionA.setChecked(false);
        mOptionB.setChecked(false);
        mOptionC.setChecked(false);
        mOptionD.setChecked(false);
        mTimer.setText("00:20");
        mAnsweredQuestions = mQuestionId + 1;
        if (mDefaultLanguage == false) {
            mNoOfQuestions.setText("Question " + mAnsweredQuestions + " of "+ mQuestionsList.size());
        } else {
            mNoOfQuestions.setText("Întrebarea " + mAnsweredQuestions + " din "+ mQuestionsList.size());
        }
        Log.i("questionID", String.valueOf(mQuestionId) + "\n");
        mQuestion.setText(mCurrentQuestion.getQuestion());
        mOptionA.setText(mCurrentQuestion.getOptionA());
        mOptionB.setText(mCurrentQuestion.getOptionB());
        mOptionC.setText(mCurrentQuestion.getOptionC());
        mOptionD.setText(mCurrentQuestion.getOptionD());
        mQuestionId++;
        Log.i("questionID++", String.valueOf(mQuestionId) + "\n");
    }

    public void alertDialog() {

        LinearLayout layout = new LinearLayout(this);
        LayoutInflater.from(this).inflate(R.layout.alert_dialogue_layout, layout);

        final EditText etUserName = (EditText) layout.findViewById(R.id.editTextUserName);
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setPositiveButton("Ok", null)
                .setView(layout)
                .create();

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsernameInput = etUserName.getEditableText().toString().trim();

                if (mUsernameInput.length() == 0) {
                    Toast.makeText(QuizActivity.this, mEnterNameToast, Toast.LENGTH_SHORT).show();
                    return;
                }
                addUser(mUsernameInput, mObtainedScore,mSelectedDifficulty);
                startFinalActivity(etUserName.getText().toString(), mBundle);
                alertDialog.dismiss();
            }
        });


    }

    public void delaySetQuestion() {
        Log.i("delaySetQuestion", "entered delaySetQuestion()\n\n");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCountDownTimer.cancel();
                resetRadioButtons();
                setQuestionsView();
                enableButtons();

            }
        }, 2000);

    }

    public void delayAlertDialog() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog();
            }
        }, 2000);
    }

    public void delayTimer() {
        Log.i("delayTimer", "entered delayTimer()\n\n");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCountDownTimer.start();

            }
        }, 2000);
    }

    public int getCorrectAnswerButton() {

        if (mCurrentQuestion.getAnswer().equals(mOptionA.getText().toString()))
            return 1;
        else if (mCurrentQuestion.getAnswer().equals(mOptionB.getText().toString()))
            return 2;
        else if (mCurrentQuestion.getAnswer().equals(mOptionC.getText().toString()))
            return 3;
        else if (mCurrentQuestion.getAnswer().equals(mOptionD.getText().toString()))
            return 4;
        else return 0;
    }

    public void startFinalActivity(String username, Bundle b) {
        b.putString("Username", username);
        Intent intent = new Intent(QuizActivity.this, ResultsActivity.class);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public void createBundle(Bundle bundle, String language, String difficulty) {
        bundle.putInt("score", mObtainedScore);
        bundle.putInt("totalQs", mQuestionsList.size());
        bundle.putString("Language", language);
        bundle.putString("Difficulty",difficulty);
    }

    public void resetRadioButtons() {
        mShowAnswer.setText("");
        mOptionA.setTextColor(Color.parseColor("#ffffff"));
        mOptionB.setTextColor(Color.parseColor("#ffffff"));
        mOptionC.setTextColor(Color.parseColor("#ffffff"));
        mOptionD.setTextColor(Color.parseColor("#ffffff"));
        if (mPressedFiftyBtn == true) {
            if (mOptionA.isEnabled() == false) mOptionA.setEnabled(true);
            if (mOptionB.isEnabled() == false) mOptionB.setEnabled(true);
            if (mOptionC.isEnabled() == false) mOptionC.setEnabled(true);
            if (mOptionD.isEnabled() == false) mOptionD.setEnabled(true);
        }
    }


    public void disableButtons() {
        mNextBtn.setEnabled(false);
        mFiftyBtn.setEnabled(false);
        mShowAnswerBtn.setEnabled(false);
        mOptionA.setEnabled(false);
        mOptionB.setEnabled(false);
        mOptionC.setEnabled(false);
        mOptionD.setEnabled(false);
    }

    public void enableButtons() {
        mNextBtn.setEnabled(true);
        if (mPressedFiftyBtn == false) {
            mFiftyBtn.setEnabled(true);
        }
        if (mPressedShowAnswerBtn == false) {
            mShowAnswerBtn.setEnabled(true);
        }
        mOptionA.setEnabled(true);
        mOptionB.setEnabled(true);
        mOptionC.setEnabled(true);
        mOptionD.setEnabled(true);
    }

   private void addUser(String username, int userScore, String difficulty) {
        String id = mDatabaseUsers.push().getKey();
        User user = new User(id, username, mSelectedLanguage, Integer.toString(userScore), difficulty);
        mDatabaseUsers.child(id).setValue(user);
    }



}
