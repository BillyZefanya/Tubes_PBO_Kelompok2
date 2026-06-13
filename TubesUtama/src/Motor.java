public class Motor extends Kendaraan {
    private String jenisTransmisi;

    public Motor(String platNomor, double hargaSewaPerHari, String jenisTransmisi) {
        super(platNomor, hargaSewaPerHari, "Motor");
        this.jenisTransmisi = jenisTransmisi;
    }

    public String getJenisTransmisi() {
        return jenisTransmisi;
    }

    public void setJenisTransmisi(String jenisTransmisi) {
        this.jenisTransmisi = jenisTransmisi;
    }

    @Override
    public String dapatkanAtributKhusus() {
        return jenisTransmisi;
    }
}