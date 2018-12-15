package com.burakdemir.firebasejava.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.burakdemir.firebasejava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText etMail;
    EditText etSifre;
    EditText etSifreTekrar;

    Button btnKayit;

    ProgressBar pbRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();

    }

    private void findViews() {

        etMail = findViewById(R.id.etRegisterMail);
        etSifre = findViewById(R.id.etRegisterSifre);
        etSifreTekrar = findViewById(R.id.etRegisterSifreTekrar);
        btnKayit = findViewById(R.id.btnRegisterKayitOl);
        pbRegister = findViewById(R.id.pbRegister);
    }

    public void btnRegisterKayitOl(View view) {

        if (!etMail.getText().toString().isEmpty() && !etSifre.getText().toString().isEmpty() && !etSifreTekrar.getText().toString().isEmpty()) {

            if (etSifre.getText().toString().equals(etSifreTekrar.getText().toString())) {

                yeniUyeKayit(etMail.getText().toString(), etSifre.getText().toString());
            }
            else {

                Toast.makeText(this, "Şifreler birbiri ile aynı olmalıdır", Toast.LENGTH_SHORT).show();
            }
        }

        else {

            Toast.makeText(this, "Boş alanları doldurunuz", Toast.LENGTH_SHORT).show();
        }
    }

    private void yeniUyeKayit(String mail, String sifre) {

        progressBarGoster();

        // singleton bir yapıda Auth sınıfı
        // email ve sifre ile kayıt oluşturma metotu
        // burada task (auth görevi) yaptığını söylüyor
        // yapılan bu tüm network işlemler worker thread üzerinde gerçekleşiyor
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, sifre)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    // eğer tamamlanırsa burası tetikleniyor (fakat başarılı olmuş diye kesin bir şey yok gelen parametreden kontrolünü sağla)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // kullanıcı firebase'e kaydedilir
                        // task parametresinden kullanıcının tüm verilerine (mail, uid vs) ulaşabilirsin
                        if (task.isSuccessful()) {


                            Toast.makeText(RegisterActivity.this, "Üye kaydedildi" + task.getResult().getUser().getUid(), Toast.LENGTH_SHORT).show();

                            // kullanıcıyı signOut yapmadan önce ona onay maili göndericem
                            onayMailiGonder();

                            // firebase kullanıcıyı oluşturduysa otomatik olarak login yapar bu yüzden signout diyerek sistemden atabilirsin
                            FirebaseAuth.getInstance().signOut();
                        }
                        else {

                            Toast.makeText(RegisterActivity.this, "Üye kaydedilerken sorun oluştu" + task.getException(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });

        progressBarGizle();
    }

    private void onayMailiGonder() {

        // mevcut o anki kullanıcıyı ele alıyorum
        FirebaseUser kullanici = FirebaseAuth.getInstance().getCurrentUser();

        // her ne kadar sistemde olduğunu düşünsekte null'a eşit olabilir kontrolü sağlıyorum
        if (kullanici != null) {

            // onay maili gönder
            kullanici.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    // başarılı bir şekilde kullanıcıya mail atılmış ise burayı ele alıcam
                    if (task.isSuccessful()) {

                        Toast.makeText(RegisterActivity.this, "Mail kutunuzu kontrol edin ve onaylayın", Toast.LENGTH_LONG).show();
                    }
                    else {

                        Toast.makeText(RegisterActivity.this, "Mail gönderilirken sorun oluştu" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }


    private void progressBarGoster() {

        pbRegister.setVisibility(View.VISIBLE);
    }

    private void progressBarGizle() {

        pbRegister.setVisibility(View.INVISIBLE);
    }
}
