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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText emailEditText;
    EditText passwordEditText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Intent i = new Intent(this, EventDetailsActivity.class);
//        startActivity(i);
        mAuth = FirebaseAuth.getInstance();


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/sfui.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void login(View view){
        emailEditText = (EditText)findViewById(R.id.email);
        passwordEditText = (EditText)findViewById(R.id.password);
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
                            //Toast.makeText(LoginActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();

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
//        Intent intent = new Intent(this, EventActivity.class);
//        emailEditText = (EditText)findViewById(R.id.emailLogineditText);
//        intent.putExtra("userId", emailEditText.getText().toString());
//        startActivity(intent);
        finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

    }


    public boolean validate() {
        boolean valid = true;

        emailEditText = (EditText)findViewById(R.id.email);
        passwordEditText = (EditText)findViewById(R.id.password);

        String emailString = emailEditText.getText().toString();
        if (TextUtils.isEmpty(emailString) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            emailEditText.setError("Enter a valid email.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String passwordString = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(passwordString) || passwordString.length() < 6 ) {
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
        finish();
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}
