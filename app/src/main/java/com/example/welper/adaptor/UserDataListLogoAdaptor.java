package com.example.welper.adaptor;

import android.content.Context;
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
import com.example.welper.base.House;
import com.example.welper.base.Image;
import com.example.welper.base.Logo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class UserDataListLogoAdaptor extends FirestoreRecyclerAdapter<Logo, UserDataListLogoAdaptor.UserDataListLogoAHolder> {

    Context context;
    private static final String TAG = "Testing";
    private UserDataListLogoAdaptor.OnItemClickListener listener;
    private UserDataListLogoAdaptor.OnItemLongClickListener longClickListener;
    DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
    CollectionReference dbImage = FirebaseFirestore.getInstance().collection("Image");


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserDataListLogoAdaptor(@NonNull FirestoreRecyclerOptions<Logo> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public UserDataListLogoAdaptor.UserDataListLogoAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent, false);
        return new UserDataListLogoAdaptor.UserDataListLogoAHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserDataListLogoAHolder holder, int position, @NonNull Logo model) {
        holder.itemDate.setText(model.getDate());
        holder.itemName.setText(model.getName());
        dbImage.document(model.getPicName()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Image image = documentSnapshot.toObject(Image.class);
                    Glide.with(holder.itemView)
                            .load(image.getLocation().get(0))
                            .into(holder.itemImage);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, e.toString(),Toast.LENGTH_SHORT).show();
                });
    }

    class UserDataListLogoAHolder extends RecyclerView.ViewHolder{
        TextView itemName, itemDate;
        ImageView itemImage;

        public UserDataListLogoAHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.name_product_item);
            itemImage = itemView.findViewById(R.id.image_product_item);
            itemDate = itemView.findViewById(R.id.date_product_item);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION && listener != null){
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });

            itemView.setOnLongClickListener(view -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION && listener != null){
                    longClickListener.onItemLongClick(getSnapshots().getSnapshot(position), position);
                }
                return true;
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(UserDataListLogoAdaptor.OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemLongClickListener(UserDataListLogoAdaptor.OnItemLongClickListener listener){
        this.longClickListener = listener;
    }
}
