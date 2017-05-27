package lightning.structby.whosup;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by vinayak on 5/9/17.
 */

public class ReceiverViewHolder extends RecyclerView.ViewHolder{

    TextView textViewMessage;
    RoundedImageView roundedImageView;
    View v;


    public ReceiverViewHolder(View v){
        super(v);
        this.v = v;
        textViewMessage = (TextView) v.findViewById(R.id.messageId);
        roundedImageView = (RoundedImageView) v.findViewById(R.id.userProfile);
    }

    public void alterLayoutForPreviousMessage() {
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.receiveMessageLayout);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) ll.getLayoutParams();
        lp.setMargins(lp.leftMargin, 0, lp.rightMargin, lp.bottomMargin);
        ll.setLayoutParams(lp);
        textViewMessage.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.receive_message_multi));
    }

    public void alterLayoutForNextMessage() {
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.receiveMessageLayout);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) ll.getLayoutParams();
        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, 0);
        ll.setLayoutParams(lp);
        roundedImageView.setVisibility(View.INVISIBLE);
    }
}