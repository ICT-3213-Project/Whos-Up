package lightning.structby.whosup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vinayak on 4/3/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages;
    private Context context;
    private String userId;
    private List<User> users;
    private Map<String, BitmapDrawable> profilePictures;
    private Map<String, Integer> waitForevent;
    private static boolean singleton = false;

    public MessageAdapter(List<Message> messages, Context context, String userId) {
        this.messages = messages;
        this.context = context;
        this.userId = userId;
        users = new ArrayList<>();
        profilePictures = new HashMap<>();
        waitForevent = new HashMap<>();

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
        }
        else{
            ReceiverViewHolder rvh = (ReceiverViewHolder) holder;
            rvh.textViewMessage.setText(message.getMessage());
            BitmapDrawable dp = profilePictures.get(message.getSenderId());
            if(dp != null){
                rvh.roundedImageView.setBackground(dp);
            }
            else if(waitForevent.get(message.getSenderId()) == null){
                waitForevent.put(message.getSenderId(), position);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                Query query = reference.orderByKey().equalTo(message.getSenderId()).limitToFirst(1);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            if (user != null) {
                                Log.d("SEE", "Received");
                                byte[] imgBytes = Base64.decode(user.getProfileImage(), Base64.NO_WRAP);
                                Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                                BitmapDrawable dp = new BitmapDrawable(context.getResources(), bmp);
                                profilePictures.put(ds.getKey(), dp);
                                users.add(user);
                                waitForevent.remove(ds.getKey());
                                notifyItemChanged(position);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position){
        if(messages.get(position).getSenderId().equals(userId))
            return 1;
        return 0;
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView textViewMessage;
//        ImageView roundedImageView;
        RoundedImageView roundedImageView;


        public ReceiverViewHolder(View v){
            super(v);

            textViewMessage = (TextView) v.findViewById(R.id.messageId);
            roundedImageView = (RoundedImageView) v.findViewById(R.id.userProfile);


        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView textViewMessage;

        public SenderViewHolder(View v){
            super(v);
            textViewMessage = (TextView) v.findViewById(R.id.messageId);

        }
    }
}
