package edu.purdue.purdueclubhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends ActionBarActivity {


    final String FIREBASE_URL = "https://clubhub.firebaseio.com";
    private Firebase ref;

    private String userID;
    private boolean isRegistrationForm = false;
    int repeatPassEditTextID;
    int displayNameEditTextID;
    String userDisplayName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_login);

        ref = new Firebase(FIREBASE_URL);

        //make form invisible
        setFormVisible(false);

        //attempt log in with saved prefs before enabling form
        attemptLoginFromPrefs();

        //log in with saved prefs failed. Present form
        setFormVisible(true);


        //Set up login button
        Button login_button = (Button) findViewById(R.id.logInButton);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFormEnabled(false);
                attemptLogin();
            }
        });

        //set up registration button
        Button reg_button = (Button)findViewById(R.id.signUpButton);
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRegistrationForm)
                    setFormToRegistration();
                else {
                    registerUser();

                }
            }
        });

        Button guest_button = (Button)findViewById(R.id.guestButton);
        guest_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFormEnabled(false);
                attemptGuestLogin();
            }
        });
    }

    private void attemptGuestLogin() {
        ref.authAnonymously(new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {
                Intent intent = new Intent(getBaseContext(), PurdueClubHub.class);
                intent.putExtra("Uid", "Guest");
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                setFormEnabled(true);
                Toast.makeText(getBaseContext(), "Auth Failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
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
        ref.authWithPassword(email.getText().toString(), pass.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                SharedPreferences prefs = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
                String id = ((EditText) findViewById(R.id.usernameEditText)).getText().toString();
                prefs.edit().putString("USER_ID", id).apply();
                id = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
                prefs.edit().putString("USER_PW",id).apply();

                createUserData(authData, userDisplayName);

                //Toast.makeText(getBaseContext(), "SAVED PREFS", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), PurdueClubHub.class);
                intent.putExtra("Uid", authData.getUid());
                startActivity(intent);
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

                    createUserData(authData, userDisplayName);

                    Intent intent = new Intent(getBaseContext(), PurdueClubHub.class);
                    intent.putExtra("Uid", authData.getUid());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Toast.makeText(getBaseContext(), "Auth Failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                    setFormEnabled(true);
                }
            });

        }
    }

    public void setFormVisible(boolean b) {
        int visibility = b ? View.VISIBLE : View.INVISIBLE;
        int progressVisibility = b ? View.INVISIBLE : View.VISIBLE;

        findViewById(R.id.loginProgressBar).setVisibility(progressVisibility);
        findViewById(R.id.loginFrame).setVisibility(visibility);
        findViewById(R.id.loginButtonsLayout).setVisibility(visibility);
    }

    //TODO: Refactor for our layout
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

    /*
        Function that animates the form from login to registration
     */
    private void setFormToRegistration() {
        LinearLayout loginForm = (LinearLayout)findViewById(R.id.loginFrame);
        ((RelativeLayout.LayoutParams)loginForm.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL, 0);
        loginForm.getLayoutParams().height = 800;

        //Toast.makeText(getBaseContext(), "Hello", Toast.LENGTH_SHORT);

        ((TextView)findViewById(R.id.loginTitle)).setText("Register");


        TextView repeatPassLabel = new TextView(this);
        repeatPassLabel.setText("Repeat Password:");

        loginForm.addView(repeatPassLabel);

        //Set up password EditText
        EditText repeatPassEditText = new EditText(getBaseContext());
        repeatPassEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repeatPassEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        repeatPassEditText.setTextColor(Color.BLACK);
        repeatPassEditText.setLinkTextColor(Color.BLACK);

        repeatPassEditTextID = View.generateViewId();
        repeatPassEditText.setId(repeatPassEditTextID);

        //add it to the view
        loginForm.addView(repeatPassEditText);

        TextView displayName = new TextView(this);
        displayName.setText("Display Name:");

        loginForm.addView(displayName);

        EditText displayNameEditText = new EditText(getBaseContext());
        displayNameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        displayNameEditText.setTextColor(Color.BLACK);

        displayNameEditTextID = View.generateViewId();
        displayNameEditText.setId(displayNameEditTextID);

        loginForm.addView(displayNameEditText);

        findViewById(R.id.guestButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.logInButton).setVisibility(View.INVISIBLE);

        isRegistrationForm = true;
    }

    private void registerUser()
    {
        setFormEnabled(false);
        EditText email = (EditText)findViewById(R.id.usernameEditText);
        EditText pass = (EditText)findViewById(R.id.passwordEditText);
        EditText confirm_pass = (EditText)findViewById(repeatPassEditTextID);

        String password = pass.getText().toString();
        String confirm = confirm_pass.getText().toString();

        if(!password.equals(confirm)) {
            setFormEnabled(true);
            Toast.makeText(getBaseContext(), "Password fields must match!", Toast.LENGTH_LONG).show();
            return;
        }

        ref.createUser(email.getText().toString(), pass.getText().toString(), new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                String pw = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
                String id = ((EditText) findViewById(R.id.usernameEditText)).getText().toString();

                userDisplayName = ((EditText) findViewById(displayNameEditTextID)).getText().toString();

                SharedPreferences prefs = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
                prefs.edit().putString("USER_ID", id).apply();
                prefs.edit().putString("USER_PW", pw).apply();

                //Toast.makeText(getBaseContext(), "AuthData: " + ref.getAuth().getUid(), Toast.LENGTH_LONG).show();
                attemptLoginFromPrefs();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                setFormEnabled(true);
                Toast.makeText(getBaseContext(), "Auth Failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createUserData(final AuthData authData, final String displayName)
    {
        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(authData.getUid()))
                {
                    if(!displayName.equals(""))
                    {
                        ref.child("users").child(authData.getUid()).child("displayName").setValue(userDisplayName);
                    }
                        ref.child("users").child(authData.getUid()).child("groups").setValue("null");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}