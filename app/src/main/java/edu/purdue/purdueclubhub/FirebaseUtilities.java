package edu.purdue.purdueclubhub;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by PRyan on 3/5/2015.
 */
public class FirebaseUtilities {

   // final String FIREBASE_URL = "https://clubhub.firebaseio.com";

    static String lookupUsername(String uid)
    {
        String FIREBASE_URL = "https://clubhub.firebaseio.com";

        Firebase mFirebaseRef = new Firebase(FIREBASE_URL);
        Firebase userRef = mFirebaseRef.child("users").child(uid);
        final String[] ret = new String[1];

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("username")) {
                    //Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                    //ret[0] = (String) value.get("username");
                }
                else {
                    ret[0] = "Username";
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        return ret[0];
    }

}
