package edu.purdue.purdueclubhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class NewClubActivity extends ActionBarActivity {
    //Firebase Reference
    Firebase mFirebaseRef;


    //preferences
    SharedPreferences prefs;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_club);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        prefs = getPreferences(Context.MODE_PRIVATE);


        AuthData auth = mFirebaseRef.getAuth();
        UID = auth.getUid();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    public void onCreateClub(View v) {
        final String club_name = ((EditText)findViewById(R.id.cc_name_input)).getText().toString();
        String club_desc = ((EditText)findViewById(R.id.cc_club_info)).getText().toString();
        String[] club_admins = new String[5];
        club_admins[0] = UID;

        final Map<String, Object> club = new HashMap<String, Object>();
        club.put("description", club_desc);
        club.put("officers", club_admins);

        mFirebaseRef.child("clubs").child(club_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    mFirebaseRef.child("clubs").child(club_name).setValue(club);
                    Intent i = new Intent(getBaseContext(), ClubViewActivity.class);
                    i.putExtra("clubName", club_name);
                    startActivity(i);
                    finish();
                }
                else {
                    Toast.makeText(getBaseContext(), "Club with that name already exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode==KeyEvent.KEYCODE_ENTER)
        {
            // Just ignore the [Enter] key
            return true;
        }
        // Handle all other keys in the default way
        return super.onKeyDown(keyCode, event);
    }

}
