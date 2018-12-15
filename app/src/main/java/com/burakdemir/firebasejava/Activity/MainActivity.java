package com.burakdemir.firebasejava.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.burakdemir.firebasejava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth.AuthStateListener mAuthStateListener;

    TextView tvMainAd;
    TextView tvMainMail;
    TextView tvMainUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        initAuthStateListener();
    }

    private void setKullaniciBilgileri() {

        FirebaseUser kullanici = FirebaseAuth.getInstance().getCurrentUser();

        if (kullanici != null) {

            if (kullanici.getDisplayName() != null) {

                tvMainAd.setText((kullanici.getDisplayName().isEmpty() ? "Tanımlanmadı" : kullanici.getDisplayName()));
            }

            tvMainMail.setText(kullanici.getEmail());
            tvMainUserId.setText(kullanici.getUid());
        }
    }

    private void findViews() {

        tvMainAd = findViewById(R.id.tvMainAd);
        tvMainMail = findViewById(R.id.tvMainMail);
        tvMainUserId = findViewById(R.id.tvMainUserId);
    }

    private void initAuthStateListener() {

        // ne zaman giriş yaptı çıkış yaptı burası tetiklenecek
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser kullanici = firebaseAuth.getCurrentUser();

                if (kullanici != null) {


                }
                else {

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ana_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menuCikisYap:
                cikisYap();
                break;

            case R.id.menuHesapAyarlari:
                Intent intent = new Intent(MainActivity.this, KullaniciAyarlariActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cikisYap() {

        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthStateListener != null) {

            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 2.güvenlik aşaması
        // Eğer kullanıcı ileri geri yaptıysa bir şekilde Activity'e gelmiş ise
        // sistemde bulunmuyorsa sistemden at
        kullaniciyiKontrolEt();

        // activity'de geri dönüldüğünde onCreate yeniden çalıştırılmaz
        // bu yüzden onResume üzerinden verileri güncellemek mantıklıdır
        setKullaniciBilgileri();
    }

    private void kullaniciyiKontrolEt() {

        FirebaseUser kullanici = FirebaseAuth.getInstance().getCurrentUser();

        if (kullanici != null) {


        }
        else {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        }
    }
}
