package gmsproduction.com.pushpush.VoiceChat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {
    private FirebaseFirestore mFireStore;

    private VoiceService.SinchServiceInterface mSinchServiceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFireStore = FirebaseFirestore.getInstance();
        getApplicationContext().bindService(new Intent(this, VoiceService.class), this,
                BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (VoiceService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (VoiceService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (VoiceService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected VoiceService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        goOfline();
    }

    public void goOnline(){
        mFireStore.collection("Users").document(FirebaseAuth.getInstance().getUid()).update("status","on");
    }

    public void goOfline(){
        mFireStore.collection("Users").document(FirebaseAuth.getInstance().getUid()).update("status","off");
    }

}