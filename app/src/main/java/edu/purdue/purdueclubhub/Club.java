package edu.purdue.purdueclubhub;

/**
 * Created by Cameron on 2/11/2015.
 */


public class Club {
    String clubName;

    //Short description of the club
    String description;

    //usernames of people who can edit the club
    String[] officers = new String[5];

    int numFollowers;

    public Club(String clubName, String description, String firstOfficer) {
        this.clubName = clubName;
        this.description = description;
        this.officers[0] = new String(firstOfficer);
        this.numFollowers = 0;
    }


}
