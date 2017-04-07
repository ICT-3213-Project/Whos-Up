package lightning.structby.whosup;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.DisplayMetrics;

import android.os.Bundle;

import android.util.Log;
import android.view.*;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

interface setEvent{
    void getEventReference(Event event);
}

public class EventDetailsActivity extends AppCompatActivity implements setEvent{

    private String eventId;
    private String userId;
    private User userObj;
    private DatabaseReference databaseReference;
    Event event;
    DatabaseReference userReference;
    private DataSnapshot eventSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/proximanova.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get our event
        eventId = getIntent().getStringExtra("eventId");

        // Get catched event
        populateCachedEvent();

        responsiveTiles();

        databaseReference = FirebaseDatabase.getInstance().getReference("Events").child(eventId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);
                    if (event != null) {
                        Log.d("HERE", "ee");
                        TextView tv = (TextView) findViewById(R.id.event_name);
                        tv.setText(event.getEventName());
                        tv = (TextView) findViewById(R.id.location);
                        tv.setText(event.getPlaceName());
                        tv = (TextView) findViewById(R.id.date_time);
                        tv.setText("On " + event.getEventDate() + " at " + event.getEventTime());
                        tv = (TextView) findViewById(R.id.description);
                        tv.setText(event.getPlaceName());
                        tv = (TextView) findViewById(R.id.people_count);
                        String count = ((Integer)event.getPeopleAttendingCount()).toString();
                        tv.setText(count);
                        if(event.getPeopleAttending().containsKey(userId)){
                            tv = (TextView) findViewById(R.id.goingText);
                            tv.setText("Going");
                        }
                        getEventReference(event);


                        populateNewImages();
                    }



                }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


