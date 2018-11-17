package gmsproduction.com.pushpush.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import gmsproduction.com.pushpush.Adapter.ChatAdapter;
import gmsproduction.com.pushpush.Models.ChatModel;
import gmsproduction.com.pushpush.R;
import gmsproduction.com.pushpush.VoiceChat.BaseActivity;
import gmsproduction.com.pushpush.VoiceChat.CallScreenActivity;
import gmsproduction.com.pushpush.VoiceChat.PushNotifications.MobileToMobileMSG;
import gmsproduction.com.pushpush.VoiceChat.VoiceService;

public class ChatActivity extends BaseActivity {
    private EditText eT_Message;
    private ImageButton btn_Send;
    private String myID, hisID, hisName, hisImg,hisStatus;
    private FirebaseFirestore mFireStore;
    ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private List<ChatModel> mList;
    private SwipeRefreshLayout mRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    private CircleImageView hisProfileImage;
    private TextView hisnameText,hisStatusText;

    @Override
    protected void onStart() {
        super.onStart();
        mList.clear();
        getMsg();



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Initi();

        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = eT_Message.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    chat(message);
                    eT_Message.setText("");
                }
            }
        });
        if (ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    private void chat(final String msg) {
        Date currentTime = Calendar.getInstance().getTime();
        Log.e("time", "time : " + currentTime);
        String myPerspective = "Chat/" + myID + "/" + hisID;
        String hisPerspective = "Chat/" + hisID + "/" + myID;
        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put("msg", msg);
        chatMap.put("from", myID);
        chatMap.put("time", currentTime);

        mFireStore.collection(myPerspective).add(chatMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                mRecyclerView.smoothScrollToPosition(999999999);
                new MobileToMobileMSG(ChatActivity.this).sendMSG("From : "+getname(),msg,hisID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();


            }
        });
        mFireStore.collection(hisPerspective).add(chatMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();


            }
        });

        getstatus(hisID);

    }

    private void getMsg() {
        mFireStore.collection("Chat/" + myID + "/" + hisID).orderBy("time", Query.Direction.ASCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String msg = doc.getDocument().getString("msg");
                        String from = doc.getDocument().getString("from");

                        Log.e("msg", "msg : " + msg);
                        Log.e("msg", "msg : " + from);
                        mList.add(new ChatModel(msg, from));
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(999999999);
                        mRefreshLayout.setRefreshing(false);

                    }

                }
            }
        });

        getstatus(hisID);
    }

    private void Initi() {
        hisProfileImage = findViewById(R.id.his_imgview);
        hisStatusText=findViewById(R.id.his_status);
        hisnameText = findViewById(R.id.his_nameTxtview);
        mFireStore = FirebaseFirestore.getInstance();
        eT_Message = findViewById(R.id.chat_message_view);
        btn_Send = findViewById(R.id.chat_send_btn);
        progressBar = findViewById(R.id.Send_progress);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                getMsg();
            }
        });


        //get extra
        hisID = getIntent().getStringExtra("userId");
        hisName = getIntent().getStringExtra("userName");
        hisImg = getIntent().getStringExtra("userImg");
        //hisStatus = getIntent().getStringExtra("status");
        Picasso.with(getApplicationContext()).load(hisImg).fit().centerInside().into(hisProfileImage);
        hisnameText.setText(hisName);



        myID = FirebaseAuth.getInstance().getUid();
        mList = new ArrayList<>();
        mAdapter = new ChatAdapter(ChatActivity.this, mList, myID, hisID, hisName, hisImg);
        mRecyclerView = findViewById(R.id.messages_list);
        mRecyclerView.hasFixedSize();


        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        //back
        ImageButton backbtn;
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //call
        ImageView callbtn;
        callbtn = findViewById(R.id.phone_call);
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callButtonClicked(hisID,hisName);
            }
        });

    }

    private void callButtonClicked(String hisids,String hisnames) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }else {
            Call call = getSinchServiceInterface().callUser(hisids+"#"+hisnames);
            String callId = call.getCallId();

            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra(VoiceService.CALL_ID, callId);
            callScreen.putExtra("hiId",hisids);
            callScreen.putExtra("hisImg",hisImg);
            startActivity(callScreen);
        }


    }

    private void getstatus(String userID){
        mFireStore.collection("Users").document(userID).get().addOnCompleteListener(this,new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("status")) {
                                hisStatus = entry.getValue().toString();

                                if (!hisStatus.isEmpty()){
                                    if (hisStatus.equals("on")){
                                        hisStatusText.setText("Online");
                                        hisStatusText.setTextColor(0xAA228B22);
                                    }else if (hisStatus.equals("off")){
                                        hisStatusText.setText("Offline");
                                        hisStatusText.setTextColor(0xAAA9A9A9);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        goOfline();
    }

    private String getname(){
        //how to get shared pref in service
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);
        return prefs.getString("MyName", "No name defined");
    }
}
