package lightning.structby.whosup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vinayak on 4/3/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ValueEventListener {

    private List<Message> messages;
    private Context context;
    private String userId;
    private Map<String, BitmapDrawable> profilePictures;
    private Map<String, List<Integer>> leftList;
    private static boolean singleton = false;

    public MessageAdapter(List<Message> messages, Context context, String userId) {
        this.messages = messages;
        this.context = context;
        this.userId = userId;
        profilePictures = new HashMap<>();
        leftList = new HashMap<>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_send_chat, parent, false);
            return new SenderViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_receive_chat, parent, false);
            return new ReceiverViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Message message = messages.get(position);
        if(holder.getItemViewType() == 1){
            SenderViewHolder svh = (SenderViewHolder) holder;
            svh.textViewMessage.setText(message.getMessage());
//
//            RecyclerView.LayoutManager llm = new RecyclerView.LayoutManager(co);
//
//            View v = llm.findViewByPosition(position); //llm.getChildAt(position);
//            Log.d("rv", v.toString() + "");
//            RecyclerView.LayoutParams rv = (RecyclerView.LayoutParams) v.getLayoutParams();
//            Log.d("rv", rv.toString() + "");
//            rv.setMargins(0,0,0,0);
////            v
////            if(v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
////                Log.d("llll", "jjjjjjjjj");
////            } else {
////                Log.d("llll", "ooooooooooo");
////            }

        }
        else{
            ReceiverViewHolder rvh = (ReceiverViewHolder) holder;
            rvh.textViewMessage.setText(message.getMessage());
            boolean nextMessageHasSameSender = position + 1 < messages.size() &&
                    messages.get(position + 1).getSenderId().equals(message.getSenderId());
            if(nextMessageHasSameSender) {
                rvh.roundedImageView.setBackgroundColor(Color.TRANSPARENT);
                return;
            }

            BitmapDrawable dp = profilePictures.get(message.getSenderId());
            if(dp != null){
                rvh.roundedImageView.setBackground(dp);
            }
            else if(leftList.get(message.getSenderId()) == null){
                leftList.put(message.getSenderId(), new ArrayList<Integer>());
                leftList.get(message.getSenderId()).add(position);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                Query query = reference.orderByKey().equalTo(message.getSenderId()).limitToFirst(1);
                query.addListenerForSingleValueEvent(this);
            }
            else if(leftList.get(message.getSenderId()) != null){
                leftList.get(message.getSenderId()).add(position);
            }
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position){
        return messages.get(position).getSenderId().equals(userId)? 1 : 0;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            User user = ds.getValue(User.class);
            if (user != null) {
                Log.d("SEE", "Received");
                BitmapDrawable dp = getBitmapFromString(user.getProfileImage());
                profilePictures.put(ds.getKey(), dp);
                callbackForUserAdded(ds.getKey());
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private BitmapDrawable getBitmapFromString(String string){
        byte[] imgBytes = Base64.decode(string, Base64.NO_WRAP);
        Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        return new BitmapDrawable(context.getResources(), bmp);
    }

    private void callbackForUserAdded(String userId){
        List<Integer> list = leftList.get(userId);
        for(int i : list){
            notifyItemChanged(i);
        }
    }

}