package gmsproduction.com.pushpush.VoiceChat;

        import android.content.Intent;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.util.Log;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.sinch.android.rtc.PushPair;
        import com.sinch.android.rtc.calling.Call;
        import com.sinch.android.rtc.calling.CallEndCause;
        import com.sinch.android.rtc.calling.CallListener;
        import com.squareup.picasso.Picasso;

        import java.util.List;
        import java.util.Map;

        import gmsproduction.com.pushpush.R;

public class IncomingCallScreenActivity extends BaseActivity {

    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    private String mCallLocation;
    private AudioPlayer mAudioPlayer;
    private ImageView hisIMGView;

    private String hisIMG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming);
        hisIMGView = findViewById(R.id.INC_HisIMG);
        Button answer = (Button) findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        Button decline = (Button) findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(VoiceService.CALL_ID);
        //mCallLocation = getIntent().getStringExtra(VoiceService.LOCATION);
    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());


            Log.e("haha","a: " +call.getRemoteUserId());
            String x = call.getRemoteUserId();
            String[] parts = x.split("#");
            String part1 = parts[0];
            String part2 = parts[1];
            Log.e("haha","b: " + part2);

            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);

            remoteUser.setText(part2);


            getIMG(part1);

        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }


    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();
            Intent intent = new Intent(this, CallScreenActivity.class);
            intent.putExtra(VoiceService.CALL_ID, mCallId);
            intent.putExtra("hisImg",hisIMG);
            startActivity(intent);
        } else {
            finish();
        }
    }

    public void getIMG(String userID){

        mFireStore.collection("Users").document(userID).get().addOnCompleteListener(this,new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("image")) {
                                hisIMG = entry.getValue().toString();
                                Log.e("imgs",""+hisIMG);
                                Picasso.with(IncomingCallScreenActivity.this).load(hisIMG).fit().into(hisIMGView);

                            }
                        }
                    }
                }
            }
        });
    }










    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.e("incoming", "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
            Log.e("incoming","finished");
            finish();

        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };
}