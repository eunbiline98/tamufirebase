package com.example.tamufirebase;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class tamuActivity extends AppCompatActivity {
    private TextView id_tamu, key;
    private EditText id, nama;
    private Button button_akses, button_login, button_key, button_dec, button_out;
    private DatabaseReference getIdtamu;

    private String publickey = "";
    private String privatekey = "";
    private byte[] encodeData = null;

    private DatabaseReference notifFirebaseDatabase;
    private FirebaseDatabase notifFirebaseInstance;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference Ref = db.getReference();
    DatabaseReference tamuRef = Ref.child("list_tamu");

    public static final String RSA = "RSA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamu);

        button_akses = findViewById(R.id.btn_akses);
        button_login = findViewById(R.id.btn_izin);
//        button_dec = findViewById(R.id.btn_decrypt);
        button_out = findViewById(R.id.btn_out);

//        id_tamu = (TextView) findViewById(R.id.idtamu);

        id = (EditText) findViewById(R.id.txt_idmasuk);
        nama = (EditText) findViewById(R.id.txt_idnama);

        getIdtamu = FirebaseDatabase.getInstance().getReference().child("tamu");

        notifFirebaseInstance = FirebaseDatabase.getInstance();
        notifFirebaseDatabase = notifFirebaseInstance.getReference("notif").child("hIDB8GmFVHTqEOh2Hu45L2BMSEn2");

        button_akses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Random myRandom = new Random();
                notifFirebaseDatabase.child("e8ZteztHxXg:APA91bH67aQ0hbepEvIzifBvgHzgRmnwCO94Pfe1mhEmRhfS8LGeifAQuJXssSdirwOKysxom5zkyWG3cJqAPidNiVp4CFbfVFj_J9Of2t3Ll87KnXK80SAHb7G2RG1Mi8LfOjHUcpUN").setValue(String.valueOf(myRandom.nextInt(10)));
            }
        });

        button_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signout = new Intent(getApplicationContext(), loginTamuActivity.class);
                startActivity(signout);
                finish();
            }
        });

                getIdtamu.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
//                            id_tamu.setText(dataSnapshot.child("tamu_akses").child("encrypt").getValue(String.class));
//                            final String privkey = dataSnapshot.child("tamu_akses").child("encrypt").getValue(String.class);
                            final String keys = dataSnapshot.child("tamu_akses").child("decrypt").getValue(String.class);
//                            id.setText(keys);

//                            button_dec.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    id.setText(privkey);
//                                    PrivateKey privateKey = keyPair.getPrivate();
//                                    String decrypted = CipherUtil.decryptRSA((String) id_tamu.getText(), privateKey);
//                                    System.out.println("decrypted Id Tamu : " + decrypted);
//                                    id.setText(decrypted);
//                                    System.out.println(privateKey);
//                                }
//                            });

                            button_login.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String inputid = id.getText().toString();
                                    String namatamu = nama.getText().toString();
                                    if (TextUtils.isEmpty(inputid) && TextUtils.isEmpty(namatamu)) {
                                        Toast.makeText(tamuActivity.this, "Isi Data Dahulu", Toast.LENGTH_SHORT).show();
                                    } else if (TextUtils.isEmpty(inputid)) {
                                        Toast.makeText(tamuActivity.this, "Isi Kunci Akses Dahulu", Toast.LENGTH_SHORT).show();
                                    } else if (TextUtils.isEmpty(namatamu)) {
                                        Toast.makeText(tamuActivity.this, "Isi Nama Dahulu", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String tamuToAdd = nama.getText().toString();
                                        if (id.getText().toString().equals(keys)) {
                                            if (!tamuToAdd.equals("")) {
                                                tamuRef.push().child("nama Tamu").setValue(tamuToAdd);
                                            }
                                            Intent home = new Intent(tamuActivity.this, controlActivity.class);
                                            startActivity(home);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(tamuActivity.this, "Akses Tidak Diizinkan", Toast.LENGTH_SHORT).show();
                                            id.setText("");
                                            nama.setText("");
                                        }
                                    }
                                }
                            });
                        }
                        else {
                            id_tamu.setText("get firebase error");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void aksesData(View view) {
        final Random myRandom = new Random();
        notifFirebaseDatabase.child("id_random").setValue(String.valueOf(myRandom.nextInt(10)));

    }

//        @RequiresApi(api = Build.VERSION_CODES.O)
//        public void decrypt(View view) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException {//            KeyPair keyPair = CipherUtil.genRSAKeyPair();
//            PrivateKey privateKey = keyPair.getPrivate();
//            String decrypted = CipherUtil.decryptRSA((String) id_tamu.getText(), privateKey);
//            System.out.println("decrypted Id Tamu : " + decrypted);
//            id.setText(decrypted);
//            System.out.println(privateKey);
//
//            byte[] bytePrivateKey = privateKey.getEncoded();
//                    String base64PrivateKey = Base64.getEncoder().encodeToString(bytePrivateKey);
//                    System.out.println("Base64 Private Key : " + base64PrivateKey);
//        }
}