//        userReference = FirebaseDatabase.getInstance().getReference("Users");
//        Query query = userReference.orderByChild("email").equalTo(.getSenderId()).limitToFirst(1);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    User user = ds.getValue(User.class);
//                    if (user != null) {
//                        Log.d("SEE", "Received");
//                        byte[] imgBytes = Base64.decode(user.getProfileImage(), Base64.NO_WRAP);
//                        Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
//                        BitmapDrawable dp = new BitmapDrawable(context.getResources(), bmp);
//                        profilePictures.put(user.getEmail(), dp);
//                        users.add(user);
//                        waitForevent.remove(user.getEmail());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//        });

    }

    private void populateNewImages() {
        if(event.getPeopleAttendingCount() >= 1) {
            final RoundedImageView personAttendingImage = (RoundedImageView) findViewById(R.id.people1);
            List<String> peopleAttendingKeys = new ArrayList<>(event.getPeopleAttending().keySet());
            String uid = event.getPeopleAttending().get(peopleAttendingKeys.get(0));
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null) {
                        String encodedImage = user.getProfileImage();
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        personAttendingImage.setImageBitmap(decodedByte);
                        personAttendingImage.setVisibility(View.VISIBLE);

                        setProfileListeners(R.id.people1, dataSnapshot.getKey());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            RoundedImageView personAttendingImage = (RoundedImageView) findViewById(R.id.people1);
            personAttendingImage.setVisibility(View.INVISIBLE);
        }

        if(event.getPeopleAttendingCount() >= 2) {
            final RoundedImageView personAttendingImage = (RoundedImageView) findViewById(R.id.people2);
            List<String> peopleAttendingKeys = new ArrayList<>(event.getPeopleAttending().keySet());
            String uid = event.getPeopleAttending().get(peopleAttendingKeys.get(1));
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null) {
                        String encodedImage = user.getProfileImage();
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        personAttendingImage.setImageBitmap(decodedByte);
                        personAttendingImage.setVisibility(View.VISIBLE);

                        setProfileListeners(R.id.people2, dataSnapshot.getKey());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            RoundedImageView personAttendingImage = (RoundedImageView) findViewById(R.id.people2);
            personAttendingImage.setVisibility(View.INVISIBLE);

        }

        if(event.getPeopleAttendingCount() >= 3) {
            final RoundedImageView personAttendingImage = (RoundedImageView) findViewById(R.id.people3);
            List<String> peopleAttendingKeys = new ArrayList<>(event.getPeopleAttending().keySet());
            String uid = event.getPeopleAttending().get(peopleAttendingKeys.get(2));
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null) {
                        String encodedImage = user.getProfileImage();
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        personAttendingImage.setImageBitmap(decodedByte);
                        personAttendingImage.setVisibility(View.VISIBLE);

                        setProfileListeners(R.id.people3, dataSnapshot.getKey());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            RoundedImageView personAttendingImage = (RoundedImageView) findViewById(R.id.people3);
            personAttendingImage.setVisibility(View.INVISIBLE);
        }

    }

    private void setProfileListeners(int viewId, final String key) {
        final RoundedImageView personAttendingImage = (RoundedImageView) findViewById(viewId);
        personAttendingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailsActivity.this, ProfileActivity.class);
                i.putExtra("profileId", key);
                startActivity(i);

            }
        });

    }

    private void populateCachedEvent() {
        String eventJson  = getIntent().getStringExtra("event");
        Event event = (new Gson()).fromJson(eventJson, Event.class);
        TextView tv = (TextView) findViewById(R.id.event_name);
        tv.setText(event.getEventName());
        tv = (TextView) findViewById(R.id.location);
        tv.setText(event.getPlaceName());
        tv = (TextView) findViewById(R.id.date_time);
        tv.setText("On " + event.getEventDate() + " at " + event.getEventTime());
        tv = (TextView) findViewById(R.id.description);
        tv.setText(event.getEventDescription());
        tv = (TextView) findViewById(R.id.people_count);
        String count = ((Integer)event.getPeopleAttendingCount()).toString();
        tv.setText(count);

        tv=(TextView)findViewById(R.id.timeicon);
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/MaterialIcons-Regular.ttf");
        tv.setTypeface(face);
        tv=(TextView)findViewById(R.id.back);
        tv.setTypeface(face);
        tv=(TextView)findViewById(R.id.bookmark);
        tv.setTypeface(face);
    }

    @Override
    public void getEventReference(Event event) {
        this.event = event;
    }

    public void back(View v){
        this.finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void responsiveTiles(){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        double width = dpWidth*0.8 - 45;
        width = width*displayMetrics.density +0.5f;
        Log.d("WIDTH", Resources.getSystem().getDisplayMetrics().heightPixels + "");
        RoundedImageView rv = (RoundedImageView) findViewById(R.id.people1);
        rv.getLayoutParams().width = (int)width/3;
        rv.getLayoutParams().height = (int)width/3;
        rv = (RoundedImageView) findViewById(R.id.people2);
        rv.getLayoutParams().width = (int)width/3;
        rv.getLayoutParams().height = (int)width/3;
        rv = (RoundedImageView) findViewById(R.id.people3);
        rv.getLayoutParams().width = (int)width/3;
        rv.getLayoutParams().height = (int)width/3;
        CardView rl = (CardView) findViewById(R.id.peopleCountLayout);
        rl.getLayoutParams().width = (int)width/3;
        rl.getLayoutParams().height = (int)width/3;
        rl = (CardView) findViewById(R.id.userGoingLayout);
        rl.getLayoutParams().width = (int)width/3;
        rl.getLayoutParams().height = (int)width/3;
    }

    public void openChat(View v){
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("eventId", eventId);
        //Toast.makeText(getApplicationContext(),eventId,Toast.LENGTH_SHORT).show();
        i.putExtra("userId", userId);
        i.putExtra("eventName", event.getEventName());
        startActivity(i);
    }

    public void joinEvent(View v){
        TextView tv = (TextView) findViewById(R.id.goingText);
        LinearLayout ll = (LinearLayout) findViewById(R.id.chatLayout);
        if(event.getPeopleAttending().containsKey(userId)){
            event.removePerson(userId);
            tv.setText("Join");
            ll.setVisibility(View.INVISIBLE);
        }
        else{
            tv.setText("Going");
            event.addPerson(userId);
            ll.setVisibility(View.VISIBLE);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("peopleAttending", event.getPeopleAttending());
        databaseReference.updateChildren(map);


    }
}
