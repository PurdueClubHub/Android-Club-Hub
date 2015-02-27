package edu.purdue.purdueclubhub;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

/**
 * Created by PJ on 2/26/2015..
 */
public class NewPostActivity extends ActionBarActivity{

    Firebase mFirebaseRef;
    String UID, clubName, username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        AuthData auth = mFirebaseRef.getAuth();
        UID = auth.getUid();
        clubName = "Test Club";
        username = "testuser";

        Button makePostButton = (Button) findViewById(R.id.make_post_button);
        makePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePost();
            }
        });
    }

    private void makePost()
    {
        Post p = new Post(clubName, "test post please ignore", username);
        Firebase postsRef = mFirebaseRef.child("posts");
        postsRef.push().setValue(p.toMap());

    }

}

