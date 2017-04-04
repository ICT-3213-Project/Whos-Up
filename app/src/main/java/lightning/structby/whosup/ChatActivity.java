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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String eventId;
    private String userId;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Message> messages;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Button tv = (Button) findViewById(R.id.sendButton);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/MaterialIcons-Regular.ttf");
        tv.setTypeface(face);
        eventId = "69";
        userId = "g@gmail";
        recyclerView = (RecyclerView) findViewById(R.id.message);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages, this, userId);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference("Message");
        databaseReference.orderByChild("eventId").equalTo("69").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message m = dataSnapshot.getValue(Message.class);
                Log.d("JKJK", m.getMessage());
                if (m.getSenderId() != userId) {
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

    }

    public void sendMessage(View v) {
        Date now = new Date();
        Log.d("TIME", now + "");
        et = (EditText) findViewById(R.id.userMessage);
        String message = et.getText().toString();
        if (message.equals(""))
            return;
        Message m = new Message(message, userId, eventId, now, "");
        et.setText("");
        messages.add(m);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);

        String key = databaseReference.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, m);
        databaseReference.updateChildren(childUpdates);
    }
}