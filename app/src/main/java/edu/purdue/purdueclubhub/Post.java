package edu.purdue.purdueclubhub;

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

    public Post(){}

    public Post(String cn, String cont, String un){
        this.clubName = cn;
        this.contents = cont;
        this.username = un;
        this.likes = "0";
    }

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

    public Map<String, String> toMap()
    {
        Map<String, String> r = new HashMap<String, String>();

        r.put("club", clubName);
        r.put("description", contents);
        r.put("username", username);
        r.put("likes", likes);

        return r;
    }

}

