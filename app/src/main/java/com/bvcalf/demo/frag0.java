package com.bvcalf.demo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 李子宣 on 2017/8/9.
 */

public class frag0 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Intent intent = new Intent(getActivity(),search.class);
        startActivity(intent);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
