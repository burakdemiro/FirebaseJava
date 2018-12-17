package com.burakdemir.firebasejava.Model;

public class Kullanici {

    // bütün verileri String olarak tutucam gerekirse diğer veri türlerine dönüşüm yaparım
    // isimlendirme kurallarına uymadan yazdım çünkü bu verilerin aynısı firebase database'de olacak
    private String isim;
    private String telefon;
    private String profil_resmi;
    private String seviye;
    private String kullanici_id;

    public Kullanici() {
    }

    public Kullanici(String isim, String telefon, String profil_resmi, String seviye, String kullanici_id) {
        this.isim = isim;
        this.telefon = telefon;
        this.profil_resmi = profil_resmi;
        this.seviye = seviye;
        this.kullanici_id = kullanici_id;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getProfil_resmi() {
        return profil_resmi;
    }

    public void setProfil_resmi(String profil_resmi) {
        this.profil_resmi = profil_resmi;
    }

    public String getSeviye() {
        return seviye;
    }

    public void setSeviye(String seviye) {
        this.seviye = seviye;
    }

    public String getKullanici_id() {
        return kullanici_id;
    }

    public void setKullanici_id(String kullanici_id) {
        this.kullanici_id = kullanici_id;
    }
}
