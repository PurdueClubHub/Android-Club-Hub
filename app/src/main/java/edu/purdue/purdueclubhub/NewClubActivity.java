package edu.purdue.purdueclubhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateClub(View v) {
        String club_name = ((EditText)findViewById(R.id.cc_name_input)).getText().toString();
        String club_desc = ((EditText)findViewById(R.id.cc_club_info)).getText().toString();
        String[] club_admins = new String[5];
        club_admins[0] = UID;

        Map<String, Object> club = new HashMap<String, Object>();
        club.put("description", club_desc);
        club.put("officers", club_admins);

        mFirebaseRef.child("clubs").child(club_name).setValue(club);

        Intent i = new Intent(this, ClubViewActivity.class);
        i.putExtra("clubName", club_name);
        startActivity(i);
        finish();
    }

}
