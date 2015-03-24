package edu.purdue.purdueclubhub;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cameron on 2/18/2015.
 */
public class Post {

    String clubName;
    String contents;
    String username;
    String likes;
    String allTimeLikes;
    String id;

    public Post(){}

    public Post(String cn, String cont, String un, String likes){
        this.clubName = cn;
        this.contents = cont;
        this.username = un;
        this.likes = likes;//"0";
        this.allTimeLikes = likes;
        id = "";
    }

    public Post(String cn, String cont, String un, String likes, String id){
        this.clubName = cn;
        this.contents = cont;
        this.username = un;
        this.likes = likes;//"0";
        this.allTimeLikes = likes;
        this.id = id;
    }

    public String getId() { return id; }

    public String getClubName(){
        return clubName;
    }

    public String getContents(){
        return contents;
    }

    public String getUsername(){
        return username;
    }

    public String getLikes(){
        return likes;
    }

    public String getAllTimeLikes() { return allTimeLikes; }

    public Map<String, String> toMap()
    {
        Map<String, String> r = new HashMap<String, String>();

        r.put("club", clubName);
        r.put("description", contents);
        r.put("username", username);
        r.put("likes", likes);
        r.put("allTimeLikes", allTimeLikes);

        return r;
    }

    public static Comparator<Post> VotesComparator = new Comparator<Post>() {
        @Override
        public int compare(Post lhs, Post rhs) {
            int likes1 = Integer.parseInt(lhs.getLikes());
            int likes2 = Integer.parseInt(rhs.getLikes());
            return likes2-likes1;
        }
    };

    public static Comparator<Post> ClubComparator = new Comparator<Post>() {
        @Override
        public int compare(Post lhs, Post rhs) {
            String club1 = lhs.getClubName().toLowerCase();
            String club2 = rhs.getClubName().toLowerCase();
            return club1.compareTo(club2);
        }
    };

    public static Comparator<Post> TimeComparator = new Comparator<Post>() {
        @Override
        public int compare(Post lhs, Post rhs) {
            String id1 = lhs.getId();
            String id2 = rhs.getId();
            return id2.compareTo(id1);
        }
    };

}

