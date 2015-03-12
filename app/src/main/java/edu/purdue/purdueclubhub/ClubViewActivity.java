package edu.purdue.purdueclubhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

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

    TextView description, clubName, officers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mToolbar.setTitle("View Club");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        description = (TextView) findViewById(R.id.descriptionTextView);
        clubName = (TextView) findViewById(R.id.clubNameTextView);
        officers = (TextView) findViewById(R.id.officersTextView);

        Bundle recdData = getIntent().getExtras();
        description.setText(recdData.getString("description"));
        clubName.setText(recdData.getString("clubName"));
        //first_officer.setText(recdData.getString("firstOfficer"));
        /*String [] array = new String[5];
        String officersFromArray = "";
        officers = recdData.getString("officers");
        officersFromArray = officersFromArray + array[0] + '\n';*/
        //officers.setText(recdData.getString("officers"));
        officers.setText(recdData.getString("firstOfficer"));

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        prefs = getPreferences(Context.MODE_PRIVATE);

        AuthData auth = mFirebaseRef.getAuth();
        UID = auth.getUid();

        //calling = getIntent();
        final String clubname = recdData.getString("clubName");

        findViewById(R.id.newpostbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), NewPostActivity.class);
                i.putExtra("Club", clubname);
                startActivity(i);
            }
        });

        //clubName.setText(clubname);

        //String uid = mFirebaseRef.getAuth().getUid();
        Firebase clubRef = mFirebaseRef.child("clubs").child(clubname);

        clubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("description")) {
                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                    description.setText(value.get("description").toString());
                }
                else {
                    description.setText("Could not find club description");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        //TODO: Verify that the club exists and find out if used is admin


    }




}
