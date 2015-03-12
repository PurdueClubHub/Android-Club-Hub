package edu.purdue.purdueclubhub;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

/**
 * Created by PJ on 2/26/2015..
 */
public class NewPostActivity extends ActionBarActivity{

    Firebase mFirebaseRef;
    String UID, clubName, username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        AuthData auth = mFirebaseRef.getAuth();
        UID = auth.getUid();
        clubName = getIntent().getStringExtra("Club");
        String uid = mFirebaseRef.getAuth().getUid();

       Firebase userRef = mFirebaseRef.child("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("username")) {
                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                    ((TextView)findViewById(R.id.usernameTextView)).setText(value.get("username").toString());
                }
                else {
                    ((TextView)findViewById(R.id.usernameTextView)).setText("User Not Found");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        ((TextView)findViewById(R.id.usernameTextView)).setText(username);
        ((TextView)findViewById(R.id.Post_Club_Name)).setText(clubName);

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
        String content = ((EditText)findViewById(R.id.postContentEditText)).getText().toString();

        if(content == null || content.equals(""))
            return;

        Post p = new Post(clubName, content, username);
        Firebase postsRef = mFirebaseRef.child("posts");
        postsRef.push().setValue(p.toMap());
        finish();
    }

}

