package com.example.tamufirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class loginTamuActivity extends AppCompatActivity {
    private Button Btn_login,Btn_register,Btn_reset;
    private EditText Email;
    private EditText Password;

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference user;
    private ProgressDialog mProgressDialog;

    private DatabaseReference IDFirebaseDatabase;
    private FirebaseDatabase IDFirebaseInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tamu);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseDatabase.getInstance().getReference().child("tamu");
        Email = (EditText) findViewById(R.id.txt_email);
        Password = (EditText) findViewById(R.id.txt_password);
        Btn_login = (Button) findViewById(R.id.btn_login);
        Btn_register = (Button) findViewById(R.id.btn_signup);
        Btn_reset = (Button) findViewById(R.id.btn_reset);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        IDFirebaseInstance = FirebaseDatabase.getInstance();
        IDFirebaseDatabase = IDFirebaseInstance.getReference("tamu").child("tamu_akses");

        Btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regis = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(regis);
            }
        });

        Btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reset = new Intent(getApplicationContext(),resetPassActivity.class);
                startActivity(reset);
            }
        });

        Btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        final String email_id = Email.getText().toString();
        String password = Password.getText().toString();
        if (TextUtils.isEmpty(email_id) && TextUtils.isEmpty(password)) {
            Toast.makeText(loginTamuActivity.this, "Isi Email dan Password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email_id)) {
            Toast.makeText(loginTamuActivity.this, "Isi Email Dahulu", Toast.LENGTH_SHORT).show();
        } else if (!email_id.contains("@")) {
            Email.setError("kurang @");
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(loginTamuActivity.this, "Isi Password Dahulu", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog dialog = new ProgressDialog(loginTamuActivity.this);
            dialog.setMessage("Harap Tunggu...");
            dialog.show();
            mAuth.signInWithEmailAndPassword(email_id, password)
                    .addOnCompleteListener(loginTamuActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(loginTamuActivity.this, "Maaf, Gagal Login", Toast.LENGTH_SHORT).show();
                            } else {
                                String currentuid = mAuth.getCurrentUser().getUid();
                                String devicetoken = FirebaseInstanceId.getInstance().getToken();
                                dialog.dismiss();
                                user.child(currentuid).child("devicetoken")
                                        .setValue(devicetoken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    IDFirebaseDatabase.child("email_tamu").setValue(email_id);
                                                    Intent home = new Intent(loginTamuActivity.this, tamuActivity.class);
                                                    startActivity(home);
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }
}