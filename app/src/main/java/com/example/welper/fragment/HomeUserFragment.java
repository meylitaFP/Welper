package com.example.welper.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.welper.R;
import com.example.welper.activity.AdminActivity;
import com.example.welper.activity.ListProductActivity;
import com.example.welper.activity.ManageDataLogoActivity;

public class HomeUserFragment extends Fragment {

    LinearLayout areaHouse, areaLogo;
    ImageButton buttonHouse, buttonLogo;
    Intent intent;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_user, container, false);

        areaHouse = v.findViewById(R.id.click_area_house);
        areaLogo = v.findViewById(R.id.click_area_logo);
        buttonHouse = v.findViewById(R.id.click_house);
        buttonLogo = v.findViewById(R.id.click_logo);


        areaHouse.setOnClickListener(view -> {
            intent = new Intent(getContext(), ListProductActivity.class);
            intent.putExtra("type", "house");
            getActivity().startActivity(intent);
        });

        buttonHouse.setOnClickListener(view -> {
            intent = new Intent(getContext(), ListProductActivity.class);
            intent.putExtra("type", "house");
            getActivity().startActivity(intent);
        });

        areaLogo.setOnClickListener(view -> {
            intent = new Intent(getContext(), ListProductActivity.class);
            intent.putExtra("type", "logo");
            getActivity().startActivity(intent);
        });

        buttonLogo.setOnClickListener(view -> {
            intent = new Intent(getContext(), ListProductActivity.class);
            intent.putExtra("type", "logo");
            getActivity().startActivity(intent);
        });
        return v;
    }

}
