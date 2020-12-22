package com.example.welper.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.welper.activity.ManageDataLogoActivity;
import com.example.welper.activity.ManageDataBuildingActivity;
import com.example.welper.R;
import com.example.welper.activity.AdminActivity;

public class HomeAdminFragment extends Fragment {

    LinearLayout houseOption, logoOption;
    ImageView houseIcon, logoIcon;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_admin, container, false);

        houseOption = v.findViewById(R.id.type_house);
        houseIcon = v.findViewById(R.id.type_house_icon);
        logoOption = v.findViewById(R.id.type_logo);
        logoIcon = v.findViewById(R.id.type_logo_icon);

        houseOption.setOnClickListener(view -> {
            ((AdminActivity)getActivity()).intentFragment(ManageDataBuildingActivity.class);
        });
        houseIcon.setOnClickListener(view -> {
            ((AdminActivity)getActivity()).intentFragment(ManageDataBuildingActivity.class);
        });


        logoOption.setOnClickListener(view -> {
            ((AdminActivity)getActivity()).intentFragment(ManageDataLogoActivity.class);
        });
        logoIcon.setOnClickListener(view -> {
            ((AdminActivity)getActivity()).intentFragment(ManageDataLogoActivity.class);
        });

        return v;
    }
}
