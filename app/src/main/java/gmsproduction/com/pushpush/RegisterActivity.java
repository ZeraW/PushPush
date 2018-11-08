package gmsproduction.com.pushpush;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE = 1;
    private Button backToLogin,doSignUp;
    private EditText eT_signUpName,eT_signUpPasswrod,eT_signUpEmail;
    private CircleImageView iV_signUpImage;
    private Uri mImageUri;
    private StorageReference mStorge;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Init();
    }

    private void Init() {
        //firebase related stuff
        mStorge = FirebaseStorage.getInstance().getReference().child("images");
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();

        mImageUri = null;
        backToLogin = findViewById(R.id.SignUp_backToLogin);
        progressBar = findViewById(R.id.progressBar2);
        doSignUp = findViewById(R.id.SignUp_doSignUp);
        eT_signUpName = findViewById(R.id.SignUp_Name);
        eT_signUpEmail = findViewById(R.id.SignUp_Email);
        eT_signUpPasswrod = findViewById(R.id.SignUp_Password);
        iV_signUpImage = findViewById(R.id.SignUp_profile_image);
        backToLogin.setOnClickListener(this);
        iV_signUpImage.setOnClickListener(this);
        doSignUp.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id) {
            case R.id.SignUp_backToLogin:
                finish();
                break;
            case R.id.SignUp_profile_image:
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
                break;
            case R.id.SignUp_doSignUp:
                SignUp();
                break;

        }
    }

    private void SignUp() {
        if (mImageUri!=null){
            progressBar.setVisibility(View.VISIBLE);
            final String name = eT_signUpName.getText().toString();
            String email = eT_signUpEmail.getText().toString();
            String password = eT_signUpPasswrod.getText().toString();
            if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //get the user id
                            final String user_Id = mAuth.getCurrentUser().getUid();

                            //storage ref for image
                            StorageReference user_profile = mStorge.child(user_Id+".jpg");

                            //to upload img and check if image is uploaded or not
                            user_profile.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadtask) {
                                    if (uploadtask.isSuccessful()){

                                        //get the uploaded img url
                                        String imgURL = uploadtask.getResult().getDownloadUrl().toString();
                                        //put the data into map
                                        Map<String,Object> userMap = new HashMap<>();
                                        userMap.put("name",name);
                                        userMap.put("image",imgURL);



                                        //here where are we going to store all of the users
                                        mFireStore.collection("Users").document(user_Id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressBar.setVisibility(View.GONE);

                                                SendUserToMainActivity();

                                            }
                                        });

                                    }else {
                                        Toast.makeText(RegisterActivity.this, "ErrorIMG"+uploadtask.getException(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }
                            });
                            
                        }else {
                            Toast.makeText(RegisterActivity.this, "Error"+task.getException(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }

                    }
                });
            }
        }
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE){
            mImageUri = data.getData();
            iV_signUpImage.setImageURI(mImageUri);

        }
    }


}
