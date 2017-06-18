package annie.com.generalknowledgequiz.controller;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import annie.com.generalknowledgequiz.R;
import annie.com.generalknowledgequiz.adapter.SpinnerAdapter;

public class MainActivity extends AppCompatActivity {

    private String[] mLanguages = {"Select language","English", "Română"};
    private int[] mImages = {R.drawable.arrow,R.drawable.ukicon, R.drawable.roicon};
    private Button mStartButton;
    private Spinner mSpinnerLanguage;
    private String mSelectedLanguage = null, mSelectedDifficulty=null, toast;
    private RadioButton mSelectedRadioButton,mRadioButtonEasy,mRadioButtonMedium,mRadioButtonHard;
    private RadioGroup mRadioGroup;
    private Configuration mConfig;
    private TextView mChooseDifficulty;
    private SpinnerAdapter adapter;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp=MediaPlayer.create(this, R.raw.button);
        mStartButton = (Button) findViewById(R.id.startBtn);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mChooseDifficulty=(TextView) findViewById(R.id.tvDifficulty);
        mRadioButtonEasy=(RadioButton) findViewById(R.id.radioButton1);
        mRadioButtonMedium=(RadioButton) findViewById(R.id.radioButton2);
        mRadioButtonHard=(RadioButton) findViewById(R.id.radioButton3);
        mConfig = new Configuration();
        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        mRadioGroup.setVisibility(View.GONE);
        mChooseDifficulty.setVisibility(View.GONE);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                v.startAnimation(animAlpha);
                if(mSelectedLanguage==null ){

                        Toast.makeText(MainActivity.this, "Select a language please", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mSelectedRadioButton = (RadioButton) findViewById(mRadioGroup.getCheckedRadioButtonId());
                    if (mSelectedRadioButton != null) {
                        mSelectedDifficulty = mSelectedRadioButton.getText().toString();
                        switch(mSelectedLanguage){
                            case "English":
                                break;
                            case "Romanian":

                                if(mSelectedDifficulty.equals("ușor")==true)
                                    mSelectedDifficulty="easy";
                                else
                                    if(mSelectedDifficulty.equals("mediu")==true)
                                        mSelectedDifficulty="medium";
                                else
                                    if(mSelectedDifficulty.equals("greu")==true)
                                        mSelectedDifficulty="hard";
                                break;
                            default:
                                break;
                        }
                        Log.i("selected diff",mSelectedDifficulty);
                        Log.i("selected lang",mSelectedLanguage);
                        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                        intent.putExtra("Language", mSelectedLanguage);
                        intent.putExtra("Difficulty", mSelectedDifficulty);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        mSpinnerLanguage = (Spinner) findViewById(R.id.spinner);

        adapter = new SpinnerAdapter(this, mLanguages, mImages);
        mSpinnerLanguage.setAdapter(adapter);

        mSpinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {


                {
                    switch (arg2) {
                        case 0:
                            mSelectedLanguage=null;
                            break;
                        case 1:
                            mConfig.locale = Locale.ENGLISH;
                            mSelectedLanguage = "English";
                            mChooseDifficulty.setVisibility(View.VISIBLE);
                            mRadioGroup.setVisibility(View.VISIBLE);
                            mChooseDifficulty.setText("Choose difficulty");
                            mRadioButtonEasy.setText("easy");
                            mRadioButtonMedium.setText("medium");
                            mRadioButtonHard.setText("hard");
                            toast="Please select a difficulty";
                            break;
                        case 2:

                            Locale locale = new Locale("ro");
                            Locale.setDefault(locale);
                            mConfig.locale = locale;
                            mSelectedLanguage = "Romanian";
                            mChooseDifficulty.setVisibility(View.VISIBLE);
                            mRadioGroup.setVisibility(View.VISIBLE);
                            toast="Selectați o dificultate";
                            mChooseDifficulty.setText("Alegeți dificultatea");
                            mRadioButtonEasy.setText("ușor");
                            mRadioButtonMedium.setText("mediu");
                            mRadioButtonHard.setText("greu");

                            break;
                        default:
                            mConfig.locale = Locale.ENGLISH;
                            //mSelectedLanguage = "English";
                            break;
                    }
                    getResources().updateConfiguration(mConfig, null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

}
