package model;

// Denda khusus kendaraan motor
public class DendaMotor extends Denda {

    private static final double DENDA_PER_HARI = 20000;

    @Override
    public double hitungDenda(int jumlahHariTerlambat) {

        return jumlahHariTerlambat * DENDA_PER_HARI;

    }
}