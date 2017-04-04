package lightning.structby.whosup;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EventDetailsActivity extends AppCompatActivity {

    private String eventId;
    private String userId;

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
        eventId = "-KgsrBfBoW5N72qqwcFG";
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
        tv.setText("Going");


    }
}
