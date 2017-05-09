package lightning.structby.whosup;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by vinayak on 5/9/17.
 */

public class SenderViewHolder extends RecyclerView.ViewHolder{

    TextView textViewMessage;

    public SenderViewHolder(View v){
        super(v);
        textViewMessage = (TextView) v.findViewById(R.id.messageId);

    }
}
