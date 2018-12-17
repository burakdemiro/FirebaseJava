package com.burakdemir.firebasejava.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.burakdemir.firebasejava.Fragment.OnayMailDialogFragment;
import com.burakdemir.firebasejava.Fragment.SifremiUnuttumDialogFragment;
import com.burakdemir.firebasejava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    ImageView ivLoginResim;

    EditText etLoginMail;
    EditText etLoginSifre;

    TextView tvLoginKayitOl;

    Button btnLoginGirisYap;

    ProgressBar pbLogin;

    FirebaseAuth.AuthStateListener myAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initMyAuthStateListener();

        findViews();

        etLoginMail.setText("ddmrburak@gmail.com");
        etLoginSifre.setText("123456");
    }

    private void initMyAuthStateListener() {

        // kullanıcının state (durumunu) dinleyen bir listener
        // kullanıcı sisteme giriş ve çıkış yaptığında burası tetikleniyor
        myAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser kullanici = firebaseAuth.getCurrentUser();

                if (kullanici != null) {

                    if (kullanici.isEmailVerified()) {

                        Toast.makeText(LoginActivity.this, "Mail onaylanmış giriş yapılabilir", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                    }
                    else {

                        Toast.makeText(LoginActivity.this, "Mail adresini onaylayıp öyle giriş yapın", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    }

    private void findViews() {

        ivLoginResim = findViewById(R.id.ivLoginResim);
        etLoginMail = findViewById(R.id.etLoginMail);
        etLoginSifre = findViewById(R.id.etLoginSifre);
        tvLoginKayitOl = findViewById(R.id.tvLoginKayitOl);
        btnLoginGirisYap = findViewById(R.id.btnLoginGirisYap);
        pbLogin = findViewById(R.id.pbLogin);
    }

    public void tvLoginKayitOl(View view) {

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void btnLoginGirisYap(View view) {

        if (!etLoginMail.getText().toString().isEmpty() && !etLoginSifre.getText().toString().isEmpty()) {

            progressBarGoster();

            // kullaniciyi email ve sifresine göre sisteme dahil et
            FirebaseAuth.getInstance().signInWithEmailAndPassword(etLoginMail.getText().toString(), etLoginSifre.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        // işlem tamamlanmış ise burası tetiklenir
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // işlem tamamlanmış ve başarılı ise burası tetiklenir
                            if (task.isSuccessful()) {

                                progressBarGizle();

                                // kullanıcı mailini onaylamış ise
                                if (task.getResult().getUser().isEmailVerified()) {


                                }
                                else {

                                    // kullanıcı signOut yapılmazsa email onaylamadan sisteme girebilir
                                    FirebaseAuth.getInstance().signOut();
                                }

                                // Toast.makeText(LoginActivity.this, "Başarılı Giriş" + task.getResult().getUser().getUid(), Toast.LENGTH_SHORT).show();

                            }
                            else {

                                progressBarGizle();
                                Toast.makeText(LoginActivity.this, "Başarısız Giriş" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {

            Toast.makeText(LoginActivity.this, "Boş alanları doldurunuz", Toast.LENGTH_SHORT).show();
        }

    }

    private void progressBarGoster() {

        pbLogin.setVisibility(View.VISIBLE);
    }

    private void progressBarGizle() {

        pbLogin.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // giriş ve çıkış işlemlerini dinlemek için benim bir interface nesnem var, onu ekle
        // böylece uygulama açılır açılmaz listenerlar çalışır ve kullanıcı giriş yapmışsa tekrar login ekranı getirmez
        FirebaseAuth.getInstance().addAuthStateListener(myAuthStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        // uygulama kapanınca kaldırıyorum
        FirebaseAuth.getInstance().removeAuthStateListener(myAuthStateListener);
    }

    public void tvLoginOnayMail(View view) {

        OnayMailDialogFragment onayMailDialogFragment = new OnayMailDialogFragment();
        onayMailDialogFragment.show(getSupportFragmentManager(), "onayMailDialogFragment");
    }

    public void tvSifremiUnuttum(View view) {

        SifremiUnuttumDialogFragment sifremiUnuttumDialogFragment = new SifremiUnuttumDialogFragment();
        sifremiUnuttumDialogFragment.show(getSupportFragmentManager(), "sifremiUnuttumDialogFragment");
    }
}
