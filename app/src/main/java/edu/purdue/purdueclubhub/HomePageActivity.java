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

    private ArrayList<Post> foundPosts;
    private ArrayList<Club> foundClubs;
    private ArrayList<Post> sortedPosts;
    private ArrayList<Club> clubs = new ArrayList<Club>();
    private ArrayList<Post> posts = new ArrayList<Post>();
    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private String m_Text = "";
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    //RecyclerView.Adapter postAdapter;
    CardAdapterPosts postAdapter;
    CardAdapterClubs clubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mToolbar.setTitle("Clubs");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer),mToolbar);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        postAdapter = new CardAdapterPosts();
        clubAdapter = new CardAdapterClubs(getApplicationContext());


        Bundle recdData = getIntent().getExtras();
        String possibleClub = (recdData.getString("Club"));
        if(possibleClub != null)
        {
            mToolbar.setTitle("Posts");
            posts = (ArrayList)postAdapter.getPosts();
            clubs = (ArrayList)clubAdapter.getClubs();
            foundPosts = new ArrayList<Post>();;
            int j = 0;
            for(int i = 0; i < posts.size(); i++){
                Post tempPost = posts.get(i);
                //displayText(posts.get(i).username);
                if(tempPost.contents.toUpperCase().contains(possibleClub.toUpperCase())){
                    //displayText("Found a match at " + i);
                    //posts.remove(i);
                    foundPosts.add(j, tempPost);
                    j++;
                }
            }
            postAdapter.switchPostList(foundPosts);
            mRecyclerView.setAdapter(postAdapter);
        }
        else
        {
            mRecyclerView.setAdapter(clubAdapter);
            posts = (ArrayList)postAdapter.getPosts();
            clubs = (ArrayList)clubAdapter.getClubs();
        }
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
        else if(id == R.id.logout)
        {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.prefs_name), MODE_PRIVATE);
            SharedPreferences.Editor prefsEdit = prefs.edit();
            prefsEdit.clear().commit();
            startActivity(intent);
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
            mRecyclerView.setAdapter(postAdapter);
            //posts = (ArrayList)postAdapter.getPosts();
            postAdapter.switchPostList(posts);
            mToolbar.setTitle("Posts");
        }
        if(position == 1){
            mRecyclerView.setAdapter(clubAdapter);
            //clubs = (ArrayList)clubAdapter.getClubs();
            clubAdapter.switchClubList(clubs);
            mToolbar.setTitle("Clubs");
        }
        if(position == 2){
            //Toast.makeText(this, "Search Clubs Selected", Toast.LENGTH_SHORT).show();
            mRecyclerView.setAdapter(postAdapter);
            mToolbar.setTitle("Posts");
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
                    posts = (ArrayList)postAdapter.getPosts();
                    foundPosts = new ArrayList<Post>();
                    for(int i = 0; i < posts.size(); i++){
                        Post tempPost = posts.get(i);
                        //displayText(posts.get(i).username);
                        if(tempPost.contents.toUpperCase().contains(m_Text.toUpperCase())){
                            //displayText("Found a match at " + i);
                            //posts.remove(i);
                            foundPosts.add(j, tempPost);
                            j++;
                        }
                    }
                    postAdapter.switchPostList(foundPosts);
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
        if(position == 3){
            mRecyclerView.setAdapter(postAdapter);
            posts = (ArrayList)postAdapter.getPosts();
            sortedPosts = new ArrayList<Post>();
            int j;
            for(int i = 0; i < posts.size(); i++){
                j = 0;
                Post tempPost = posts.get(i);
                //displayText(posts.get(i).username);
                for(j = 0; j < sortedPosts.size(); j++){
                    Post tempPost2 = posts.get(j);
                    if(Integer.parseInt(tempPost.likes) <= Integer.parseInt(tempPost2.likes)){
                        continue;
                    }else{
                        break;
                    }
                }
                sortedPosts.add(j, tempPost);
            }
            postAdapter.switchPostList(sortedPosts);
        }
        if(position == 4){
            //Search Clubs
            foundClubs = new ArrayList<Club>();
            mRecyclerView.setAdapter(clubAdapter);
            mToolbar.setTitle("Clubs");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Search Clubs");

            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            /*for(int i = 0; i < clubAdapter.getClubs().size(); i++) {
                Club tempClub = clubAdapter.getClubs().get(i);
                displayText(clubAdapter.getClubs().get(i).clubName + " at " + i);
            }*/

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    foundClubs.clear();
                    m_Text = input.getText().toString();
                    int j = 0;
                    clubs = (ArrayList)clubAdapter.getClubs();
                    //List<Club> foundClubs = new ArrayList<Club>();;
                    for(int i = 0; i < clubs.size(); i++){
                        Club tempClub = clubs.get(i);
                        //displayText(posts.get(i).username);
                        //displayText(clubs.get(i).clubName + " is here");
                        if(tempClub.clubName.toUpperCase().contains(m_Text.toUpperCase())){
                            foundClubs.add(j, tempClub);
                            j++;
                        }
                    }
                    clubAdapter.switchClubList(foundClubs);
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
        if(position == 5){
            /*Intent intent = new Intent(getBaseContext(), NewClubActivity.class);
            intent.putExtra("Uid", "Guest");
            startActivity(intent);
            finish();*/
            Bundle bundle = getIntent().getExtras();
            String UID = bundle.getString("Uid");
            if(UID.equals("Guest")){
                Toast.makeText(this, "Please login to create a club.", Toast.LENGTH_SHORT).show();
            }else {
                //Toast.makeText(this, UID + " clicked 'Create Clubs'", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), NewClubActivity.class);
                intent.putExtra("Uid", UID);
                startActivity(intent);
                //finish();
            }
        }
        if(position == 6){
            //Go to settings page
        }
        if(position == 7){
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.prefs_name), MODE_PRIVATE);
            SharedPreferences.Editor prefsEdit = prefs.edit();
            prefsEdit.clear().commit();
            startActivity(intent);
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
