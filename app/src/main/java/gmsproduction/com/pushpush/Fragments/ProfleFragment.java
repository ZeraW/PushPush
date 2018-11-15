package gmsproduction.com.pushpush.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import gmsproduction.com.pushpush.Activity.LoginActivity;
import gmsproduction.com.pushpush.Activity.MainActivity;
import gmsproduction.com.pushpush.R;
import gmsproduction.com.pushpush.VoiceChat.VoiceService;

import static android.content.Context.MODE_PRIVATE;

public class ProfleFragment extends Fragment {
    private View view;
    private Button mLogOutBTN;
    private FirebaseAuth mAuth;
    private TextView tV_Profilename;
    private CircleImageView iV_ProfilePic;
    private FirebaseFirestore mFireStore;
    private String userId;
    public static String user_name;
    public ProfleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profle, container, false);
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        mLogOutBTN = view.findViewById(R.id.Profile_LogOutBTN);
        tV_Profilename = view.findViewById(R.id.Profile_Name);
        iV_ProfilePic = view.findViewById(R.id.Profile_Img);

        userId = mAuth.getCurrentUser().getUid();



        mFireStore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user_name = documentSnapshot.getString("name");
                String user_Image= documentSnapshot.getString("image");

                sharedpref(userId+"#"+user_name,user_name);
                tV_Profilename.setText(user_name);
                Picasso.with(container.getContext()).load(user_Image).fit().centerCrop().placeholder(R.drawable.camilo).into(iV_ProfilePic);
            }
        });





        mLogOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void sharedpref(String name,String myName){
        if (getActivity()!=null){
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("users", MODE_PRIVATE).edit();
            editor.putString("name", name);
            editor.putString("MyName", myName);

            editor.apply();
            getActivity().startService(new Intent(getContext(),VoiceService.class));
        }
    }

}
