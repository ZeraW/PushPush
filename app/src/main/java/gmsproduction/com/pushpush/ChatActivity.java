package gmsproduction.com.pushpush;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private EditText eT_Message;
    private ImageButton btn_Send;
    private String myID,hisID,hisName,hisImg;
    private FirebaseFirestore mFireStore;
    ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private List<ChatModel> mList;
    private SwipeRefreshLayout mRefreshLayout;
     LinearLayoutManager linearLayoutManager;
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
                if (!TextUtils.isEmpty(message)){
                    chat(message);
                    eT_Message.setText("");
                }
            }
        });

    }
    private void chat(String msg){
        Date currentTime = Calendar.getInstance().getTime();
        Log.e("time","time : " + currentTime);
        String myPerspective = "Chat/"+myID+"/"+hisID;
        String hisPerspective = "Chat/"+hisID+"/"+myID;
        Map<String,Object> chatMap = new HashMap<>();
        chatMap.put("msg",msg);
        chatMap.put("from",myID);
        chatMap.put("time",currentTime);

        mFireStore.collection(myPerspective).add(chatMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                mRecyclerView.smoothScrollToPosition(999999999);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();


            }
        });
        mFireStore.collection(hisPerspective).add(chatMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();


            }
        });

    }
    private void getMsg(){
        mFireStore.collection("Chat/"+myID+"/"+hisID).orderBy("time",Query.Direction.ASCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    if (doc.getType()== DocumentChange.Type.ADDED){
                        String msg = doc.getDocument().getString("msg");
                        String from = doc.getDocument().getString("from");

                        Log.e("msg","msg : "+msg);
                        Log.e("msg","msg : "+from);
                        mList.add(new ChatModel(msg,from));
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(999999999);
                        mRefreshLayout.setRefreshing(false);
                    }

                }
            }
        });
    }
    private void Initi(){
        mFireStore = FirebaseFirestore.getInstance();
        eT_Message = findViewById(R.id.chat_message_view);
        btn_Send = findViewById(R.id.chat_send_btn);
        progressBar= findViewById(R.id.Send_progress);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        //get extra
        hisID = getIntent().getStringExtra("userId");
        hisName = getIntent().getStringExtra("userName");
        hisImg = getIntent().getStringExtra("userImg");
        myID = FirebaseAuth.getInstance().getUid();
        mList = new ArrayList<>();
        mAdapter = new ChatAdapter(ChatActivity.this,mList,myID,hisID,hisName,hisImg);
        mRecyclerView = findViewById(R.id.messages_list);
        mRecyclerView.hasFixedSize();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
