package com.burakdemir.firebasejava.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.burakdemir.firebasejava.R;

public class ProfileResmiFragment extends DialogFragment {

    TextView tvProfilResmiResimCek;
    TextView tvProfilResmiGaleridenSec;

    // FRAGMENTTEN ACTIVITY'E VERİ ALIŞVERİŞ METOTLARI
    // EventBus kütüphanesi
    // Interface ile gönderme
    // Interface'ler genelde On ile başlar.
    public interface OnProfilResimListener {


        void getResimYolu(Uri resimYolu);
        void getResimBitmap(Bitmap bitmap);
    }

    OnProfilResimListener onProfilResimListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile_resmi, container, false);

        tvProfilResmiResimCek = v.findViewById(R.id.tvProfilResmiResimCek);
        tvProfilResmiGaleridenSec = v.findViewById(R.id.tvProfilResmiGaleridenSec);

        tvProfilResmiGaleridenSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // INTENT ACTION_PICK ve INTENT GET_CONTENT farkı
                // ACTION_PICK: tüm veri tipleri gelir (Resim, video, pdf, mp3 vs)
                // GET_CONTENT: sadece hangi tür veri ile ilgileniyorsan onu söylüyorsun (Sadece resimleri getir gibi)
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                // bir sonuç için aktivity başlat
                startActivityForResult(intent, 100); // request code = 100
            }


        });

        tvProfilResmiResimCek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 200); // request code = 200
            }
        });

        return v;
    }

    // startActivityForResult'ı dinleyen metot
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // galeriden resim seçiliyor
        // URI: Androidde galeriden resim seçtiğin zaman resmin kendisi değil resmin adresi gelir aynı web sitelerindeki url adresi gibi
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {

            Uri galeridenSecilenResimYolu = data.getData();
            Log.d("serkan", "galeridenSecilenResimYolu: " + galeridenSecilenResimYolu);

            // bunu gönderdiğimiz verileri activity'e taşımak için yaptık (interface yoluyla)
            onProfilResimListener.getResimYolu(galeridenSecilenResimYolu);

            dismiss();
        }

        // kameradan resim seçiliyor
        // kameradan dönen resim URI ile gelmiyor Bitmap olarak dönüyor
        else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {

            Bitmap kameradanCekilenResim = (Bitmap) data.getExtras().get("data");

            // bunu gönderdiğimiz verileri activity'e taşımak için yaptık (interface yoluyla)
            onProfilResimListener.getResimBitmap(kameradanCekilenResim);
            dismiss();
        }
    }

    // fragment ilk attach olduğu anda bu metot çalışır bu yüzden interface'i initialize ettim
    @Override
    public void onAttach(Context context) {

        // getFragment = bu fragmenti çağıran activity
        // bu listenere bu fragmenti çağıran activity'e atıyorum
        onProfilResimListener = (OnProfilResimListener) getActivity();

        super.onAttach(context);
    }
}
