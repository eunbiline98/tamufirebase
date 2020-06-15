package com.example.tamufirebase;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class controlActivity extends AppCompatActivity {

    private DatabaseReference PintuFirebaseDatabase;
    private FirebaseDatabase PintuFirebaseInstance;

    private DatabaseReference IDFirebaseDatabase;
    private FirebaseDatabase IDFirebaseInstance;

    private DatabaseReference TimeFirebaseDatabase;
    private FirebaseDatabase TimeFirebaseInstance;

    private CountDownTimer CountTimer;

    private TextView KeadaanPintu,waktu;

    public int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        KeadaanPintu = findViewById(R.id.keadaan_pintu);
        waktu = findViewById(R.id.waktu);
        // pintu control
        PintuFirebaseInstance = FirebaseDatabase.getInstance();
        PintuFirebaseDatabase = PintuFirebaseInstance.getReference("control");
        // id random akses
        IDFirebaseInstance = FirebaseDatabase.getInstance();
        IDFirebaseDatabase = IDFirebaseInstance.getReference("tamu").child("tamu_akses");

        // timer conutdown
        TimeFirebaseInstance = FirebaseDatabase.getInstance();
        TimeFirebaseDatabase = TimeFirebaseInstance.getReference("time_counter");

        // kondisi pintu
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference statusref = database.getReference("control");

        final DatabaseReference timer = database.getReference("time_counter");

        timer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int time = dataSnapshot.getValue(int.class);
                hitung (time);

            }
            private void hitung (int time) {
                final long[] times = {time};
                CountTimer =new CountDownTimer(times[0],1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        times[0] = millisUntilFinished;
                        int minutes = (int) (times[0] / 1000) / 60;
                        int seconds = (int) (times[0] / 1000) % 60;
                        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                        waktu.setText(timeLeftFormatted);
                        counter++;
                        System.out.println(counter);
                        statusref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int nilai = dataSnapshot.getValue(int.class);
                                KeadaanPintu (nilai);
                            }

                            private void KeadaanPintu(int nilai) {
                                if(nilai == 1) {
                                    KeadaanPintu.setText("Terbuka");
                                } else {
                                    KeadaanPintu.setText("Tertutup");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    @Override
                    public void onFinish() {
                        TimeFirebaseDatabase.setValue(0);
                        IDFirebaseDatabase.child("encrypt").setValue("");
                        IDFirebaseDatabase.child("email_tamu").setValue("-");
                        IDFirebaseDatabase.child("decrypt").setValue("");
                        IDFirebaseDatabase.child("public_key").setValue("");
                        IDFirebaseDatabase.child("private_key").setValue("");
                        finish();
                        Intent login = new Intent(getApplicationContext(), loginTamuActivity.class);
                        startActivity(login);

                    }
                }.start();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void bukaData(View view)
    {PintuFirebaseDatabase.setValue(1);
        //mengirim keadaan pintu terbuka
    }

    public void tutupData(View view)
    {PintuFirebaseDatabase.setValue(0);
        //mengirim keadaan pintu tertutup
    }
}

