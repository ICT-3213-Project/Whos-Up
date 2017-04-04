package lightning.structby.whosup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private static final String TAG = "SignUpActivity";

    EditText email;
    EditText password;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);



        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signup(View view){
        email = (EditText)findViewById(R.id.emaileditText);
        password = (EditText)findViewById(R.id.passwordeditText);
        name = (EditText)findViewById(R.id.personNameEditText);
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        createAccount(emailString,passwordString);

    }

    public void createAccount(final String email, String password){

        Log.d(TAG, "createAccount:" + email);
        if (!validate()) {
            return;
        }

        if(!isOnline())
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Connectivity Error.");
            builder.setMessage("Check your internet connection.");
            builder.setPositiveButton("OK", null);
            builder.show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Creating Account");
        progressDialog.show();

        final AlertDialog.Builder builder1 =
                new AlertDialog.Builder(this);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if(task.isSuccessful())
                        {
                            String nameString = name.getText().toString();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                            }
                                        }
                                    });

                            HashMap<String,String> datamap = new HashMap<String, String>();
                            datamap.put("Name",nameString);
                            datamap.put("E-mail",email);

                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("Users").child(user.getUid()).child("Profile").setValue(datamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        //Toast.makeText(context,"Successful",Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        //Toast.makeText(context,"Error",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            progressDialog.dismiss();
                            builder1.setTitle("Congratulations!");
                            builder1.setMessage("Your account has been registered successfully.");
                            builder1.setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            changeActivity();
                                        }
                                    }).create().show();
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //Toast.makeText(SignUpActivity.this, R.string.auth_failed,Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signUpWithEmail:failed", task.getException());
                            progressDialog.dismiss();
                            builder1.setTitle("Error");
                            builder1.setMessage("Check your internet connection.");
                            builder1.setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).create().show();
                            return;
                        }

                        // ...
                    }
                });
    }

    public boolean validate() {
        boolean valid = true;

        email = (EditText)findViewById(R.id.emaileditText);
        password = (EditText)findViewById(R.id.passwordeditText);

        String emailString = email.getText().toString();
        if (TextUtils.isEmpty(emailString) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.setError("Enter a valid email.");
            valid = false;
        } else {
            email.setError(null);
        }

        String passwordString = password.getText().toString();
        if (TextUtils.isEmpty(passwordString) || passwordString.length() < 6 ) {
            password.setError("Enter a strong password.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void changeActivity()
    {
        finish();
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }

}