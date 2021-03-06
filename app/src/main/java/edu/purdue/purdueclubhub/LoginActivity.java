package edu.purdue.purdueclubhub;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class LoginActivity extends ActionBarActivity {

    private Firebase ref;
    private Firebase postUpdate;
    private String PREF_NAME;
    private String userID;
    private boolean isRegistrationForm = false;
    int repeatPassEditTextID;
    int usernameEditTextID;
    long postTime;

    LinearLayout oldLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Possibly Remove
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.prefs_name), MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefs.edit().putInt("CLUB_FLAG", 0).apply();
        //^^^^^^^^^^^^^^^^

        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_login);

        ref = new Firebase(getString(R.string.firebase_url));
        PREF_NAME = getResources().getString(R.string.prefs_name);
        Firebase ref2 = new Firebase("https://clubhub.firebaseio.com/lastUpdate/time");
        ref2.runTransaction(new Transaction.Handler(){
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                long prev = Long.parseLong(currentData.getValue().toString());
                Date day = new Date();
                final long now = day.getTime();

                if(now-prev>86400000) {
                    currentData.setValue(now);
                    Firebase clubhub = new Firebase("https://clubhub.firebaseio.com");
                    clubhub.child("posts").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Iterable<DataSnapshot> iterator = snapshot.getChildren();
                            for(DataSnapshot ds: iterator){
                                String postKey = ds.getKey();
                                Firebase ref3 = new Firebase("https://clubhub.firebaseio.com/posts/"+postKey+"/likes");
                                Firebase ref4 = new Firebase("https://clubhub.firebaseio.com/posts/"+postKey);
                                Firebase toGetTime = ref4.child("updated");
                                toGetTime.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData currentData) {
                                        if(currentData.getValue()==null){
                                            currentData.setValue(0);
                                            postTime = 0;
                                        }else{
                                            postTime = Long.parseLong(currentData.getValue().toString());
                                        }
                                        return Transaction.success(currentData);
                                    }
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                                        //This method will be called once with the results of the transaction.
                                    }
                                });
                                if(now-postTime>86400000) {
                                    int likes = Integer.parseInt(ds.child("likes").getValue().toString());
                                    likes = (int) (likes * 0.8);
                                    ref3.setValue((String) String.valueOf(likes));
                                    toGetTime.setValue(now);
                                }
                            }
                        }
                        @Override public void onCancelled(FirebaseError error) { }
                    });
                }

                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                //This method will be called once with the results of the transaction.
            }
        });

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

    @Override
    protected void onStart() {
        super.onStart();


        //make form invisible
        setFormVisible(false);

        //attempt log in with saved prefs before enabling form
        attemptLoginFromPrefs();

        //log in with saved prefs failed. Present form
        setFormVisible(true);
    }

    private void attemptGuestLogin() {
        ref.authAnonymously(new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {
                Intent intent = new Intent(getBaseContext(), HomePageActivity.class);
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


        return super.onOptionsItemSelected(item);
    }

    private void attemptLogin()
    {
        EditText email = (EditText)findViewById(R.id.emailEditText);
        EditText pass = (EditText)findViewById(R.id.passwordEditText);
        ref.authWithPassword(email.getText().toString(), pass.getText().toString().trim(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                String id = ((EditText) findViewById(R.id.emailEditText)).getText().toString().trim();
                prefs.edit().putString("USER_ID", id).apply();
                id = ((EditText) findViewById(R.id.passwordEditText)).getText().toString().trim();
                prefs.edit().putString("USER_PW",id).apply();

                //Toast.makeText(getBaseContext(), "SAVED PREFS", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), HomePageActivity.class);
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
            //String newString = new String("AUTH OKAY: " + userEmail);
          //  Toast.makeText(getBaseContext(), newString, Toast.LENGTH_LONG).show();
            ref.authWithPassword(userEmail, userPW, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    //Toast.makeText(getBaseContext(), "Logged in with user ID: " + userID, Toast.LENGTH_LONG).show();
                    userID = authData.getUid();

                    Toast.makeText(getBaseContext(), "AUTH OKAY", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getBaseContext(), HomePageActivity.class);
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
        findViewById(R.id.emailEditText).setEnabled(b);
        findViewById(R.id.passwordEditText).setEnabled(b);
        findViewById(R.id.logInButton).setEnabled(b);
        findViewById(R.id.signUpButton).setEnabled(b);
    }

    private String getSavedEmail()
    {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        return prefs.getString("USER_ID", "NOT_FOUND");
    }

    private String getSavedPassword()
    {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getString("USER_PW", "NOT_FOUND");
    }

    /*
        Function that animates the form from login to registration
     */

    private void returnToLogin(){
        LinearLayout loginForm = (LinearLayout)findViewById(R.id.loginFrame);
        loginForm.setLayoutParams(oldLayout.getLayoutParams());
    }

    private void setFormToRegistration() {
        LinearLayout loginForm = (LinearLayout)findViewById(R.id.loginFrame);
        oldLayout = loginForm;
        ((RelativeLayout.LayoutParams)loginForm.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL, 0);
        //loginForm.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        loginForm.getLayoutParams().height = point.y - 400;

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
        repeatPassEditText.setEms(10);
        repeatPassEditText.setTextAppearance(this, android.R.style.TextAppearance_Medium);

        repeatPassEditTextID = View.generateViewId();
        repeatPassEditText.setId(repeatPassEditTextID);

        //add it to the view
        loginForm.addView(repeatPassEditText);

        TextView usernameLabel = new TextView(this);
        usernameLabel.setText("Username:");

        loginForm.addView(usernameLabel);

        EditText usernameEditText = new EditText(getBaseContext());
        usernameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        usernameEditText.setTextColor(Color.BLACK);

        usernameEditTextID = View.generateViewId();
        usernameEditText.setId(usernameEditTextID);

        loginForm.addView(usernameEditText);

        findViewById(R.id.guestButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.logInButton).setVisibility(View.INVISIBLE);

        isRegistrationForm = true;
    }

    private void registerUser()
    {
        setFormEnabled(false);

        //EditTexts of email, passwords, and username
        EditText usernameEditText = (EditText) findViewById(usernameEditTextID);
        EditText emailEditText = (EditText)findViewById(R.id.emailEditText);
        final EditText pass = (EditText)findViewById(R.id.passwordEditText);
        EditText confirm_pass = (EditText)findViewById(repeatPassEditTextID);

        final String username = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = pass.getText().toString().trim();
        String confirm = confirm_pass.getText().toString().trim();

        //Compare passwords make sure they match
        if(!password.equals(confirm)) {
            setFormEnabled(true);
            Toast.makeText(getBaseContext(), "Password fields must match!", Toast.LENGTH_LONG).show();
            return;
        }

        //validate that password is strong
        if(!Validation.isValidPassword(password)){
            setFormEnabled(true);
            Toast.makeText(getBaseContext(), "Invalid Password: Must Contain Capitalized and Non-Capitalized letter, a number, and a Special Character", Toast.LENGTH_LONG).show();
            return;
        }
        if(!Validation.isValidUsername(username))
        {
            Toast.makeText(getBaseContext(), "Invalid Username: Must not be specail username, \"guest\" or \"anonymous:-\" and must be composed of letters, numbers, dashes, and underscores. Username should be between 3 and 18 characters long", Toast.LENGTH_LONG).show();
            setFormEnabled(true);
            return;
        }

        if(username.equals("") || (username == null))
        {
            setFormEnabled(true);
            Toast.makeText(getBaseContext(), "Provide a display name", Toast.LENGTH_LONG).show();
            return;
        }

        //Create the user
        ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String,Object>>() {

            @Override
            public void onSuccess(Map<String,Object> result) {
                //Save those values
                SharedPreferences prefs = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
                prefs.edit().putString("USER_ID", email).apply();
                prefs.edit().putString("USER_PW", password).apply();

                String UID = (String)result.get("uid");

                //save user name and email
                Map<String, String> map = new HashMap<String, String>();
                map.put("email", email);
                map.put("username", username);
                ref.child("users").child(UID).setValue(map);

                attemptLoginFromPrefs();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                setFormEnabled(true);
                Toast.makeText(getBaseContext(), "Auth Failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onBackPressed() {
        if(isRegistrationForm){
            Intent i = new Intent(getBaseContext(),LoginActivity.class);
            startActivity(i);
            finish();
        }else{
            finish();
        }
    }

}