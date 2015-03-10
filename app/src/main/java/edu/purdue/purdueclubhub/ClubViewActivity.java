package edu.purdue.purdueclubhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

/**
 * Created by Tomer on 2/26/2015.
 */
public class ClubViewActivity extends ActionBarActivity {

    //Firebase Reference
    Firebase mFirebaseRef;


    //preferences
    SharedPreferences prefs;
    String UID;

    //intent of calling activity
    Intent calling;

    TextView description, clubName, first_officer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        description = (TextView) findViewById(R.id.textView4);
        clubName = (TextView) findViewById(R.id.textView2);
        first_officer = (TextView) findViewById(R.id.textView6);

        Bundle recdData = getIntent().getExtras();
        description.setText(recdData.getString("description"));
        clubName.setText(recdData.getString("clubName"));
        first_officer.setText(recdData.getString("firstOfficer"));

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        prefs = getPreferences(Context.MODE_PRIVATE);

        AuthData auth = mFirebaseRef.getAuth();
        UID = auth.getUid();

        calling = getIntent();
        final String clubname = calling.getStringExtra("Club");

        findViewById(R.id.newpostbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), NewPostActivity.class);
                i.putExtra("Club", clubname);
                startActivity(i);
            }
        });

        //TODO: Verify that the club exists and find out if used is admin


    }




}
