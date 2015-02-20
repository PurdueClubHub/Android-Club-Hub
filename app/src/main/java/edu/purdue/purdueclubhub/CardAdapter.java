package edu.purdue.purdueclubhub;

/**
 * Created by Cameron on 2/20/2015.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<ViewHolder>{

    List<Post> posts;

    public CardAdapter(){
        super();
        posts = new ArrayList<Post>();

        for (int i = 0; i< 24; i++){
            posts.add(new Post("Club "+i,"User "+i,"Message Contents "+i));
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.clubName.setText(post.clubName);
        holder.userid.setText(post.userid);
        holder.contents.setText((post.contents));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}

class ViewHolder extends RecyclerView.ViewHolder{

    public TextView contents;
    public TextView clubName;
    public TextView userid;

    public ViewHolder(View itemView) {
        super(itemView);
        contents = (TextView)itemView.findViewById(R.id.contents);
        clubName = (TextView)itemView.findViewById(R.id.club_name);
        userid = (TextView)itemView.findViewById(R.id.username);
    }
}
