package model;

public class Pelanggan {
    private String nomorKtp;
    private String nama;
    private String noTelepon;

    public Pelanggan(String nomorKtp, String nama, String noTelepon) {
        this.nomorKtp = nomorKtp;
        this.nama = nama;
        this.noTelepon = noTelepon;
    }

    public String getNomorKtp() { return nomorKtp; }
    public String getNama() { return nama; }
    public String getNoTelepon() { return noTelepon; }

    public void setNomorKtp(String nomorKtp) { this.nomorKtp = nomorKtp; }
    public void setNama(String nama) { this.nama = nama; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }
}