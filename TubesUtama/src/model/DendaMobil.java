package model;

// Denda khusus kendaraan mobil
public class DendaMobil extends Denda {

    private static final double DENDA_PER_HARI = 50000;

    @Override
    public double hitungDenda(int jumlahHariTerlambat) {

        return jumlahHariTerlambat * DENDA_PER_HARI;

    }
}