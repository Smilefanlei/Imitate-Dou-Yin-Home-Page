package com.ray.verticalviewpagerandscrolllayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private VerticalViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vp = findViewById(R.id.vp);

        vp.setAdapter(new VVPAdapter(getSupportFragmentManager()));

    }


    private class VVPAdapter extends FragmentPagerAdapter {

        public VVPAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DemoFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 10;
        }
    }

}
