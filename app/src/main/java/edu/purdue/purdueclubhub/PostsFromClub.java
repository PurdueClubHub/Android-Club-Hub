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
 * Created by Shade390 on 3/24/2015.
 */
public class PostsFromClub extends ActionBarActivity //implements NavigationDrawerCallbacks
{

    private ArrayList<Post> foundPosts;
    private ArrayList<Post> sortedPosts;
    private ArrayList<Post> clubPosts;
    private ArrayList<Post> posts = new ArrayList<Post>();
    private Toolbar mToolbar;
    //private NavigationDrawerFragment mNavigationDrawerFragment;
    private String m_Text = "";
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapterPosts postAdapter;
    CardAdapterClubs clubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);                //here
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);  //?
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        //mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer),mToolbar);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);


        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.prefs_name), MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        int flag;
        String club_name = "";
        flag = prefs.getInt("CLUB_FLAG", 0);
        prefs.edit().putInt("CLUB_FLAG", 0).apply();
        //Toast.makeText(this, "" + flag, Toast.LENGTH_LONG).show();
        club_name = prefs.getString("CLUB_NAME", "");
        mToolbar.setTitle("Posts from Club \"" + club_name + "\"");

        postAdapter = new CardAdapterPosts(flag, club_name);
        //clubAdapter = new CardAdapterClubs(getApplicationContext());
        posts = (ArrayList)postAdapter.getPosts();
        mRecyclerView.setAdapter(postAdapter);
        sortedPosts = new ArrayList<Post>();
        int j;
        for(int i = 0; i < posts.size(); i++){
            j = 0;
            Post tempPost = posts.get(i);
            //displayText(posts.get(i).username);
            for(j = 0; j < sortedPosts.size(); j++){
                Post tempPost2 = sortedPosts.get(j);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purdue_club_hub, menu); //???
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_posts){
            mRecyclerView.setAdapter(postAdapter);
            posts = (ArrayList)postAdapter.getPosts();
            sortedPosts = new ArrayList<Post>();
            int j;
            for(int i = 0; i < posts.size(); i++){
                j = 0;
                Post tempPost = posts.get(i);
                //displayText(posts.get(i).username);
                for(j = 0; j < sortedPosts.size(); j++){
                    Post tempPost2 = sortedPosts.get(j);
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
        else if (id == R.id.sort_posts_club) {
            mRecyclerView.setAdapter(postAdapter);
            posts = (ArrayList) postAdapter.getPosts();
            sortedPosts = new ArrayList<Post>();
            int j;
            for (int i = 0; i < posts.size(); i++) {
                j = 0;
                Post tempPost = posts.get(i);
                //displayText(posts.get(i).username);
                for (j = 0; j < sortedPosts.size(); j++) {
                    Post tempPost2 = sortedPosts.get(j);
                    if (tempPost.clubName.compareToIgnoreCase(tempPost2.clubName) >= 0) {
                        continue;
                    } else {
                        break;
                    }
                }
                sortedPosts.add(j, tempPost);
            }
            postAdapter.switchPostList(sortedPosts);
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayText(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
