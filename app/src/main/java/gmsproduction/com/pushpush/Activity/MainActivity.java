package gmsproduction.com.pushpush.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import gmsproduction.com.pushpush.Adapter.PagerViewAdapter;
import gmsproduction.com.pushpush.R;
import gmsproduction.com.pushpush.VoiceChat.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView mProfile, mUsers, mNotifications;
    private ViewPager mViewPager;
    private PagerViewAdapter mPagerViewAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goOnline();

        //permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

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


        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "win";
                        if (!task.isSuccessful()) {
                            msg = "lose";
                        }
                        Log.d("ddd", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


    }



    private void changeTabs(int position) {
        switch (position) {
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

    private void Init() {
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
        switch (id) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        goOfline();
        Toast.makeText(this, "destroy", Toast.LENGTH_SHORT).show();
    }


}
