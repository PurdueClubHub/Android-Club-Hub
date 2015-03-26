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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Cameron on 2/18/2015.
 */
public class HomePageActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    private ArrayList<Post> foundPosts;
    private ArrayList<Club> foundClubs;
    private ArrayList<Post> sortedPosts;
    private ArrayList<Post> clubPosts;
    private ArrayList<Club> subscribedClubs;
    private ArrayList<Post> subscribedPosts;
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
    Firebase clubhub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.prefs_name), MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        int flag;
        String club_name = "";
        flag = prefs.getInt("CLUB_FLAG", 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mToolbar.setTitle("Clubs");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);



        //Toast.makeText(this, "" + flag, Toast.LENGTH_LONG).show();
        if(flag == 1) {
            club_name = prefs.getString("CLUB_NAME", "");
            mToolbar.setTitle("Posts from Club \"" + club_name + "\"");
        }else{
            mToolbar.setTitle("Posts");
        }

        postAdapter = new CardAdapterPosts(flag, club_name);
        clubAdapter = new CardAdapterClubs(getApplicationContext());
        posts = (ArrayList)postAdapter.getPosts();
        clubs = (ArrayList)clubAdapter.getClubs();

        mRecyclerView.setAdapter(postAdapter);

        prefs.edit().putInt("CLUB_FLAG", 0);
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
        if (id == R.id.sort_posts){
            mRecyclerView.setAdapter(postAdapter);
            mToolbar.setTitle("Sorted Posts by Up-Votes");
            posts = (ArrayList)postAdapter.getPosts();
            sortedPosts = new ArrayList<Post>();
            /*int j;
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
            }*/
            sortedPosts = posts;
            postAdapter.setCurrCompare(Post.VotesComparator);
            Collections.sort(sortedPosts, Post.VotesComparator);
            postAdapter.switchPostList(sortedPosts);
        }
        else if (id == R.id.sort_posts_club) {
            mRecyclerView.setAdapter(postAdapter);
            mToolbar.setTitle("Sorted Posts by Club");
            posts = (ArrayList) postAdapter.getPosts();
            sortedPosts = posts;//new ArrayList<Post>();
            //removed brute force sorting from here
            postAdapter.setCurrCompare(Post.ClubComparator);
            Collections.sort(sortedPosts,Post.ClubComparator);
            postAdapter.switchPostList(sortedPosts);
        }else if (id == R.id.sort_posts_time) {
            mRecyclerView.setAdapter(postAdapter);
            mToolbar.setTitle("Sorted Posts by Time");
            posts = (ArrayList) postAdapter.getPosts();
            sortedPosts = posts;//new ArrayList<Post>();
            //removed brute force sorting from here
            postAdapter.setCurrCompare(Post.TimeComparator);
            Collections.sort(sortedPosts,Post.TimeComparator);
            postAdapter.switchPostList(sortedPosts);
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
            //View posts
            mRecyclerView.setAdapter(postAdapter);
            //posts = (ArrayList)postAdapter.getPosts();
            postAdapter.switchPostList(posts);
            mToolbar.setTitle("Posts");
        }
        if(position == 1){
            //View Clubs
            mRecyclerView.setAdapter(clubAdapter);
            //clubs = (ArrayList)clubAdapter.getClubs();
            clubAdapter.switchClubList(clubs);
            mToolbar.setTitle("Clubs");
        }
        if(position == 2) {
            //View Subscriptions
            mRecyclerView.setAdapter(clubAdapter);
            mToolbar.setTitle("Club Subscriptions");
            subscribedClubs = new ArrayList<Club>();

            clubhub = new Firebase("https://clubhub.firebaseio.com");
            clubhub.child("users").child(clubhub.getAuth().getUid()).child("clubs").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    subscribedClubs.clear();
                    Iterable<DataSnapshot> iterator;
                    if ((iterator = snapshot.getChildren()) != null) {
                        for (DataSnapshot ds : iterator) {
                            for(int i = 0; i < clubs.size(); i++){
                                if(clubs.get(i).clubName.equals(ds.getValue().toString())){
                                    subscribedClubs.add(clubs.get(i));
                                }
                            }
                        }
                        clubAdapter.switchClubList(subscribedClubs);
                    }
                }

                @Override
                public void onCancelled(FirebaseError error) {
                }
            });
        }
        if(position==3){
            //View Subscription Posts
            mRecyclerView.setAdapter(postAdapter);
            mToolbar.setTitle("Subscription Posts");
            subscribedPosts = new ArrayList<Post>();

            clubhub = new Firebase("https://clubhub.firebaseio.com");
            clubhub.child("users").child(clubhub.getAuth().getUid()).child("clubs").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    subscribedPosts.clear();
                    Iterable<DataSnapshot> iterator;
                    if ((iterator = snapshot.getChildren()) != null) {
                        for (DataSnapshot ds : iterator) {
                            for(int i = 0; i < posts.size(); i++){
                                if(posts.get(i).clubName.equals(ds.getValue().toString())){
                                    subscribedPosts.add(posts.get(i));
                                }
                            }
                        }
                        postAdapter.switchPostList(subscribedPosts);
                    }
                }

                @Override
                public void onCancelled(FirebaseError error) {
                }
            });
        }
        if(position==4) {
                   //Search Posts
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
                           posts = (ArrayList) postAdapter.getPosts();
                           foundPosts = new ArrayList<Post>();
                           for (int i = 0; i < posts.size(); i++) {
                               Post tempPost = posts.get(i);
                               //displayText(posts.get(i).username);
                               if (tempPost.contents.toUpperCase().contains(m_Text.toUpperCase())) {
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

               if(position==5)

               {
                   //Search Clubs
                   foundClubs = new ArrayList<Club>();
                   mRecyclerView.setAdapter(clubAdapter);
                   mToolbar.setTitle("Clubs");
                   AlertDialog.Builder builder = new AlertDialog.Builder(this);
                   builder.setTitle("Search Clubs");

                   final EditText input = new EditText(this);

                   input.setInputType(InputType.TYPE_CLASS_TEXT);
                   builder.setView(input);


                   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           foundClubs.clear();
                           m_Text = input.getText().toString();
                           int j = 0;
                           clubs = (ArrayList) clubAdapter.getClubs();
                           //List<Club> foundClubs = new ArrayList<Club>();;
                           for (int i = 0; i < clubs.size(); i++) {
                               Club tempClub = clubs.get(i);
                               //displayText(posts.get(i).username);
                               //displayText(clubs.get(i).clubName + " is here");
                               if (tempClub.clubName.toUpperCase().contains(m_Text.toUpperCase())) {
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

               if(position==6)

               {
                   //Create Club
            /*Intent intent = new Intent(getBaseContext(), NewClubActivity.class);
            intent.putExtra("Uid", "Guest");
            startActivity(intent);
            finish();*/
                   Bundle bundle = getIntent().getExtras();
                   String UID = bundle.getString("Uid");
                   if (UID.equals("Guest")) {
                       Toast.makeText(this, "Please login to create a club.", Toast.LENGTH_SHORT).show();
                   } else {
                       //Toast.makeText(this, UID + " clicked 'Create Clubs'", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(getBaseContext(), NewClubActivity.class);
                       intent.putExtra("Uid", UID);
                       startActivity(intent);
                       //finish();
                   }
               }

               if(position==7)

               {
                   //Logout
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
