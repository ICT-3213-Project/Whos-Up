package lightning.structby.whosup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private static final String TAG = "SignUpActivity";
    private final int PICK_IMAGE = 1;
    private ImageView profilePictureImageView;
    private Bitmap imagebitmap;
    private Bitmap scaled;


    EditText email;
    EditText password;
    EditText name;
    EditText description;

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
        email = (EditText)findViewById(R.id.emailEditText);
        password = (EditText)findViewById(R.id.passwordeditText);
        name = (EditText)findViewById(R.id.personNameEditText);
        description = (EditText)findViewById(R.id.descriptioneditText);
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        String descriptionString = description.getText().toString();
        createAccount(emailString,passwordString,descriptionString);

    }

    public  void openSignIn(View v){
        finish();
    }

    public void createAccount(final String email, String password, final String shortBio){

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

                            //Encoding bitmap to a string
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] byteFormat = stream.toByteArray();
                            String encodedImage = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

                            //Decoding string to a bitmap
                            //byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                            //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            //profilePictureImageView.setImageBitmap(decodedByte);

                            User profileDataNode = new User(nameString, email, shortBio, encodedImage);

                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("Users").child(user.getUid()).setValue(profileDataNode).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        email = (EditText)findViewById(R.id.emailEditText);
        password = (EditText)findViewById(R.id.passwordeditText);
        name = (EditText)findViewById(R.id.personNameEditText);
        description = (EditText)findViewById(R.id.descriptioneditText);

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

        String nameString = name.getText().toString();
        if (TextUtils.isEmpty(nameString)){
            name.setError("Enter a valid name.");
            valid = false;
        }else {
            name.setError(null);
        }

        String descriptionString = description.getText().toString();
        if(TextUtils.isEmpty(descriptionString)){
            description.setError("Enter a short bio.");
            valid = false;
        }else {
            description.setError(null);
        }

        if(scaled == null) {
            Toast.makeText(SignUpActivity.this, "Please use a profile image!", Toast.LENGTH_SHORT).show();
            valid = false;
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
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void selectImage(View view){
        Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
        gallIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(gallIntent, "Select Profile Picture"), PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,"Status: " + requestCode + " " + resultCode);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                imagebitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                int scaleSize = (int) ( imagebitmap.getHeight() * (512.0 / imagebitmap.getWidth()) );
                scaled = Bitmap.createScaledBitmap(imagebitmap, 512, scaleSize, true);

                profilePictureImageView = (ImageView) findViewById(R.id.profileImageView);
                profilePictureImageView.setImageBitmap(scaled);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}