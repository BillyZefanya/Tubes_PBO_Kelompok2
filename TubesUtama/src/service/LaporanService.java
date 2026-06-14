package service;

import model.Transaksi;
import java.util.List;

public class LaporanService {

    public void tampilkanLaporanPendapatan(List<Transaksi> daftarTransaksi) {
        System.out.println("=========================================================================");
        System.out.println("                     LAPORAN RIWAYAT & PENDAPATAN                        ");
        System.out.println("=========================================================================");
        System.out.printf(
                "%-15s | %-15s | %-12s | %-10s | %-15s\n",
                "ID Transaksi",
                "Pelanggan",
                "Kendaraan",
                "Status",
                "Total Tagihan");
        System.out.println("-------------------------------------------------------------------------");

        double totalPendapatan = 0;

        if (daftarTransaksi == null || daftarTransaksi.isEmpty()) {
            System.out.println("Belum ada data riwayat transaksi di dalam sistem.");
        } else {
            for (Transaksi transaksi : daftarTransaksi) {
                String idTransaksi = transaksi.getIdTransaksi();
                String namaPelanggan = transaksi.getPelanggan().getNama();
                String platNomor = transaksi.getKendaraan().getPlatNomor();
                String status = transaksi.getStatusTransaksi();
                double totalTagihan = transaksi.getTotalBiayaSewa() + transaksi.getTotalDenda();

                // Tampilkan "-" untuk transaksi yang masih BERJALAN, sesuai contoh UI di soal
                String totalTagihanStr = status.equalsIgnoreCase("SELESAI")
                        ? String.format("Rp %,.0f", totalTagihan)
                        : "-";

                System.out.printf(
                        "%-15s | %-15s | %-12s | %-10s | %-15s\n",
                        idTransaksi,
                        namaPelanggan,
                        platNomor,
                        status,
                        totalTagihanStr);

                if (status.equalsIgnoreCase("SELESAI")) {
                    totalPendapatan += totalTagihan;
                }
            }
        }

        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("TOTAL PENDAPATAN : Rp %,.0f\n", totalPendapatan);
        System.out.println("=========================================================================");
    }
}