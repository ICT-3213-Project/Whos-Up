package lightning.structby.whosup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EventActivity extends Activity {

    private static final String TAG = "AddEventActivity";

    String placeName;
    String placeAddress;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    String uid;

    EditText eventNameEditText;
    EditText descriptionEditText;
    EditText dateEditText;
    EditText timeEditText;

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        dateEditText = (EditText)findViewById(R.id.dateeditText);
        timeEditText = (EditText)findViewById(R.id.timeeditText);


        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }


        };

        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EventActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        timeEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeEditText.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//24 hour time
                mTimePicker.show();

            }
        });



        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                placeName = place.getName().toString();
                placeAddress = place.getAddress().toString();
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Address: " + place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        Button button = (Button)findViewById(R.id.addbutton);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!validate()){
                    return;
                }
                eventNameEditText = (EditText)findViewById(R.id.eventNameeditText);
                descriptionEditText = (EditText)findViewById(R.id.descriptioneditText);
                dateEditText = (EditText)findViewById(R.id.dateeditText);
                timeEditText = (EditText)findViewById(R.id.timeeditText);

                String eventNameString = eventNameEditText.getText().toString();
                String descriptionString = descriptionEditText.getText().toString();
                String dateString = dateEditText.getText().toString();
                String timeString = timeEditText.getText().toString();

                List<String> peopleAttending = new ArrayList<>();
                peopleAttending.add(uid);
                peopleAttending.add("1234");

                Event newEvent = new Event(eventNameString, descriptionString, dateString, timeString, placeName, placeAddress, peopleAttending);

                String pushKey = myRef.child("Events").push().getKey();

                for(int i = 0; i < peopleAttending.size(); i++){
                    myRef.child("Events").child(pushKey).child("peopleAttending").push().setValue(peopleAttending.get(i));
                }
                myRef.child("Events").child(pushKey).setValue(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    public boolean validate() {
        boolean valid = true;



        eventNameEditText = (EditText)findViewById(R.id.eventNameeditText);
        descriptionEditText = (EditText)findViewById(R.id.descriptioneditText);
        dateEditText = (EditText)findViewById(R.id.dateeditText);
        timeEditText = (EditText)findViewById(R.id.timeeditText);

        String eventNameString = eventNameEditText.getText().toString();
        String descriptionString = descriptionEditText.getText().toString();
        String dateString = dateEditText.getText().toString();
        String timeString = timeEditText.getText().toString();


        if(TextUtils.isEmpty(eventNameString)){
            eventNameEditText.setError("Enter the event name.");
            valid = false;
        }
        else{
            eventNameEditText.setError(null);
        }

        if(TextUtils.isEmpty(descriptionString)){
            descriptionEditText.setError("Enter the event description.");
            valid = false;
        }
        else{
            descriptionEditText.setError(null);
        }

        if(TextUtils.isEmpty(dateString)){
            dateEditText.setError("Enter the event date.");
            valid = false;
        }
        else{
            dateEditText.setError(null);
        }

        if(TextUtils.isEmpty(timeString)){
            timeEditText.setError("Enter the event time.");
            valid = false;
        }
        else {
            timeEditText.setError(null);
        }


        return valid;
    }

}
