package lightning.structby.whosup;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private String eventId;
    private String userId;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Message> messages;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Button tv=(Button) findViewById(R.id.sendButton);
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/MaterialIcons-Regular.ttf");
        tv.setTypeface(face);
        eventId = "69";
        userId = "g@gmail";
        recyclerView = (RecyclerView) findViewById(R.id.message);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messages = new ArrayList<>();

        messages.add(new Message("Hello, there! How are you?", "e@gmail", "3", new Date(), ""));
        messages.add(new Message("How are you?", "f@gmail", "3", new Date(), ""));
        messages.add(new Message("Hello, there?", "g@gmail", "3", new Date(), ""));

        adapter = new MessageAdapter(messages, this, userId);

        recyclerView.setAdapter(adapter);

//        userId = getIntent().getStringExtra("userId");
        databaseReference = FirebaseDatabase.getInstance().getReference("Message");
        databaseReference.orderByChild("eventId").equalTo("69").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message m = dataSnapshot.getValue(Message.class);
                Log.d("JKJK", "lolol" + m.getMessage());
                if(m.getSenderId() != userId) {
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
        });
        Query query = databaseReference.orderByChild("eventId").equalTo("69");

        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                Message newMessage = dataSnapshot.getValue(Message.class);
                Log.d("CHECK", newMessage.getMessage() + "");
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("SEEE", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
//        query.addValueEventListener(messageListener);

    }

    public void sendMessage(View v){
        Date now = new Date();
        Log.d("TIME", now + "");
        et = (EditText) findViewById(R.id.userMessage);
        String message = et.getText().toString();
        if(message.equals(""))
            return;
        Message m = new Message(message, userId, eventId, now, "");
        et.setText("");
        messages.add(m);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size()-1);

        String key = databaseReference.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, m);
        databaseReference.updateChildren(childUpdates);
    }

//    public void loadChatMessages(){
//        adapter = new FirebaseListAdapter<Message>(this, Message.class,
//                R.layout.activity_chat, FirebaseDatabase.getInstance().getReference("")) {
//            @Override
//            protected void populateView(View v, Message model, int position) {
//
//            }
//        }
//    }
}
