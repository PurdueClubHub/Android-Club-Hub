package edu.purdue.purdueclubhub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;


public class PurdueClubHub extends ActionBarActivity {


    final String FIREBASE_URL = "https://clubhub.firebaseio.com";
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_home);

        Firebase.setAndroidContext(this);
        ref = new Firebase(FIREBASE_URL);
        String Uid = getIntent().getExtras().getString("Uid");
        ref.child("Users").child(Uid).child("registered?").setValue(true);

        Button top_posts_button = (Button) findViewById(R.id.topPostsButton);
        top_posts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout topClubs = (RelativeLayout) findViewById(R.id.topClubsRelativeLayout);
                topClubs.setVisibility(View.INVISIBLE);
                RelativeLayout topPosts = (RelativeLayout) findViewById(R.id.topPostsRelativeLayout);
                topPosts.setVisibility(View.VISIBLE);
                Toast.makeText(getBaseContext(), "Now Viewing Top Posts", Toast.LENGTH_LONG).show();
            }
        });

        Button top_clubs_button = (Button) findViewById(R.id.topClubsButton);
        top_clubs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout topClubs = (RelativeLayout) findViewById(R.id.topClubsRelativeLayout);
                topClubs.setVisibility(View.VISIBLE);
                RelativeLayout topPosts = (RelativeLayout) findViewById(R.id.topPostsRelativeLayout);
                topPosts.setVisibility(View.INVISIBLE);
                Toast.makeText(getBaseContext(), "Now Viewing Top Clubs", Toast.LENGTH_LONG).show();
            }
        });

        TextView post_text_view = (TextView)findViewById(R.id.topPostsTextView);
        post_text_view.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               displayPost();
           }
        });
    }

    void displayPost(){
        Intent intent = new Intent(getBaseContext(), PostViewActivity.class);
        startActivity(intent);
        finish();
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
        else if (id == R.id.create_club_menu_item) {
            Bundle bundle = getIntent().getExtras();
            String UID = bundle.getString("Uid");
            if(UID.equals("Guest")) {
                Toast.makeText(this, "Please login to create a club.", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(getBaseContext(), NewClubActivity.class);
                startActivity(intent);
                //finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
