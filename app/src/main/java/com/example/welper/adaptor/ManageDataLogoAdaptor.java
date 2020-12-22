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
import com.example.welper.base.Logo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManageDataLogoAdaptor extends FirestoreRecyclerAdapter<Logo, ManageDataLogoAdaptor.ManageDataLogoHolder> {

    private ManageDataLogoAdaptor.OnItemClickListener listener;
    private ManageDataLogoAdaptor.OnItemLongClickListener longClickListener;
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ManageDataLogoAdaptor(@NonNull FirestoreRecyclerOptions<Logo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ManageDataLogoHolder holder, int position, @NonNull Logo model) {
        holder.itemName.setText(model.getName());
        holder.itemDate.setText(model.getDate());
        holder.itemType.setImageDrawable(ContextCompat.getDrawable(holder.itemName.getContext(), R.drawable.ic_baseline_palette_24));
    }

    @NonNull
    @Override
    public ManageDataLogoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage,parent, false);
        return new ManageDataLogoAdaptor.ManageDataLogoHolder(v);
    }

    class ManageDataLogoHolder extends RecyclerView.ViewHolder{
        TextView itemName, itemDate;
        ImageView itemType;

        public ManageDataLogoHolder(@NonNull View itemView) {
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

    public void setOnItemClickListener(ManageDataLogoAdaptor.OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemLongClickListener(ManageDataLogoAdaptor.OnItemLongClickListener listener){
        this.longClickListener = listener;
    }
}
