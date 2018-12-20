package com.burakdemir.firebasejava.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.burakdemir.firebasejava.Fragment.ProfileResmiFragment;
import com.burakdemir.firebasejava.Model.Kullanici;
import com.burakdemir.firebasejava.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class KullaniciAyarlariActivity extends AppCompatActivity implements ProfileResmiFragment.OnProfilResimListener{

    EditText etKullaniciAyarlariName;
    EditText etKullaniciAyarlariTelNo;
    EditText etKullaniciAyarlariSifre;
    EditText etKullaniciAyarlariYeniSifre;
    EditText etKullaniciAyarlariYeniMail;

    TextView tvKullaniciAyarlariMail;

    Button btnKullaniciAyarlariSifreGuncelle;
    Button btnKullaniciAyarlariMailGuncelle;

    ImageView ivKullaniciAyarlariProfilePhoto;

    ProgressBar pbPicture;

    FirebaseUser kullanici;

    ConstraintLayout clKullaniciAyarlariGuncelle;

    boolean izinlerVerildiMi = false;

    Uri galeridenGelenURI = null;
    Bitmap kameradanGelenBitmap = null;

    final double MEGABAYT = 1000000;

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
        ivKullaniciAyarlariProfilePhoto = findViewById(R.id.ivKullaniciAyarlariProfilePhoto);
        pbPicture = findViewById(R.id.pbPicture);
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
        }

        if (galeridenGelenURI != null) {

            fotografCompress(galeridenGelenURI);
        }
        else if (kameradanGelenBitmap != null) {

            fotografCompress(kameradanGelenBitmap);
        }
    }

    private void fotografCompress(Uri galeridenGelenURI) {

        // Arkaplan çalışacak işlemi yapacak sınıfımdan nesne oluşturuyorum
        BackgroundResimCompress compress = new BackgroundResimCompress();
        // doInBackground'ı çalıştırmak için execute metotunu çalıştırdım
        compress.execute(galeridenGelenURI);
    }

    private void fotografCompress(Bitmap kameradanGelenBitmap) {

        BackgroundResimCompress compress = new BackgroundResimCompress(kameradanGelenBitmap);
        Uri uri = null;
        compress.execute(uri);
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

                    etKullaniciAyarlariName.setText(okunanKullanici.getIsim());
                    etKullaniciAyarlariTelNo.setText(okunanKullanici.getTelefon());
                    Picasso.get().load(okunanKullanici.getProfil_resmi()).resize(100, 100).into(ivKullaniciAyarlariProfilePhoto);

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

    public void ivKullaniciAyarlariProfilePhoto(View view) {

        if (izinlerVerildiMi) {

            ProfileResmiFragment profileResmiFragment = new ProfileResmiFragment();
            profileResmiFragment.show(getSupportFragmentManager(), "profileResmiFragment");
        }
        else {

            izinleriIste();
        }


    }

    private void izinleriIste() {

        String izinler[] = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        // kontrolü yapılacak izin == Paket Yöneticisi tarafından (Android içerisinde izin verilmiş mi?)
        // 3 iznin hepsine izin verilmiş mi daha önceden kontrol et
        if (ContextCompat.checkSelfPermission(this, izinler[0]) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, izinler[1]) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, izinler[2]) == PackageManager.PERMISSION_GRANTED) {

                izinlerVerildiMi = true;
        }
        else {

            // izin verilmemiştir izin iste
            ActivityCompat.requestPermissions(this, izinler, 150);
        }
    }

    // Kullanıcıdan izin istedikten sonraki sonuçlar buraya düşer
    // ActivityCompat.requestPermissions çağrılırsa burası tetiklenir, sonuçlar buraya düşer
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 150) {

            // kullanıcı kendisine sunulan tüm izinleri kabul etmiş ise bu koşul sağlanır
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                izinlerVerildiMi = true;
            }
            else {

                Toast.makeText(KullaniciAyarlariActivity.this, "Tüm izinleri vermelisiniz", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ProfilResmiFragment Listeners
    // fragment içerisinde onActivityResult içerisindeki interface her tetiklendiğinde bu metotlarda da tetiklenmiş olacak
    @Override
    public void getResimYolu(Uri resimYolu) {

        galeridenGelenURI = resimYolu;
        // Picasso ile URI formatında da resim yükleme işlemi yapabilirsin
        Picasso.get().load(galeridenGelenURI).resize(100, 100).into(ivKullaniciAyarlariProfilePhoto);
    }

    @Override
    public void getResimBitmap(Bitmap bitmap) {

        kameradanGelenBitmap = bitmap;
        ivKullaniciAyarlariProfilePhoto.setImageBitmap(bitmap);
    }

    // Main Thread'de olmaması gereken işlemleri yapmamız için android'in bize verdiği AsyncTask sınıfını kullandım
    // 1.parametre params: doInBackground'ın alacağı işlenecek veri türü (galeriden resim seçme)
    // 2.parametre progress: arka plan süresince yapılacak işlemler (proggress bar işlemleri)
    // 3.parametre result: arka plan işlemlerinden ne tür bir veri dönecek onun bilgisi doInBackground geriye ne döndürecek
    class BackgroundResimCompress extends AsyncTask<Uri, Double, byte[]> {

        Bitmap myBitmap = null;

        public BackgroundResimCompress(Bitmap bm) {

            if (bm != null) {

                myBitmap = bm;
            }
        }

        public BackgroundResimCompress() {

        }

        // Bu metot main thread üzerinde çalışıyor
        // doInBackground işlemi yapılmadan önce çalışan metot
        // UI elemanlarına yazma gibi main thread üzerinde yapılması gereken işlemler burada yapılıyor bir nevi doInBackground arasında bir köprü
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Bu metot worker thread üzerinde çalışıyor
        @Override
        protected byte[] doInBackground(Uri... uris) {

            // galeriden resim seçilmiş, URI gelmiştir
            // burada bitmap'in constructoru çalışmayacağı için ne seçtiğini anlayabiliyorum
            if (myBitmap == null) {

                try {
                    // kullanıcının galeriden seçtiği URI adresini biliyorum
                    // adresten o resmi bitmap olarak ele almak için bu metotu kullandım
                    myBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uris[0]);

                    // For Example Bitmap ARGB_8888 Every pixel contains alpha, red, green, blue channel. And than every changel size 8 bit.
                    // Width = 50px Height = 50px Format = ARGB_8888 Size = 50 x 50 x 32bit(4byte) = 2500 x 4 = 10000 bytes
                    // Bu yüzden bitmap çok dafa fazla boyut kaplar
                    Log.d("serkan", "ORİJİNAL RESMİ BOYUTU: " + ((double) myBitmap.getByteCount()) / MEGABAYT);
                }
                catch (IOException e) {

                    e.printStackTrace();
                }

            }
                // resmi 1 ve 0 lara çeviriyorum
                byte[] resimBytes = null;

                // aşama aşama sıkıştırma işlemi burada gerçekleşecek
                for (int i = 1; i <= 10; i++) {

                    resimBytes = convertBitmaptoByte(myBitmap, 100 / i);

                    // ben burada Toast mesaj gösterirsem hata alırım çünkü main thread üzerinde değilim
                    // bu metotu çalıştırdığımda onProgressUpdate tetikleniyor ve ben burada Toast gibi MainThread işlemlerini gösterebilirim çünkü MainThread ile bir köprü sağlıyor
                    publishProgress(((double) resimBytes.length));
                }

                // bitmap olarak verdim, bytearray olarak döndürüyorum
            return resimBytes;
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);

            // Toast.makeText(KullaniciAyarlariActivity.this, "Suanki Byte: " + values[0] / MEGABAYT + " MB", Toast.LENGTH_SHORT).show();

        }

        private byte[] convertBitmaptoByte(Bitmap myBitmap, int i) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            myBitmap.compress(Bitmap.CompressFormat.JPEG, i, stream);

            return stream.toByteArray();
        }

        // Bu metot main thread üzerinde çalışıyor
        // doInBackground işlemi bittikten sonra çalışan metot
        @Override
        protected void onPostExecute(byte[] bytes) {

            super.onPostExecute(bytes);
            uploadResimtoFirebase(bytes);
        }
    }

    private void uploadResimtoFirebase(byte[] result) {

        progressGoster();

        // daha önce oluşturulmamış ise oluşturur
        // dosya yolunu burada oluşturursun son oluşturulan resmin adı olur
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference resimEklenecekYer = storageReference.child("images/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profil_resim");

        // gönderme görevini Firebase ile burada başlatıyorum
        UploadTask uploadGorevi = resimEklenecekYer.putBytes(result);

        // yüklenen url adresini burada alıp daha sonrasında database'e yükledim
        uploadGorevi.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (!task.isSuccessful()) {

                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return resimEklenecekYer.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri downloadUri = task.getResult();

                    Toast.makeText(KullaniciAyarlariActivity.this, "Resmin yolu: " + downloadUri.toString(), Toast.LENGTH_SHORT).show();

                    FirebaseDatabase.getInstance().getReference()
                            .child("kullanici")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("profil_resmi")
                            .setValue(downloadUri.toString());

                    Toast.makeText(KullaniciAyarlariActivity.this, "Değişiklikler Yapıldı", Toast.LENGTH_LONG).show();
                }
                else {

                    Toast.makeText(KullaniciAyarlariActivity.this, "Resim yüklenirken bir hata oluştu..", Toast.LENGTH_SHORT).show();
                }

                progressGizle();
            }
        });
    }

    private void progressGoster() {

        pbPicture.setVisibility(View.VISIBLE);
    }

    private void progressGizle() {

        pbPicture.setVisibility(View.INVISIBLE);
    }
}
