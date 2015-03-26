package edu.purdue.purdueclubhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    Button editButton, newPostButton, postsButton, subscribeButton;
    String clubname;
    boolean canUpdate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mToolbar.setTitle("View Club");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       
        //get textviews
        description = (TextView) findViewById(R.id.descriptionTextView);
        clubName = (TextView) findViewById(R.id.clubNameTextView);
        officers = (TextView) findViewById(R.id.officersTextView);

        //get buttons
        //editButton = (Button) findViewById(R.id.editclubButton);
        newPostButton = (Button) findViewById(R.id.newpostbutton);
        postsButton = (Button) findViewById(R.id.postsButton);
        subscribeButton = (Button) findViewById(R.id.subscribeButton);

        final Bundle recdData = getIntent().getExtras();
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
        clubname = recdData.getString("clubName");


        findViewById(R.id.newpostbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bundle bundle = getIntent().getExtras();
                //String UID = bundle.getString("Uid");
                //Toast.makeText(getApplicationContext(), UID, Toast.LENGTH_SHORT).show();
               if(UID.contains("anonymous:-") == true)
                {
                    Toast.makeText(getApplicationContext(), "Please login to create a post.", Toast.LENGTH_SHORT).show();
                }
               else
               {
                    Intent i = new Intent(getBaseContext(), NewPostActivity.class);
                    i.putExtra("Club", clubname);
                    startActivity(i);
               }
            }
        });

        findViewById(R.id.postsButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //Intent i = new Intent(getBaseContext(), HomePageActivity.class);
                //i.putExtra("Club",clubname);
                //startActivity(i);
                Intent i = new Intent(getBaseContext(), PostsFromClub.class);
                SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.prefs_name), MODE_PRIVATE);
                SharedPreferences.Editor prefsEdit = prefs.edit();
                prefs.edit().putString("CLUB_NAME", clubname).apply();
                prefs.edit().putInt("CLUB_FLAG", 1).apply();
                startActivity(i);
                //finish();
            }
        });

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase userRef = mFirebaseRef.child("users").child(mFirebaseRef.getAuth().getUid()).child("clubs");//.child(recdData.getString("clubName"));
                userRef.push().setValue(recdData.getString("clubName"));
                //userRef.push(recdData.getString("clubName"));
                subscribeButton.setEnabled(false);
            }
        });

        isUserOfficer(clubname);
        checkSubscribed(clubname);
    }

    public void checkSubscribed(final String club_name){
        final String uid = mFirebaseRef.getAuth().getUid();
        Firebase clubRef  = mFirebaseRef.child("users").child(mFirebaseRef.getAuth().getUid()).child("clubs");
        clubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iterator = dataSnapshot.getChildren();
                for(DataSnapshot ds: iterator){
                    if (ds.getValue().equals(club_name)){
                        subscribeButton.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void isUserOfficer(String club_name) {
        final String uid = mFirebaseRef.getAuth().getUid();
        Firebase clubRef  = mFirebaseRef.child("clubs").child(club_name);

        clubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //set description
                if(dataSnapshot.hasChild("description")) {
                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                    description.setText(value.get("description").toString());
                }
                else {
                    description.setText("Could not find club description");
                }

                for (DataSnapshot officer : dataSnapshot.child("officers").getChildren()) {
                    if (officer.getValue().toString().equals(uid)) {
                        newPostButton.setVisibility(View.VISIBLE);
                        //editButton.setVisibility(View.VISIBLE);
                        canUpdate = true;
                        break;
                    }
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_club, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.updateinfo:
                if(canUpdate) {
                    Intent i = new Intent(getBaseContext(), UpdateClubActivity.class);
                    i.putExtra("clubName", clubname);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getBaseContext(), "You do not have permission to update this club", Toast.LENGTH_LONG).show();
                }
                break;
        }


        return super.onOptionsItemSelected(item);
    }

}
