package edu.purdue.purdueclubhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class ClubActivity extends ActionBarActivity {
    //Firebase Reference
    Firebase mFirebaseRef;


    //preferences
    SharedPreferences prefs;
    String UID;

    //calling activity's intent
    Intent calling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        prefs = getPreferences(Context.MODE_PRIVATE);


        AuthData auth = mFirebaseRef.getAuth();
        UID = auth.getUid();

        calling = getIntent();
        final String club_name = calling.getStringExtra("Club");

        mFirebaseRef.child("clubs").child(club_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null)
                {
                    ((TextView)findViewById(R.id.activity_club_title)).setText(club_name);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purdue_club_hub, menu);
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
