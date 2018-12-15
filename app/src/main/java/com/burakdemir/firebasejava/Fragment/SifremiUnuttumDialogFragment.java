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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SifremiUnuttumDialogFragment extends DialogFragment {

    EditText etSifremiUnuttumMail;

    Button btnSifremiUnuttumIptal;
    Button btnSifremiUnuttumGonder;

    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sifremi_unuttum_dialog, container, false);

        context = getActivity();

        findViews(v);

        btnSifremiUnuttumIptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDialog().dismiss();
            }
        });

        btnSifremiUnuttumGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etSifremiUnuttumMail.getText().toString().isEmpty()) {

                    sifremiGonder(etSifremiUnuttumMail.getText().toString());
                }
                else {

                    Toast.makeText(context, "Boş alanları doldurunuz", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void sifremiGonder(String mail) {

        // kullanıcı mevcut olsa da olmasa da farketmez
        // bu yüzden kullanici != null kontrolünü burada sağlamadım
        FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(context, "Şifre sıfırlama maili gönderildi", Toast.LENGTH_SHORT).show();
                            getDialog().dismiss();
                        }
                        else {

                            Toast.makeText(context, "Hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            getDialog().dismiss();
                        }
                    }
                });
    }


    private void findViews(View v) {

        etSifremiUnuttumMail = v.findViewById(R.id.etSifremiUnuttumMail);
        btnSifremiUnuttumIptal = v.findViewById(R.id.btnSifremiUnuttumIptal);
        btnSifremiUnuttumGonder = v.findViewById(R.id.btnSifremiUnuttumGonder);
    }

}
