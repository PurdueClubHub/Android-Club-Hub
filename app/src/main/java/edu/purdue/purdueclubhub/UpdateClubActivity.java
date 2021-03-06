package edu.purdue.purdueclubhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class UpdateClubActivity extends ActionBarActivity {
    //Firebase Reference
    Firebase mFirebaseRef;


    //preferences
    SharedPreferences prefs;
    String UID;
    String club_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_club);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        prefs = getPreferences(Context.MODE_PRIVATE);

        final Bundle recdData = getIntent().getExtras();
        //description.setText(recdData.getString("description"));
        //clubName.setText(recdData.getString("clubName"));

        club_name = recdData.getString("clubName");
        TextView titleView = (TextView)findViewById(R.id.titleTextView);
        titleView.setText("Update Club");

        EditText cnameET = (EditText)(findViewById(R.id.cc_name_input));
        cnameET.setText(club_name);
        cnameET.setEnabled(false);

        ((Button)findViewById(R.id.create_club_submit)).setText("Update Club");

        AuthData auth = mFirebaseRef.getAuth();
        UID = auth.getUid();

        mFirebaseRef.child("clubs").child(club_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    ((EditText)findViewById(R.id.cc_club_info)).setText((String)dataSnapshot.child("description").getValue());
                }
                else {
                    Toast.makeText(getBaseContext(), "Club with that name does not exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

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
                if (dataSnapshot.hasChildren()) {
                    mFirebaseRef.child("clubs").child(club_name).setValue(club);
                    Intent i = new Intent(getBaseContext(), ClubViewActivity.class);
                    i.putExtra("clubName", club_name);
                    startActivity(i);
                    finish();
                }
                else {
                    Toast.makeText(getBaseContext(), "Club with that name does not exist", Toast.LENGTH_LONG).show();
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
