package edu.purdue.purdueclubhub;

/**
 * Created by Cameron on 2/20/2015.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CardAdapterPosts extends RecyclerView.Adapter<ViewHolderPosts>{

    List<Post> posts;
    Firebase clubhub;

    public CardAdapterPosts(){
        super();
        posts = new ArrayList<Post>();
        clubhub = new Firebase("https://clubhub.firebaseio.com");

        clubhub.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                posts.clear();
                //System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase.
                // Toast.makeText(context, snapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                Iterable<DataSnapshot> iterator = snapshot.getChildren();
                //Toast.makeText(context, snapshot.getKey(), Toast.LENGTH_LONG).show();
                for(DataSnapshot ds: iterator){
                    String description = ds.child("description").getValue().toString();
                    String clubName = ds.child("club").getValue().toString();
                    String id = ds.getKey();
                    Object obj = ds.child("username").getValue();
                    String user;
                    if(obj != null) {
                        user = ds.child("username").getValue().toString();
                    }else{
                        user = "No Username Found";
                    }
                    String likes = ds.child("likes").getValue().toString();
                    //Toast.makeText(context, description, Toast.LENGTH_LONG).show();
                    //DataSnapshot officers = ds.child("officers");
                    //String first_officer = officers.child("0").getValue().toString();
                    //Toast.makeText(context, first_officer, Toast.LENGTH_LONG).show();
                    Post post = new Post(clubName, description, user, likes, id);
                    posts.add(post);
                }
                CardAdapterPosts.this.notifyDataSetChanged();
                /*DataSnapshot description = snapshot.child("description");
                Toast.makeText(context, description.getValue().toString(), Toast.LENGTH_LONG).show();
                DataSnapshot officers = snapshot.child("officers");
                Toast.makeText(context, officers.getValue().toString(), Toast.LENGTH_LONG).show();*/

            }
            @Override public void onCancelled(FirebaseError error) { }
        });

        /*for (int i = 0; i< 24; i++){
            posts.add(new Post("Post "+i,"Message Contents "+i,"User "+i));
        }*/

    }

    public void switchPostList(ArrayList<Post> p){
        posts = p;
        CardAdapterPosts.this.notifyDataSetChanged();
    }

    public List<Post> getPosts(){
        return posts;
    }

    @Override
    public ViewHolderPosts onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_posts,parent,false);
        ViewHolderPosts viewHolder = new ViewHolderPosts(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderPosts holder, int position) {
        final Post post = posts.get(position);
        holder.userid.setText(post.username);
        if (!post.username.equals("No Username Found") && post.username.contains("simplelogin")){
            clubhub.child("users").child(post.getUsername()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String trueName = dataSnapshot.getValue().toString();
                    holder.userid.setText(trueName);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
        holder.clubName.setText(post.clubName);
        holder.contents.setText((post.contents));
        holder.scoreText.setText((post.likes));
        holder.postId = post.getId();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}

class ViewHolderPosts extends RecyclerView.ViewHolder{

    public View view;
    public TextView contents;
    public TextView clubName;
    public TextView userid;
    public TextView scoreText;
    public String postId;

    public ViewHolderPosts(View itemView) {
        super(itemView);
        view = itemView;
        scoreText = (TextView) itemView.findViewById(R.id.score);
        contents = (TextView)itemView.findViewById(R.id.contents);
        clubName = (TextView)itemView.findViewById(R.id.club_name);
        userid = (TextView)itemView.findViewById(R.id.username);
        view.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                String content = contents.getText().toString();
                String cName = clubName.getText().toString();
                String user = userid.getText().toString();
                String score = scoreText.getText().toString();
                Intent intent = new Intent(v.getContext(), PostViewActivity.class);
                intent.putExtra("content",content);
                intent.putExtra("clubName",cName);
                intent.putExtra("userid",user);
                intent.putExtra("postid", postId);
                intent.putExtra("score",score);
                v.getContext().startActivity(intent);
            }
        });

    }
}
