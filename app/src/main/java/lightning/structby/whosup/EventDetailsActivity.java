package lightning.structby.whosup;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    private Event event;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/proximanova.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        TextView tv=(TextView)findViewById(R.id.timeicon);
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/MaterialIcons-Regular.ttf");
        tv.setTypeface(face);
        tv=(TextView)findViewById(R.id.back);
        tv.setTypeface(face);
        tv=(TextView)findViewById(R.id.bookmark);
        tv.setTypeface(face);

        responsiveTiles();

        userId = "Z10z93OdYjaLFelnh4i98XuhQqB3";
        eventId = "-Kgz6tLEuzAMxu6LB2jQ";

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
