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

import java.util.ArrayList;
import java.util.List;

public class CardAdapterPosts extends RecyclerView.Adapter<ViewHolderPosts>{

    List<Post> posts;

    public CardAdapterPosts(){
        super();
        posts = new ArrayList<Post>();

        for (int i = 0; i< 24; i++){
            posts.add(new Post("Club "+i,"Message Contents "+i,"User "+i));
        }

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
                intent.putExtra("firstOfficer",user);
                v.getContext().startActivity(intent);
            }
        });

    }
}
