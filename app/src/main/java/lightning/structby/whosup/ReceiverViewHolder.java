package lightning.structby.whosup;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by vinayak on 5/9/17.
 */

public class ReceiverViewHolder extends RecyclerView.ViewHolder{

    TextView textViewMessage;
    RoundedImageView roundedImageView;


    public ReceiverViewHolder(View v){
        super(v);

        textViewMessage = (TextView) v.findViewById(R.id.messageId);
        roundedImageView = (RoundedImageView) v.findViewById(R.id.userProfile);


    }
}