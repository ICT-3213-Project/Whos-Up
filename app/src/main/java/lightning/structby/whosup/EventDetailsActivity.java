package lightning.structby.whosup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.DisplayMetrics;

import android.os.Bundle;

import android.util.Log;
import android.view.*;
import android.view.GestureDetector;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;
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
    private Event event;
    private DatabaseReference databaseReference;
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

        // Get our event
        eventId = getIntent().getStringExtra("eventId");
        String eventJson  = getIntent().getStringExtra("event");
        event = (new Gson()).fromJson(eventJson, Event.class);


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("HYHY", userId + "");



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

        responsiveTiles();

        databaseReference = FirebaseDatabase.getInstance().getReference("Events");
        Query query = databaseReference.orderByKey().equalTo(eventId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Event event = ds.getValue(Event.class);
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
                        getEventReference(event);
                    } else {
                        Log.d("HERE", "eeeeeeeeeeeee");
                    }
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
        i.putExtra("userId", userId);
        startActivity(i);
    }

    public void joinEvent(View v){
        TextView tv = (TextView) findViewById(R.id.goingText);
        if(event.getPeopleAttending().contains(userId)){
            event.removePerson(userId);
            tv.setText("Join");
        }
        else{
            tv.setText("Going");
            event.addPerson(userId);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("/" + eventId + "/peopleAttending", event.getPeopleAttending());
        databaseReference.updateChildren(map);


    }
}
