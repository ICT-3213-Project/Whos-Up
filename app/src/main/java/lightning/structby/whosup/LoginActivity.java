package lightning.structby.whosup;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "LoginActivity";
    EditText emailEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

    public void login(View view){
        emailEditText = (EditText)findViewById(R.id.emailLogineditText);
        passwordEditText = (EditText)findViewById(R.id.passwordLogineditText);
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        signIn(email,password);
    }

    public void signIn(String email, String password){

        Log.d(TAG, "signIn:" + email);
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

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Authenticating");
        progressDialog.show();

        final AlertDialog.Builder builder1 =
                new AlertDialog.Builder(this);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if(task.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            changeActivity();
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                            builder1.setTitle("Error");
                            builder1.setMessage("Incorrect email id/password.");
                            builder1.setPositiveButton("OK", null);
                            builder1.show();
                            return;
                        }

                        // ...
                    }
                });
    }

    private void changeActivity()
    {
        finish();
        Intent intent = new Intent(this, EventActivity.class);
        emailEditText = (EditText)findViewById(R.id.emailLogineditText);
        intent.putExtra("userId", emailEditText.getText().toString());
        startActivity(intent);
    }


    public boolean validate() {
        boolean valid = true;

        emailEditText = (EditText)findViewById(R.id.emailLogineditText);
        passwordEditText = (EditText)findViewById(R.id.passwordLogineditText);

        String emailString = emailEditText.getText().toString();
        if (TextUtils.isEmpty(emailString) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            emailEditText.setError("Enter a valid email.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String passwordString = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(passwordString) || passwordString.length() < 4 ) {
            passwordEditText.setError("Enter a strong password.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void openSignUp(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}
