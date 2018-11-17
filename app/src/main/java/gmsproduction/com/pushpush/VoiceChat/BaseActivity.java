package gmsproduction.com.pushpush.VoiceChat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {
     FirebaseFirestore mFireStore;

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
        if(FirebaseAuth.getInstance().getUid()!=null) {
            mFireStore.collection("Users").document(FirebaseAuth.getInstance().getUid()).update("status", "on");
        }
    }

    public void goOfline(){
        if(FirebaseAuth.getInstance().getUid()!=null) {
            mFireStore.collection("Users").document(FirebaseAuth.getInstance().getUid()).update("status", "off");
        }
    }



}