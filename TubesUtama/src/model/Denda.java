package model;

// Abstract class untuk polymorphism denda
public abstract class Denda {

    // Method yang wajib dioverride oleh subclass
    public abstract double hitungDenda(int jumlahHariTerlambat);

}