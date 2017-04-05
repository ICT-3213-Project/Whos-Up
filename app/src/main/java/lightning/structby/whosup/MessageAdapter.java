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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by vinayak on 4/3/17.
 */
interface profilePictureCallback{
    void setProfilePicture (String s);
}


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements profilePictureCallback{

    private List<Message> messages;
    private Context context;
    private String userId;
    private String encodedPicture;
    public MessageAdapter(List<Message> messages, Context context, String userId) {
        this.messages = messages;
        this.context = context;
        this.userId = userId;
    }

    @Override
    public void setProfilePicture(String s){
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
            return new ReceiverViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(holder.getItemViewType() == 1){
            SenderViewHolder svh = (SenderViewHolder) holder;
            svh.textViewMessage.setText(message.getMessage());



        }
        else{


            ReceiverViewHolder rvh = (ReceiverViewHolder) holder;
            rvh.textViewMessage.setText(message.getMessage());
//            rvh.textViewUserId.setText(message.getSenderId());

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child("User").equalTo(message.getSenderId());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    String image = user.getProfileImage();
                    setProfilePicture(image);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            try {
                byte[] imgBytes = Base64.decode(encodedPicture, Base64.NO_WRAP);
                Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                rvh.roundedImageView.setBackground(new BitmapDrawable(context.getResources(), bmp));
            }catch(Exception e){
                Log.d("ERR", e.toString());
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
