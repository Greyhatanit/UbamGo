package com.asyantech.ubamgo.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asyantech.ubamgo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteEarnFragment extends Fragment {

    public InviteEarnFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite_earn, container, false);
    }
}
