package lightning.structby.whosup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChatActivity extends AppCompatActivity implements ChildEventListener {

    private String eventId;
    private String userId;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setUpFonts();
        getIntentData();

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages, this, userId);

        setRecyclerView();

        databaseReference = FirebaseDatabase.getInstance().getReference("Message");
        Query query=databaseReference.orderByChild("eventId").equalTo(eventId);
        query.addChildEventListener(this);

    }


    private void getIntentData(){
        userId = getIntent().getStringExtra("userId");
        eventId = getIntent().getStringExtra("eventId");
        String eventName = getIntent().getStringExtra("eventName");
        getSupportActionBar().setTitle(eventName);
    }

    private void setRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.message);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setUpFonts(){
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/sfui.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Button button = (Button) findViewById(R.id.sendButton);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/MaterialIcons-Regular.ttf");
        button.setTypeface(face);
    }

    public void sendMessage(View v) {
        Date now = new Date();
        String date = new SimpleDateFormat("HH:mm").format(now);
        String time = new SimpleDateFormat("MMM dd, yyyy").format(now);

        EditText editText = (EditText) findViewById(R.id.userMessage);
        String messageText = editText.getText().toString().trim();

        if (messageText.equals(""))
            return;

        Message message = new Message(messageText, userId, eventId, date, time, "");
        editText.setText("");
        messages.add(message);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);

        String key = databaseReference.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, message);
        databaseReference.updateChildren(childUpdates);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Message m = dataSnapshot.getValue(Message.class);
        String senderId = m.getSenderId();
        if (senderId != userId) {
            messages.add(m);
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerView.scrollToPosition(messages.size() - 1);


        }


    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}