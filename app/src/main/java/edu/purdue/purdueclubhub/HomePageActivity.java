package edu.purdue.purdueclubhub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cameron on 2/18/2015.
 */
public class HomePageActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private String m_Text = "";
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    //RecyclerView.Adapter adapter;
    CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer),mToolbar);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new CardAdapter();
        mRecyclerView.setAdapter(adapter);

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
            Intent intent = new Intent(getBaseContext(), NewClubActivity.class);
            startActivity(intent);
            //finish();
        }
        else if(id == R.id.logout)
        {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
           // SharedPreferences.Editor prefsEdit = prefs.edit();
            //prefsEdit.remove("USER_ID").apply();
            //prefsEdit.remove("USER_PW").apply();
            prefs.edit().remove("USER_ID").apply();
            prefs.edit().remove("USER_ID").apply();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayText(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        if(position == 0){
            Toast.makeText(this, "Search Clubs Selected", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Search Posts");

            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    int j = 0;
                    List<Post> posts = adapter.getPosts();
                    List<Post> foundPosts = new ArrayList<Post>();;
                    for(int i = 0; i < posts.size(); i++){
                        Post tempPost = posts.get(i);
                        //displayText(posts.get(i).username);
                        if(tempPost.contents.equalsIgnoreCase(m_Text)){
                            displayText("Found a match at " + i);
                            foundPosts.add(j, tempPost);
                            j++;
                        }
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        if(position == 1){
            /*Intent intent = new Intent(getBaseContext(), NewClubActivity.class);
            intent.putExtra("Uid", "Guest");
            startActivity(intent);
            finish();*/
            Bundle bundle = getIntent().getExtras();
            String UID = bundle.getString("Uid");
            if(UID.equals("Guest")){
                Toast.makeText(this, "Please login to create a club.", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, UID + " clicked 'Create Clubs'", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), NewClubActivity.class);
                intent.putExtra("Uid", UID);
                startActivity(intent);
                //finish();
            }
        }
        if(position == 2){
            //Add transition to setting screen
        }
        if(position == 3){
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
            // SharedPreferences.Editor prefsEdit = prefs.edit();
            //prefsEdit.remove("USER_ID").apply();
            //prefsEdit.remove("USER_PW").apply();
            prefs.edit().remove("USER_ID").apply();
            prefs.edit().remove("USER_ID").apply();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

}
