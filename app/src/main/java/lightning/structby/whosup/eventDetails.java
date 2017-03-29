package lightning.structby.whosup;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class eventDetails extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/proximanova.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TextView tv=(TextView)findViewById(R.id.timeicon);
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/MaterialIcons-Regular.ttf");
        tv.setTypeface(face);
        tv=(TextView)findViewById(R.id.back);
        tv.setTypeface(face);
        tv=(TextView)findViewById(R.id.bookmark);
        tv.setTypeface(face);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("E", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("E", "Can't find style. Error: ", e);
        }
        // Position the map's camera near Sydney, Australia.
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));
        map.setMaxZoomPreference(15);
        map.setMinZoomPreference(15);
    }

    public void back(View v){
        this.finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
