package edu.purdue.purdueclubhub;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.firebase.client.Firebase;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;


public class NewClubActivity extends ActionBarActivity {
    //Firebase Reference
    Firebase mFirebaseRef;

    //used for creating the app, brought in from a different intent
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_club);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(getString(R.string.firebase_url));
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

    public void onCreateClub() {
        String club_name = ((EditText)findViewById(R.id.cc_name_input)).getText().toString();
        String club_desc = ((EditText)findViewById(R.id.cc_club_info)).getText().toString();

        //TODO: Actually assign the current user. Not my name
        Club toCreate = new Club(club_name, club_desc, "Tomer");
        Map<String, Club> club = new HashMap<String, Club>();

        club.put(club_name.replaceAll("\\s+", ""), toCreate);

        Firebase mClubRef = mFirebaseRef.child("/Clubs/");
        mClubRef.setValue(club);

    }

}
