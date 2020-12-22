package com.example.welper.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.welper.R;
import com.example.welper.activity.ManageDataBuildingActivity;
import com.example.welper.activity.ManageDataLogoActivity;
import com.example.welper.adaptor.ManageDataHouseAdaptor;
import com.example.welper.adaptor.ManageDataLogoAdaptor;
import com.example.welper.base.House;
import com.example.welper.base.Logo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class OverviewAdminFragment extends Fragment {

    ImageView searchLayout, houseOpt, logoOpt, allOpt;
    LinearLayout linearLayout, typeBar;
    ImageButton searchBtn;
    EditText searchText;

    private ManageDataLogoAdaptor logoAdaptor;
    private ManageDataHouseAdaptor houseAdaptor;
    private static String typeView = "house";
    View v;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference dbHouse = FirebaseFirestore.getInstance().collection("House");
    CollectionReference dbLogo = FirebaseFirestore.getInstance().collection("Logo");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_list_item, container, false);

        searchLayout = v.findViewById(R.id.overview_search);
        linearLayout = v.findViewById(R.id.linear_search);
        typeBar = v.findViewById(R.id.overview_type_bar);
        searchBtn = v.findViewById(R.id.btn_search);
        houseOpt = v.findViewById(R.id.overview_house);
        logoOpt = v.findViewById(R.id.overview_logo);
        searchText = v.findViewById(R.id.editText_message_search);
        //allOpt = v.findViewById(R.id.overview_all);

        setAllRecycleView("house", "");

        houseOpt.setOnClickListener(view -> {
            try {
                logoAdaptor.stopListening();
                setAllRecycleView("house", "");
                houseAdaptor.startListening();
            } catch (Exception e) {
                setAllRecycleView("house", "");
                houseAdaptor.startListening();
            }
            typeView = "house";
        });

        logoOpt.setOnClickListener(view -> {
            try {
                houseAdaptor.stopListening();
                setAllRecycleView("logo", "");
                logoAdaptor.startListening();
            } catch (Exception e) {
                setAllRecycleView("logo", "");
                logoAdaptor.startListening();
            }
            typeView = "logo";
        });

        searchBtn.setOnClickListener(view -> {
            String searchValue = searchText.getText().toString();
            if (!searchValue.equalsIgnoreCase("") || searchValue != null) {
                try {
                    houseAdaptor.stopListening();
                } catch (Exception e) {
                    logoAdaptor.stopListening();
                }
                setAllRecycleView(typeView, searchValue);
                if (typeView.equalsIgnoreCase("house")) {
                    houseAdaptor.startListening();
                } else {
                    logoAdaptor.startListening();
                }
            }
        });

        searchLayout.setOnClickListener(view -> {
            if (linearLayout.getVisibility() == View.VISIBLE)
                linearLayout.setVisibility(View.GONE);
            else
                linearLayout.setVisibility(View.VISIBLE);
        });
        return v;
    }

    //Setup list All data for management --C-- (before)
    private void setAllRecycleView(String type, String search) {
        Query queryHouse;
        Query queryLogo;
        if (type.equalsIgnoreCase("house")) {
            if(search.equalsIgnoreCase(""))
                queryHouse = FirebaseFirestore.getInstance().collection("House");
            else
                queryHouse = FirebaseFirestore.getInstance()
                        .collection("House")
                        .orderBy("name")
                        .startAt(search)
                        .endAt(search + "\uf8ff");
            FirestoreRecyclerOptions<House> optionsHouse = new FirestoreRecyclerOptions.Builder<House>()
                    .setQuery(queryHouse, House.class)
                    .build();
            houseAdaptor = new ManageDataHouseAdaptor(optionsHouse);
            RecyclerView recyclerView = v.findViewById(R.id.recycler_image_colletion);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(houseAdaptor);

            houseAdaptor.setOnItemLongClickListener((documentSnapshot, position) -> {
                popOutDeleteHouse(documentSnapshot.getId());
            });

            houseAdaptor.setOnItemClickListener((documentSnapshot, position) -> {
                String id = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), ManageDataBuildingActivity.class);
                if (id == null) {
                    dbHouse.document(id).update("houseId", id);
                }
                intent.putExtra("id", id);
                Toast.makeText(getContext(), "Send information to other activity",
                        Toast.LENGTH_LONG).show();
                getActivity().startActivity(intent);
            });

        } else {
            if(search.equalsIgnoreCase(""))
                queryLogo = FirebaseFirestore.getInstance().collection("Logo");
            else
                queryLogo = FirebaseFirestore.getInstance()
                        .collection("Logo")
                        .orderBy("name")
                        .startAt(search)
                        .endAt(search + "\uf8ff");
            FirestoreRecyclerOptions<Logo> optionsLogo = new FirestoreRecyclerOptions.Builder<Logo>()
                    .setQuery(queryLogo, Logo.class)
                    .build();
            logoAdaptor = new ManageDataLogoAdaptor(optionsLogo);
            RecyclerView recyclerView = v.findViewById(R.id.recycler_image_colletion);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(logoAdaptor);

            logoAdaptor.setOnItemLongClickListener((documentSnapshot, position) -> {
                popOutDeleteLogo(documentSnapshot.getId());
            });

            logoAdaptor.setOnItemClickListener((documentSnapshot, position) -> {
                String id = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), ManageDataLogoActivity.class);
                if (id == null) {
                    dbLogo.document(id).update("logoId", id);
                }
                intent.putExtra("id", id);
                Toast.makeText(getContext(), "Send information to other activity",
                        Toast.LENGTH_LONG).show();
                getActivity().startActivity(intent);
            });
        }
    }

    // handle delete Data pop out --C--
    private void popOutDeleteHouse(String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(dialogView);

        final Button buttonCancel = dialogView.findViewById(R.id.alert_cancel);
        final Button buttonDelete = dialogView.findViewById(R.id.alert_delete);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        buttonCancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        buttonDelete.setOnClickListener(view -> {
            dbHouse.document(id).delete();
            Toast.makeText(getContext(), "Success To the Delete Data",
                    Toast.LENGTH_LONG).show();
            alertDialog.dismiss();
        });
    }

    private void popOutDeleteLogo(String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(dialogView);

        final Button buttonCancel = dialogView.findViewById(R.id.alert_cancel);
        final Button buttonDelete = dialogView.findViewById(R.id.alert_delete);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        buttonCancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        buttonDelete.setOnClickListener(view -> {
            dbLogo.document(id).delete();
            Toast.makeText(getContext(), "Success To the Delete Data",
                    Toast.LENGTH_LONG).show();
            alertDialog.dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        houseAdaptor.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            houseAdaptor.stopListening();
        } catch (Exception e) {
            logoAdaptor.stopListening();
        }
    }
}
