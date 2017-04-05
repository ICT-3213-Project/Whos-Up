package lightning.structby.whosup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DevNavigation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_navigation);
    }

    public void addEvent(View v) {
        Intent i = new Intent(this, EventActivity.class);
        startActivity(i);
    }

    public void listEvent(View v) {

    }
    public void openProfile(View v) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public void mapView(View v) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }
}
