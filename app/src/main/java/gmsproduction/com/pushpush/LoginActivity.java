package gmsproduction.com.pushpush;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pusher.pushnotifications.PushNotifications;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button register, doLogin;
    private EditText eT_loginEmail, eT_loginPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    private void sendToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();
    }

    private void Init() {
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar_logIn);
        register = findViewById(R.id.Login_SignUp);
        doLogin = findViewById(R.id.Login_doLogin);
        eT_loginEmail = findViewById(R.id.Login_email);
        eT_loginPassword = findViewById(R.id.Login_password);
        register.setOnClickListener(LoginActivity.this);
        doLogin.setOnClickListener(LoginActivity.this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id) {
            case R.id.Login_SignUp:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.Login_doLogin:
                LogIN();
                break;
        }
    }

    private void LogIN() {
        String email = eT_loginEmail.getText().toString();
        String password = eT_loginPassword.getText().toString();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // if the login completed
                    if (task.isSuccessful()) {
                        sendToMain();
                        progressBar.setVisibility(View.GONE);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();

                    }

                }
            });
        }
    }
}