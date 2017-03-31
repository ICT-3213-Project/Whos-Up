package lightning.structby.whosup;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class eventDetails extends AppCompatActivity, GestureDetector.SimpleOnGestureListener {

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
    }


    public void back(View v){
        this.finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void openChat(View v){
        Intent i = new Intent(this, chat.class);
        startActivity(i);
    }
}
