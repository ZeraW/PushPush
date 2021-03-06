package gmsproduction.com.pushpush.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import gmsproduction.com.pushpush.Fragments.AllUserFragment;
import gmsproduction.com.pushpush.Fragments.NotificationFragment;
import gmsproduction.com.pushpush.Fragments.ProfleFragment;


public class PagerViewAdapter extends FragmentPagerAdapter {
    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ProfleFragment profleFragment = new ProfleFragment();
                return profleFragment;
            case 1:
                AllUserFragment allUserFragment = new AllUserFragment();
                return allUserFragment;
            case 2:
                NotificationFragment notificationFragment = new NotificationFragment();
                return notificationFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
