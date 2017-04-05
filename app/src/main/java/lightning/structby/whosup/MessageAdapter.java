package lightning.structby.whosup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
interface profilePictureCallback{
    void setProfilePicture (String s, String u);
}


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements profilePictureCallback{

    private List<Message> messages;
    private Context context;
    private String userId;
    private String encodedPicture;
    private String callbackUserId;
    private List<User> users;
    private ReceiverViewHolder rvh;
    private Map<String, Integer> delayedUpdate;

    public MessageAdapter(List<Message> messages, Context context, String userId) {
        this.messages = messages;
        this.context = context;
        this.userId = userId;
        users = new ArrayList<>();
        delayedUpdate = new HashMap<>();
    }

    @Override
    public void setProfilePicture(String s, String userId){
        for(User u : users){
            if(userId.equals(u.getEmail()))
                u.setProfileImage(s);
        }
        int pos = delayedUpdate.get(userId);
        byte[] imgBytes = Base64.decode(s, Base64.NO_WRAP);
        Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        rvh.roundedImageView.setBackground(new BitmapDrawable(context.getResources(), bmp));
        notifyItemChanged(pos);
        encodedPicture = s;

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
            rvh = new ReceiverViewHolder(v);
            return rvh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("Call", "Called here too!");
        Message message = messages.get(position);
        if(holder.getItemViewType() == 1){
            SenderViewHolder svh = (SenderViewHolder) holder;
            svh.textViewMessage.setText(message.getMessage());
        }
        else{


            ReceiverViewHolder rvh = (ReceiverViewHolder) holder;
            rvh.textViewMessage.setText(message.getMessage());
            boolean foundUser = false;
            for (User u: users) {
                Log.d("Call", message.getSenderId() + " " + u.getEmail());
                if(message.getSenderId().equals(u.getEmail())){
                    foundUser = true;
                    Log.d("Call", "reached");
                    byte[] imgBytes = Base64.decode(u.getProfileImage(), Base64.NO_WRAP);
                    Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                    rvh.roundedImageView.setBackground(new BitmapDrawable(context.getResources(), bmp));
                    break;
                }
            }
            if(!foundUser) {
                Log.d("CHECK", message.getSenderId());
                delayedUpdate.put(message.getSenderId(), position);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                Query query = reference.orderByChild("email").equalTo(message.getSenderId()).limitToFirst(1);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            if (user != null) {
                                users.add(user);
                                String image = user.getProfileImage();
//                                setProfilePicture(image, user.getEmail());
                            } else {
                                Log.d("FU", "null returned!");
                            }
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError){

                    }

                });

//                try {
//                    byte[] imgBytes = Base64.decode(encodedPicture, Base64.NO_WRAP);
//                    Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
//                    rvh.roundedImageView.setBackground(new BitmapDrawable(context.getResources(), bmp));
//                    notifyItemChanged(position);
//
//                } catch (Exception e) {
//                    Log.d("ERR", e.toString());
//                }
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
        TextView textViewUserId;
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
