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
import android.widget.TextView;
import android.widget.Toast;

import com.burakdemir.firebasejava.Model.Kullanici;
import com.burakdemir.firebasejava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class KullaniciAyarlariActivity extends AppCompatActivity {

    EditText etKullaniciAyarlariName;
    EditText etKullaniciAyarlariTelNo;
    EditText etKullaniciAyarlariSifre;
    EditText etKullaniciAyarlariYeniSifre;
    EditText etKullaniciAyarlariYeniMail;

    TextView tvKullaniciAyarlariMail;

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

        kullaniciBilgileriniOku();
    }

    private void findViews() {

        etKullaniciAyarlariName = findViewById(R.id.etKullaniciAyarlariName);
        etKullaniciAyarlariTelNo = findViewById(R.id.etKullaniciAyarlariTelNo);
        etKullaniciAyarlariSifre = findViewById(R.id.etKullaniciAyarlariSifre);
        tvKullaniciAyarlariMail = findViewById(R.id.tvKullaniciAyarlariMail);
        clKullaniciAyarlariGuncelle = findViewById(R.id.clKullaniciAyarlariGuncelle);
        btnKullaniciAyarlariSifreGuncelle = findViewById(R.id.btnKullaniciAyarlariSifreGuncelle);
        btnKullaniciAyarlariMailGuncelle = findViewById(R.id.btnKullaniciAyarlariMailGuncelle);
        etKullaniciAyarlariYeniSifre = findViewById(R.id.etKullaniciAyarlariYeniSifre);
        etKullaniciAyarlariYeniMail = findViewById(R.id.etKullaniciAyarlariYeniMail);
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

    public void tvKullaniciAyarlariSifreveyaMailGuncelle(View view) {

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

    public void btnKullaniciAyarlariDegisiklikleriKaydet(View view) {

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

                                    // isim zaten başarılı şekilde dönmüştür database'e ekleyebilirim
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("kullanici")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("isim")
                                            .setValue(etKullaniciAyarlariName.getText().toString());

                                    Toast.makeText(KullaniciAyarlariActivity.this, "Değişiklikler Yapıldı", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }

        }
        else {

            Toast.makeText(KullaniciAyarlariActivity.this, "Kullanıcı adını doldurunuz", Toast.LENGTH_LONG).show();
        }

        if (!etKullaniciAyarlariTelNo.getText().toString().isEmpty()) {

            FirebaseDatabase.getInstance().getReference()
                    .child("kullanici")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("telefon")
                    .setValue(etKullaniciAyarlariTelNo.getText().toString());

            Toast.makeText(KullaniciAyarlariActivity.this, "Telefon numarası eklendi", Toast.LENGTH_LONG).show();
        }
    }

    public void tvKullaniciAyarlariSifremiUnuttum(View view) {

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

    private void kullaniciBilgileriniOku() {

        // Database'in kendisine ulaşmanı sağlar
        // fir-java-873da
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        // query1 - orderByKey
        // imleç kullanıcı kısmına gitti

        // orderbykey ile burada uid şeklinde sıralı alanlar [key olarak] gelecek

        // sıralanan bu değelerden mevcut kullanıcı uid'sine eşit olanı getir bunu demeseydim bütün uid'ler gelecekti
        // ancak uid unique olduğu için tek bir tane kullanıcının bilgileri gelecek

        // limitToFirst() gibi sıralama parametreleride mevcut
        Query query = reference.child("kullanici").orderByKey().equalTo(kullanici.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            // veri geldiği zaman tetiklenir
            // Datasnapshot json gibi key-value üzerinden çalışır
            // hangi değeri almak istiyosan Snapshot üzerinden getkey ya da getvalue diyerek alabilirsin
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) { // bi alta indin child'ları aldın

                    // json parçalama gibi sınıfa tek tek atan metot
                    // tek seferde hepsi birden atanır çünkü json gibi veri geliyor
                    // burada önemli olan sınıfın property'leri ile json gibi key'leri aynı olması
                    Kullanici okunanKullanici = child.getValue(Kullanici.class);

                    Log.d("etiket", "isim: " + okunanKullanici.getIsim() + " kullanici id: " + okunanKullanici.getKullanici_id() + " telefon: " + okunanKullanici.getTelefon());
                }
            }

            // iptal olma durumunda tetiklenir
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // query2 - orderByChild
        // child içerisindeki bir değere göre sıralama yaptım
        // telefon numarası şu olan bir kullanıcıyı da getir diyebilirdim
        Query query2 = reference.child("kullanici").orderByChild("kullanici_id").equalTo(kullanici.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            // veri geldiği zaman tetiklenir
            // Datasnapshot json gibi key-value üzerinden çalışır
            // hangi değeri almak istiyosan Snapshot üzerinden getkey ya da getvalue diyerek alabilirsin
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // burada ben tek nesne aldığım için bir kere döndü
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    // json parçalama gibi sınıfa tek tek atan metot
                    // tek seferde hepsi birden atanır çünkü json gibi veri geliyor
                    // burada önemli olan sınıfın property'leri ile json gibi key'leri aynı olması
                    Kullanici okunanKullanici = child.getValue(Kullanici.class);

                    etKullaniciAyarlariName.setText(okunanKullanici.getIsim());
                    etKullaniciAyarlariTelNo.setText(okunanKullanici.getTelefon());

                    Log.d("etiket", "isim: " + okunanKullanici.getIsim() + " kullanici id: " + okunanKullanici.getKullanici_id() + " telefon: " + okunanKullanici.getTelefon());
                }
            }

            // iptal olma durumunda tetiklenir
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // query3 - orderByValue
        // değerleri value değerlerine göre ele alır
        // o value değerlerinin sahip olduğu object'i döndürür
        Query query3 = reference.child("kullanici").child("kullanici_id").orderByValue().equalTo(kullanici.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            // veri geldiği zaman tetiklenir
            // Datasnapshot json gibi key-value üzerinden çalışır
            // hangi değeri almak istiyosan Snapshot üzerinden getkey ya da getvalue diyerek alabilirsin
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("deneme", "" + dataSnapshot);

                // burada ben tek nesne aldığım için bir kere döndü
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    // json parçalama gibi sınıfa tek tek atan metot
                    // tek seferde hepsi birden atanır çünkü json gibi veri geliyor
                    // burada önemli olan sınıfın property'leri ile json gibi key'leri aynı olması
                    Kullanici okunanKullanici = child.getValue(Kullanici.class);

                    Log.d("etiket", "isim: " + okunanKullanici.getIsim() + " kullanici id: " + okunanKullanici.getKullanici_id() + " telefon: " + okunanKullanici.getTelefon());
                }
            }

            // iptal olma durumunda tetiklenir
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
