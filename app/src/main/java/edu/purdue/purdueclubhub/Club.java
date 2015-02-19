package edu.purdue.purdueclubhub;

/**
 * Created by Cameron on 2/11/2015.
 */


public class Club {
    String clubName;

    //Short description of the club
    String description;

    int numFollowers;

    public Club(String clubName, String description) {
        this.clubName = clubName;
        this.description = description;
        this.numFollowers = 0;
    }


}
