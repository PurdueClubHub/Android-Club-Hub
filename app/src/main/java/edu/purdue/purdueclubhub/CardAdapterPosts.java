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
                    //Toast.makeText(context, description, Toast.LENGTH_LONG).show();
                    //DataSnapshot officers = ds.child("officers");
                    //String first_officer = officers.child("0").getValue().toString();
                    //Toast.makeText(context, first_officer, Toast.LENGTH_LONG).show();
                    Post post = new Post("NAME", description,"USER");
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
    public void onBindViewHolder(ViewHolderPosts holder, int position) {
        Post post = posts.get(position);
        holder.clubName.setText(post.clubName);
        holder.userid.setText(post.username);
        holder.contents.setText((post.contents));
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

    public ViewHolderPosts(View itemView) {
        super(itemView);
        view = itemView;
        contents = (TextView)itemView.findViewById(R.id.contents);
        clubName = (TextView)itemView.findViewById(R.id.club_name);
        userid = (TextView)itemView.findViewById(R.id.username);
        view.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                String content = contents.getText().toString();
                String cName = clubName.getText().toString();
                String user = userid.getText().toString();
                Intent intent = new Intent(v.getContext(), PostViewActivity.class);
                intent.putExtra("content",content);
                intent.putExtra("clubName",cName);
                intent.putExtra("userid",user);
                v.getContext().startActivity(intent);
            }
        });

    }
}
