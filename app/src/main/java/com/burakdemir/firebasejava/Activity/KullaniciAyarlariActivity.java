package com.burakdemir.firebasejava.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.burakdemir.firebasejava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;

public class KullaniciAyarlariActivity extends AppCompatActivity {

    EditText etKullaniciAyarlariName;
    EditText etKullaniciAyarlariSifre;
    EditText etKullaniciAyarlariYeniSifre;
    EditText etKullaniciAyarlariYeniMail;

    Button btnKullaniciAyarlariSifreSifirla;
    Button btnKullaniciAyarlariKaydet;
    Button btnKullaniciAyarlariSifreGuncelle;
    Button btnKullaniciAyarlariMailGuncelle;

    FirebaseUser kullanici;

    ConstraintLayout clKullaniciAyarlariGuncelle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_ayarlari);

        findViews();

        kullanici = FirebaseAuth.getInstance().getCurrentUser();

        if (kullanici != null) {

            if (kullanici.getDisplayName() != null) {

                etKullaniciAyarlariName.setText((kullanici.getDisplayName().isEmpty() ? "Tanımlanmadı" : kullanici.getDisplayName()));
            }
        }

    }

    private void findViews() {

        etKullaniciAyarlariName = findViewById(R.id.etKullaniciAyarlariName);
        etKullaniciAyarlariSifre = findViewById(R.id.etKullaniciAyarlariSifre);
        btnKullaniciAyarlariSifreSifirla = findViewById(R.id.btnKullaniciAyarlariSifreSifirla);
        btnKullaniciAyarlariKaydet = findViewById(R.id.btnKullaniciAyarlariKaydet);
        clKullaniciAyarlariGuncelle = findViewById(R.id.clKullaniciAyarlariGuncelle);
        btnKullaniciAyarlariSifreGuncelle = findViewById(R.id.btnKullaniciAyarlariSifreGuncelle);
        btnKullaniciAyarlariMailGuncelle = findViewById(R.id.btnKullaniciAyarlariMailGuncelle);
        etKullaniciAyarlariYeniSifre = findViewById(R.id.etKullaniciAyarlariYeniSifre);
        etKullaniciAyarlariYeniMail = findViewById(R.id.etKullaniciAyarlariYeniMail);
    }

    public void btnKullaniciAyarlariSifreSifirla(View view) {

        FirebaseAuth.getInstance().sendPasswordResetEmail(kullanici.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(KullaniciAyarlariActivity.this, "Şifre sıfırlama maili gönderildi", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Toast.makeText(KullaniciAyarlariActivity.this, "Hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void btnKullaniciAyarlariKaydet(View view) {

        if (!etKullaniciAyarlariName.getText().toString().isEmpty()) {

            if (!etKullaniciAyarlariName.getText().toString().equals(kullanici.getDisplayName())) {

                // kullanıcı profili bilgilerini tutan bir sınıf credentials gibi verileri içinde tutuyor sadece
                UserProfileChangeRequest profilBilgileri = new UserProfileChangeRequest.Builder()
                        .setDisplayName(etKullaniciAyarlariName.getText().toString())
                        .build();

                kullanici.updateProfile(profilBilgileri)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(KullaniciAyarlariActivity.this, "Değişiklikler Yapıldı", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }

        }
        else {

            Toast.makeText(KullaniciAyarlariActivity.this, "Tüm boş alanları doldurun", Toast.LENGTH_LONG).show();
        }
    }

    public void btnKullaniciAyarlariSifreveyaMailGuncelle(View view) {

        if (!etKullaniciAyarlariSifre.getText().toString().isEmpty()) {


            // kullanıcının mail ve sifre işlemlerini sıfırlamak için re-authentication yapman gerekir
            // Kullanıcı giriş yaptıktan 5 dakika gibi kısa bir süre içinde mail ve sifre değiştirme ya da mail silme gibi işlemleri yapabilir süre uzarsa tekrar authentication işlemi gerekir
            // burada doğrudan kullanıcının şifreini alamazsın
            // fakat aşağıdaki yöntem ile hem şifresini doğrulamış olursun hem de sisteme tekrar dahil edersin
            AuthCredential credential = EmailAuthProvider.getCredential(kullanici.getEmail(), etKullaniciAyarlariSifre.getText().toString());

            kullanici.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                clKullaniciAyarlariGuncelle.setVisibility(View.VISIBLE);

                                btnKullaniciAyarlariMailGuncelle.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        mailAdresiniGuncelle();
                                    }
                                });

                                btnKullaniciAyarlariSifreGuncelle.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        sifreBilgisiniGuncelle();
                                    }
                                });
                            }
                            else {

                                Toast.makeText(KullaniciAyarlariActivity.this, "Şuanki şifrenizi yanlış girdiniz", Toast.LENGTH_SHORT).show();
                                clKullaniciAyarlariGuncelle.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        }
        else {

            Toast.makeText(KullaniciAyarlariActivity.this, "Güncellemeler için geçerli şifrenizi yazınız", Toast.LENGTH_SHORT).show();
        }
    }

    private void mailAdresiniGuncelle() {

        if (kullanici != null) {

            // Bu metot sistemde daha önce böyle bir mail varmı yokmu diye bakacak
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(etKullaniciAyarlariYeniMail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                            if (task.isSuccessful()) {

                                // Fetch signIn ile kontrol ediliyor eğer sistemde böyle bir mail var var ise 1 döndürülüyor
                                if (task.getResult().getSignInMethods().size() == 1) {

                                    Toast.makeText(KullaniciAyarlariActivity.this, "Mail kullanımda", Toast.LENGTH_SHORT).show();
                                }
                                else {

                                    // mail güncelleme metotu
                                    kullanici.updateEmail(etKullaniciAyarlariYeniMail.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(KullaniciAyarlariActivity.this, "Mail adresiniz değiştirildi tekrar giriş yapın", Toast.LENGTH_SHORT).show();

                                                        // kullanıcıyı tekrar giriş yapması için signOut yaptım
                                                        FirebaseAuth.getInstance().signOut();
                                                        loginSayfasinaYonlendir();
                                                    }
                                                }
                                            });
                                }
                            }
                            else {

                                Toast.makeText(KullaniciAyarlariActivity.this, "Mail güncellenemedi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }
    }

    private void sifreBilgisiniGuncelle() {


        if (kullanici != null) {

            // şifre güncelleme metotu
            kullanici.updatePassword(etKullaniciAyarlariYeniSifre.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(KullaniciAyarlariActivity.this, "Şifreniz değiştirildi tekrar giriş yapın", Toast.LENGTH_SHORT).show();

                                // kullanıcıyı tekrar giriş yapması için signOut yaptım
                                FirebaseAuth.getInstance().signOut();
                                loginSayfasinaYonlendir();
                            }
                        }
                    });
        }
    }

    private void loginSayfasinaYonlendir() {

        Intent intent = new Intent(KullaniciAyarlariActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
