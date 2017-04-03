package lightning.structby.whosup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private int eventId;
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

        userId = "f@gmail";
        recyclerView = (RecyclerView) findViewById(R.id.message);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messages = new ArrayList<>();

        messages.add(new Message("Hello, there! How are you?", "e@gmail", 3, new Date(), ""));
        messages.add(new Message("How are you?", "f@gmail", 3, new Date(), ""));
        messages.add(new Message("Hello, there?", "g@gmail", 3, new Date(), ""));

        adapter = new MessageAdapter(messages, this, userId);

        recyclerView.setAdapter(adapter);

//        userId = getIntent().getStringExtra("userId");
//        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    public void sendMessage(View v){
        Date now = new Date();
        et = (EditText) findViewById(R.id.userMessage);
        String message = et.getText().toString();
        Message m = new Message(message, userId, eventId, now, "");

    }

}
