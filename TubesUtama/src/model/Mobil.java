package model;
public class Mobil extends Kendaraan {
    private int jumlahPintu;

    public Mobil(String platNomor, double hargaSewaPerHari, int jumlahPintu) {
        super(platNomor, hargaSewaPerHari, "Mobil");
        this.jumlahPintu = jumlahPintu;
    }

    public int getJumlahPintu() {
        return jumlahPintu;
    }

    public void setJumlahPintu(int jumlahPintu) {
        this.jumlahPintu = jumlahPintu;
    }

    @Override
    public String dapatkanAtributKhusus() {
        return jumlahPintu + " pintu";
    }
}