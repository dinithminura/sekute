package com.scorelab.kute.kute.Activity.FragmentUI;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scorelab.kute.kute.R;

/**
 * Created by nrv on 2/7/17.
 */

public class TrackFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Inflate the layout for this fragment

        return inflater.inflate(
                R.layout.trackfragment, container, false);
    }

}
