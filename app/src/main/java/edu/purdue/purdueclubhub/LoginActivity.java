package edu.purdue.purdueclubhub;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class LoginActivity extends ActionBarActivity {


    final String FIREBASE_URL = "https://clubhub.firebaseio.com/";
    private Firebase ref;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_login);

        //TODO: Create function to toggle login form and progress spinner

        //attempt log in with saved prefs before enabling form
        attemptLoginFromPrefs();

        //Set up buttons, etc
        Button login_button = (Button) findViewById(R.id.logInButton);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFormEnabled(false);
                attemptLogin();
            }
        });

        Button reg_button = (Button)findViewById(R.id.signUpButton);
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Start register activity
                //Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                //startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void attemptLogin()
    {
        EditText email = (EditText)findViewById(R.id.usernameEditText);
        EditText pass = (EditText)findViewById(R.id.passwordEditText);
        ref = new Firebase(FIREBASE_URL);
        ref.authWithPassword(email.getText().toString(), pass.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                SharedPreferences prefs = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
                String id = ((EditText) findViewById(R.id.usernameEditText)).getText().toString();
                prefs.edit().putString("USER_ID", id).apply();
                id = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
                prefs.edit().putString("USER_PW",id).apply();

                //Toast.makeText(getBaseContext(), "SAVED PREFS", Toast.LENGTH_LONG).show();
                //TODO: Start new main activity
                //Intent intent = new Intent(getBaseContext(), MainActivity.class);
                //startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                setFormEnabled(true);
                Toast.makeText(getBaseContext(), "Auth Failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void attemptLoginFromPrefs() {
        String userEmail = getSavedEmail();
        String userPW = getSavedPassword();

        if (userEmail.equals("NOT_FOUND") && userPW.equals("NOT_FOUND")) {
            //TODO:show login form
            Toast.makeText(getBaseContext(), "Could not find saved prefs", Toast.LENGTH_LONG).show();
            setFormEnabled(true);
        }

        else {
            //Toast.makeText(getBaseContext(), "Logging in with saved preferences", Toast.LENGTH_LONG).show();
            //if (ref.getAuth() == null) {
            ref.authWithPassword(userEmail, userPW, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    //Toast.makeText(getBaseContext(), "Logged in with user ID: " + userID, Toast.LENGTH_LONG).show();
                    userID = authData.getUid();
                    Toast.makeText(getBaseContext(), "AUTH OKAY", Toast.LENGTH_LONG).show();
                    //TODO:open main activity
                    //caution: need to get user id in main activity
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Toast.makeText(getBaseContext(), "Auth Failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                    //TODO:Show login form
                    setFormEnabled(true);
                }
            });

        }
    }

    public void setFormEnabled(boolean b)
    {
        findViewById(R.id.usernameEditText).setEnabled(b);
        findViewById(R.id.passwordEditText).setEnabled(b);
        findViewById(R.id.logInButton).setEnabled(b);
        findViewById(R.id.signUpButton).setEnabled(b);
    }

    private String getSavedEmail()
    {
        SharedPreferences prefs = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
        return prefs.getString("USER_ID", "NOT_FOUND");
    }

    private String getSavedPassword()
    {
        SharedPreferences prefs = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
        return prefs.getString("USER_PW", "NOT_FOUND");
    }
}