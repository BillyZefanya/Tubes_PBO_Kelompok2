package service;

import model.Transaksi;
import java.util.List;

public class LaporanService {

    public void tampilkanLaporanPendapatan(List<Transaksi> daftarTransaksi) {
        System.out.println("=========================================================================");
        System.out.println("                     LAPORAN RIWAYAT & PENDAPATAN                        ");
        System.out.println("=========================================================================");
        System.out.printf("%-15s | %-15s | %-12s | %-10s | %-15s\n", 
                "ID Transaksi", "Pelanggan", "Kendaraan", "Status", "Total Tagihan");
        System.out.println("-------------------------------------------------------------------------");

        double totalPendapatan = 0;

        if (daftarTransaksi == null || daftarTransaksi.isEmpty()) {
            System.out.println("Belum ada data riwayat transaksi di dalam sistem.");
        } else {
            for (Transaksi trx : daftarTransaksi) {
                String id = trx.getIdTransaksi();
                String namaPelanggan = trx.getPelanggan().getNamaLengkap();
                String platKendaraan = trx.getKendaraan().getPlatNomor();
                String status = trx.getStatus().name(); 
                double tagihan = trx.getTotalTagihanAkhir();

                System.out.printf("%-15s | %-15s | %-12s | %-10s | Rp %,.0f\n", 
                        id, namaPelanggan, platKendaraan, status, tagihan);

                if (trx.getStatus() == StatusTransaksi.SELESAI) {
                    totalPendapatan += tagihan;
                }
            }
        }
        
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("TOTAL PENDAPATAN (Hanya dari Transaksi Selesai) : Rp %,.0f\n", totalPendapatan);
        System.out.println("=========================================================================");
    }
}