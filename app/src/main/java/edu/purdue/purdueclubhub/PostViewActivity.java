package edu.purdue.purdueclubhub;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by Dylan on 2/12/2015.
 */
public class PostViewActivity extends Activity{

    TextView contents, clubName, userid;
    Firebase clubhub;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_with_comments);

        contents = (TextView) findViewById(R.id.textView_desc);
        clubName = (TextView) findViewById(R.id.textView_Club);
        userid = (TextView) findViewById(R.id.textView_poster);

        Bundle recdData = getIntent().getExtras();
        contents.setText(recdData.getString("content"));
        clubName.setText(recdData.getString("clubName"));
        userid.setText(recdData.getString("userid"));
        String postID = recdData.getString("postid");
        String url = "https://clubhub.firebaseio.com/posts/"+postID+"/likes";
        clubhub = new Firebase(url);

        Button upvote_button = (Button)findViewById(R.id.button_up_vote);
        upvote_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clubhub.runTransaction(new Transaction.Handler(){
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        AuthData auth = clubhub.getAuth();
                        String UID = auth.getUid();
                        if(UID.contains("anonymous:-") == true)
                        {
                            //Toast.makeText(getBaseContext(), "Please login to vote on a post.", Toast.LENGTH_SHORT).show();
                            return Transaction.abort();
                        }
                        if (currentData.getValue() == null) {
                            currentData.setValue("1");
                        } else {
                            int val = Integer.parseInt(currentData.getValue().toString());
                            val++;
                            currentData.setValue((String) String.valueOf(val));
                        }
                        return Transaction.success(currentData);
                    }
                    @Override
                    public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                    }
                });
            }
        });
    }


}

