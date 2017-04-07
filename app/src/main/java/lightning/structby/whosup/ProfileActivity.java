package lightning.structby.whosup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    EditText name, shortBio;
    com.makeramen.roundedimageview.RoundedImageView profileImage;

    FirebaseDatabase database;
    DatabaseReference userRef;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;

    private static final String TAG = "ProfileActivity";
    private final int PICK_IMAGE = 1;
    private Bitmap imagebitmap;
    private Bitmap scaled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String profileId = getIntent().getStringExtra("profileId");

        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(profileId);

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Retrieving");
        progressDialog.show();

        name = (EditText) findViewById(R.id.user_profile_name);
        shortBio = (EditText) findViewById(R.id.user_profile_short_bio);
        profileImage = (com.makeramen.roundedimageview.RoundedImageView) findViewById(R.id.user_profile_image);

        // If not user profile
        if(!profileId.equals(firebaseUser.getUid())) {
            name.setEnabled(false);
            shortBio.setEnabled(false);
            profileImage.setEnabled(false);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    loadProfile(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    loadProfile(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }


        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userRef.child("name").setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        shortBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userRef.child("shortBio").setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadProfile(User user) {
        name.setText(user.getName());
        shortBio.setText(user.getShortBio());
        String encodedImage = user.getProfileImage();
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        profileImage.setImageBitmap(decodedByte);
        progressDialog.dismiss();
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
                profileImage.setImageBitmap(scaled);

                //Encoding bitmap to a string
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteFormat = stream.toByteArray();
                String encodedImage = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
                userRef.child("profileImage").setValue(encodedImage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goBack(View view) {
        this.finish();
    }


}
