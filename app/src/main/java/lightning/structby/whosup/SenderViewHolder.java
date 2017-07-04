package lightning.structby.whosup;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by vinayak on 5/9/17.
 */

public class SenderViewHolder extends RecyclerView.ViewHolder{

    TextView textViewMessage;
    TextView time;
    LinearLayout messageContainer;
    View v;

    public SenderViewHolder(View v){
        super(v);
        this.v = v;
        textViewMessage = (TextView) v.findViewById(R.id.messageId);
        time = (TextView) v.findViewById(R.id.time);
        messageContainer = (LinearLayout) v.findViewById(R.id.messageContainer);
    }

    public void alterLayoutForPreviousMessage() {
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.sendMessageLayout);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) ll.getLayoutParams();
        lp.setMargins(lp.leftMargin, 1, lp.rightMargin, lp.bottomMargin);
        ll.setLayoutParams(lp);
        messageContainer.setBackground(ContextCompat.getDrawable(v.getContext(),
                R.drawable.send_message_multi));
    }

    public void alterLayoutForNextMessage() {
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.sendMessageLayout);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) ll.getLayoutParams();
        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, 1);
        ll.setLayoutParams(lp);
    }
}
