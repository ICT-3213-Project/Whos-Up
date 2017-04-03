package lightning.structby.whosup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vinayak on 4/3/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages;
    private Context context;
    private String userId;

    public MessageAdapter(List<Message> messages, Context context, String userId) {
        this.messages = messages;
        this.context = context;
        this.userId = userId;
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
            rvh.textViewUserId.setText(message.getSenderId());
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
        //RoundedImageView roundedImageView;

        public ReceiverViewHolder(View v){
            super(v);

            textViewMessage = (TextView) v.findViewById(R.id.messageId);
            //roundedImageView = (RoundedImageView) v.findViewById(R.id.userProfile);
            textViewUserId = (TextView) v.findViewById(R.id.userProfile);
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
