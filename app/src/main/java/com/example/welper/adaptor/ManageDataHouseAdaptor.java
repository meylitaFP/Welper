package com.example.welper.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.welper.R;
import com.example.welper.base.House;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManageDataHouseAdaptor extends FirestoreRecyclerAdapter<House,ManageDataHouseAdaptor.ManageDataHouseHolder> {

    private ManageDataHouseAdaptor.OnItemClickListener listener;
    private ManageDataHouseAdaptor.OnItemLongClickListener longClickListener;
    DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ManageDataHouseAdaptor(@NonNull FirestoreRecyclerOptions<House> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ManageDataHouseHolder holder, int position, @NonNull House model) {
        holder.itemName.setText(model.getName());
        holder.itemDate.setText(model.getDate());
        holder.itemType.setImageDrawable(ContextCompat.getDrawable(holder.itemName.getContext(), R.drawable.ic_baseline_house_24));
    }

    @NonNull
    @Override
    public ManageDataHouseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage,parent, false);
        return new ManageDataHouseAdaptor.ManageDataHouseHolder(v);
    }


    class ManageDataHouseHolder extends RecyclerView.ViewHolder{
        TextView itemName, itemDate;
        ImageView itemType;

        public ManageDataHouseHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemType = itemView.findViewById(R.id.item_type);
            itemDate = itemView.findViewById(R.id.item_date);

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

    public void setOnItemClickListener(ManageDataHouseAdaptor.OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemLongClickListener(ManageDataHouseAdaptor.OnItemLongClickListener listener){
        this.longClickListener = listener;
    }
}
