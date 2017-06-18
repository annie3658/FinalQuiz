package annie.com.generalknowledgequiz.controller;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import annie.com.generalknowledgequiz.R;
import annie.com.generalknowledgequiz.adapter.ListviewAdapter;
import annie.com.generalknowledgequiz.model.User;

/**
 * Created by Annie on 12/05/2017.
 */

public class ScoresActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseReference databaseUsers;
    private List<User> usersList, mediumScores, easyScores,hardScores;
    private Button btnStartGame;
    private String toast;
    private boolean testingNetwork;
    private MediaPlayer buttonSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scores_activity);
        buttonSound=MediaPlayer.create(this,R.raw.button);
        Log.i("onCreate()","entered");
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        databaseUsers = database.getReference("usersScores");
        listView = (ListView) findViewById(R.id.scoresListView);
        btnStartGame = (Button) findViewById(R.id.startGameBtn);
        usersList = new ArrayList<>();
        mediumScores =new ArrayList<>();
        easyScores=new ArrayList<>();
        hardScores=new ArrayList<>();

        Bundle b = getIntent().getExtras();
        final String language=b.getString("Language");
        final String difficulty=b.getString("Difficulty");
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        if(language.equals("Romanian")==true)
        {
            toast="Aveți nevoie de conexiune la internet pentru a vizualiza scorurile";
        }
        else {
            toast="You need internet connection to view the scores";
        }

        testingNetwork=isOnline();
        if(testingNetwork==false)
        {
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
        }

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                buttonSound.start();
                Intent intent = new Intent(ScoresActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                usersList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    usersList.add(user);
                }

                for(int i=0;i<usersList.size();i++)
                {
                    if(usersList.get(i).getDifficulty().equals("easy")==true && usersList.get(i).getLanguage().equals(language)==true)
                    {
                        easyScores.add(usersList.get(i));
                    }
                    if(usersList.get(i).getDifficulty().equals("medium")==true && usersList.get(i).getLanguage().equals(language)==true)
                    {
                        mediumScores.add(usersList.get(i));
                    }
                    if(usersList.get(i).getDifficulty().equals("hard")==true && usersList.get(i).getLanguage().equals(language)==true)
                    {
                        hardScores.add(usersList.get(i));
                    }
                }

                if(difficulty.equals("easy")==true) {

                    Collections.sort(easyScores);

                    if(language.equals("Romanian")==true)
                    {
                        translating(easyScores,"ușor");
                    }

                    ListviewAdapter adapter = new ListviewAdapter(ScoresActivity.this, easyScores);
                    listView.setAdapter(adapter);
                }
                else
                if(difficulty.equals("medium")==true)
                {
                    Collections.sort(mediumScores);
                    if(language.equals("Romanian")==true)
                    {
                        translating(mediumScores,"mediu");
                    }

                    ListviewAdapter adapter = new ListviewAdapter(ScoresActivity.this, mediumScores);
                    listView.setAdapter(adapter);
                }
                else
                if(difficulty.equals("hard")==true)
                {
                    Collections.sort(hardScores);
                    if(language.equals("Romanian")==true)
                    {
                        translating(hardScores,"greu");
                    }

                    ListviewAdapter adapter = new ListviewAdapter(ScoresActivity.this, hardScores);
                    listView.setAdapter(adapter);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void translating(List<User> list, String diff)
    {
        for(int i=0;i<list.size();i++)
        {
            list.get(i).setDifficulty(diff);
        }
    }


}
