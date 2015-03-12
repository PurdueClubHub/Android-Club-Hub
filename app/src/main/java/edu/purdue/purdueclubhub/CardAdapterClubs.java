package edu.purdue.purdueclubhub;

/**
 * Created by Cameron on 2/20/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CardAdapterClubs extends RecyclerView.Adapter<ViewHolderClubs>{


    Firebase clubhub;
    ArrayList<Club> clubs;
    Context context;
    String first_officer;
    String first_officer_UID;

    public CardAdapterClubs(Context c){
        super();
        clubs = new ArrayList<Club>();
        clubhub = new Firebase("https://clubhub.firebaseio.com");
        context = c;


        //clubhub.child("clubs").addValueEventListener(new ValueEventListener() {
        clubhub.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //clubs.clear();
                Iterable<DataSnapshot> iterator = snapshot.child("clubs").getChildren();
                for(DataSnapshot ds: iterator){
                    String description = ds.child("description").getValue().toString();
                    DataSnapshot officers = ds.child("officers");
                    first_officer_UID = officers.child("0").getValue().toString();

                   Object fo_obj = snapshot.child("users").child(first_officer_UID).child("username").getValue();
                    if(fo_obj!= null) {
                        first_officer = fo_obj.toString();
                    }else{
                        first_officer = "null";
                    }

                    Club club = new Club(ds.getKey(), description, first_officer);
                    if(!Club.alreadyExists(clubs,club)){
                        clubs.add(club);
                    }
                    CardAdapterClubs.this.notifyDataSetChanged();
                }
            }
            @Override public void onCancelled(FirebaseError error) { }
        });

        /*for (int i = 0; i< 24; i++){
            clubs.add(new Club("Club " + i, "Description "+i, "First Officer " + i));
        }*/

    }
    public void getOfficers(final String f){
        clubhub.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot1) {
                first_officer = snapshot1.child(f).toString();
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
        Toast.makeText(context, first_officer, Toast.LENGTH_LONG).show();
    }

    public List<Club> getClubs(){
        return clubs;
    }

    public void switchClubList(ArrayList<Club> c){
        clubs = c;
        CardAdapterClubs.this.notifyDataSetChanged();
    }

    @Override
    public ViewHolderClubs onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_clubs,parent,false);
        ViewHolderClubs viewHolder = new ViewHolderClubs(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderClubs holder, int position) {
        Club club = clubs.get(position);
        holder.clubName.setText(club.clubName);
        holder.description.setText(club.description);
        holder.firstOfficer.setText((club.officers[0]));
    }

    @Override
    public int getItemCount() {
        return clubs.size();
    }
}

class ViewHolderClubs extends RecyclerView.ViewHolder{

    public View view;
    public TextView description;
    public TextView clubName;
    public TextView firstOfficer;

    public ViewHolderClubs(View itemView) {
        super(itemView);
        view = itemView;
        description = (TextView)itemView.findViewById(R.id.description);
        clubName = (TextView)itemView.findViewById(R.id.club_name1);
        firstOfficer = (TextView)itemView.findViewById(R.id.first_officer);
        view.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                String desc = description.getText().toString();
                String cName = clubName.getText().toString();
                String fOfficer = firstOfficer.getText().toString();
                Intent intent = new Intent(v.getContext(), ClubViewActivity.class);
                intent.putExtra("description",desc);
                intent.putExtra("clubName",cName);
                intent.putExtra("firstOfficer",fOfficer);
                v.getContext().startActivity(intent);
            }
        });

    }
}
