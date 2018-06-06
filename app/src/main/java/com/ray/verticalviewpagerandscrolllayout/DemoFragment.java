package com.ray.verticalviewpagerandscrolllayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * 测试Fragment
 */
public class DemoFragment extends Fragment {

    public DemoFragment() {
        // Required empty public constructor
    }


    public static DemoFragment newInstance() {
        DemoFragment fragment = new DemoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }

}
