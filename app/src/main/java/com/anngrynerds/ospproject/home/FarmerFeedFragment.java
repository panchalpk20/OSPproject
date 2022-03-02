package com.anngrynerds.ospproject.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.anngrynerds.ospproject.R;

public class FarmerFeedFragment extends Fragment {

      public FarmerFeedFragment() {
        // Required empty public constructor
    }

   public static FarmerFeedFragment newInstance() {
       return new FarmerFeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_farmer_feed, container, false);

        //todo implement weather API here

        return view;
    }
}