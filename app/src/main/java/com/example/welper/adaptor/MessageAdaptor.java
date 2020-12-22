package com.example.welper.adaptor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.welper.R;
import com.example.welper.activity.AdminChatActivity;
import com.example.welper.activity.DetailItemActivity;
import com.example.welper.base.Message;
import com.example.welper.base.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<Message> mMessage;

    CollectionReference dbImage = FirebaseFirestore.getInstance().collection("profile");
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public MessageAdaptor(Context context, List<Message> mMessage) {
        this.context = context;
        this.mMessage = mMessage;
    }


    @Override
    public MessageAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT)
            view = LayoutInflater.from(context).inflate(R.layout.my_message, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.their_message, parent, false);

        return new MessageAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdaptor.ViewHolder holder, int position) {
        Message message = mMessage.get(position);
        holder.messageBody.setText(message.getMessage());

        Intent intentSend = new Intent(context, DetailItemActivity.class);

        // function to redirect user to item detail by message @
        holder.itemView.setOnClickListener(view -> {
            String item = holder.messageBody.getText().toString().trim();
            String[] splitValue = item.substring(1).split("/");
            String type = "";
            String id = "";
            if(splitValue.length > 1){
                type = splitValue[0];
                id = splitValue[1];
            }
            char tag = item.charAt(0);
            if (tag == '@') {
                if (!type.isEmpty() || !type.equalsIgnoreCase("")) {
                    if (type.equalsIgnoreCase("house")) {
                        intentSend.putExtra("id", id);
                        intentSend.putExtra("type", "house");
                        intentSend.putExtra("condition", "check");
                    }
                    else{
                        intentSend.putExtra("id", id);
                        intentSend.putExtra("type", "logo");
                        intentSend.putExtra("condition", "check");
                    }
                    context.startActivity(intentSend);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.message_body);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("TAG", "getItemViewType: " + mMessage);
        if (mMessage.get(position).getSender().equalsIgnoreCase(user.getUid()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }
}
