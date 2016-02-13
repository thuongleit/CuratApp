package com.avectris.curatapp.view.posted;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avectris.curatapp.R;

/**
 * Created by thuongle on 1/13/16.
 */
public class PostedFragmnet extends Fragment {

    public PostedFragmnet() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(R.layout.fragment_posted, container, false);
        return view;
    }
}
