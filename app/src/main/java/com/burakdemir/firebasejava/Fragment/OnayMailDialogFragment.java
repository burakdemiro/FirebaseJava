package com.burakdemir.firebasejava.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.burakdemir.firebasejava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OnayMailDialogFragment extends DialogFragment {

    EditText etDialogMail;
    EditText etDialogSifre;

    Button btnDialogGonder;
    Button btnDialogIptal;
    
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_onay_mail_dialog, container, false);
        
        context = getActivity();

        findViews(v);

        btnDialogIptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDialog().dismiss();
            }
        });

        btnDialogGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etDialogMail.getText().toString().isEmpty() && !etDialogSifre.getText().toString().isEmpty()) {

                    girisYapveOnayMailiniTekrarGonder(etDialogMail.getText().toString(), etDialogSifre.getText().toString());
                }
                else {

                    Toast.makeText(context, "Boş alanları doldurunuz", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void girisYapveOnayMailiniTekrarGonder(String mail, String sifre) {

        // kullanıcı mevcut olsa da olmasa da farketmez
        // bu yüzden kullanici != null kontrolünü burada sağlamadım

        // credential: kullanıcı email ve sifre kimlik bilgisini tutan nesne
        AuthCredential credential = EmailAuthProvider.getCredential(mail, sifre);

        // bu sefer email ve sifre bilgisini tutan kimlik ile giriş yap dedim hiçbir farkı yok
        // fragmente'de olsan arkada activity çalıştığı için giriş ve çıkış işleminde mAuthStateListener (LoginActivity) tetiklenir
        // (LoginActivity mAuthStateListener içi) signOut yaparsan kullanıcı sistemden atıldığı için
        // onayMailiniTekrarGonder() kısmında kullanıcı == null durumuna girdiğinden çalışmayacaktır
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            onayMailiniTekrarGonder();
                            getDialog().dismiss();
                        }
                        else {

                            Toast.makeText(context, "Email veya Şifre Hatalı", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onayMailiniTekrarGonder() {

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

                        Toast.makeText(context, "Mail kutunuzu kontrol edin ve onaylayın", Toast.LENGTH_LONG).show();
                    }
                    else {

                        Toast.makeText(context, "Mail gönderilirken sorun oluştu" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private void findViews(View v) {

        etDialogMail = v.findViewById(R.id.etDialogMail);
        etDialogSifre = v.findViewById(R.id.etDialogSifre);
        btnDialogGonder = v.findViewById(R.id.btnDialogGonder);
        btnDialogIptal = v.findViewById(R.id.btnDialogIptal);
    }

}
