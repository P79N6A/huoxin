package com.daimeng.live.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimeng.live.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveTabLookingFragment extends Fragment {


    public LiveTabLookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_tab_looking, container, false);
    }

}
