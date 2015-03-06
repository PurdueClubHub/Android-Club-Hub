package edu.purdue.purdueclubhub;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Dylan on 2/12/2015.
 */
public class PostViewActivity extends Activity{

    TextView contents, clubName, userid;

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
        userid.setText(recdData.getString("firstOfficer"));

    }


}

