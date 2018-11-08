package gmsproduction.com.pushpush;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.pusher.pushnotifications.PushNotifications;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mProfile,mUsers,mNotifications;
    private ViewPager mViewPager;
    private PagerViewAdapter mPagerViewAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null){
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changeTabs(int position) {
        switch (position){
            case 0:
                mProfile.setTextColor(getResources().getColor(R.color.textTabLight));
                mProfile.setTextSize(22);

                mUsers.setTextColor(getResources().getColor(R.color.textTabBright));
                mUsers.setTextSize(16);

                mNotifications.setTextColor(getResources().getColor(R.color.textTabBright));
                mNotifications.setTextSize(16);
                break;
            case 1:
                mUsers.setTextColor(getResources().getColor(R.color.textTabLight));
                mUsers.setTextSize(22);

                mProfile.setTextColor(getResources().getColor(R.color.textTabBright));
                mProfile.setTextSize(16);

                mNotifications.setTextColor(getResources().getColor(R.color.textTabBright));
                mNotifications.setTextSize(16);
                break;

            case 2:
                mNotifications.setTextColor(getResources().getColor(R.color.textTabLight));
                mNotifications.setTextSize(22);

                mProfile.setTextColor(getResources().getColor(R.color.textTabBright));
                mProfile.setTextSize(16);

                mUsers.setTextColor(getResources().getColor(R.color.textTabBright));
                mUsers.setTextSize(16);
                break;

        }
    }

    private void Init(){
        mAuth = FirebaseAuth.getInstance();
        mProfile = findViewById(R.id.mProfile);
        mUsers = findViewById(R.id.mUsers);
        mNotifications = findViewById(R.id.mNotifications);
        mViewPager = findViewById(R.id.mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mPagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerViewAdapter);
        mProfile.setOnClickListener(this);
        mUsers.setOnClickListener(this);
        mNotifications.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.mProfile:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.mUsers:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.mNotifications:
                mViewPager.setCurrentItem(2);
                break;
        }

    }


    public void pushNotifications(String interest){
        PushNotifications.start(getApplicationContext(), "54807e2c-4fb6-4eac-910e-0c017fc99617");
        PushNotifications.subscribe(interest);
    }
}
