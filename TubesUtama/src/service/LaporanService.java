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

            String idTransaksi =
                    transaksi.getIdTransaksi();

            String namaPelanggan =
                    transaksi.getPelanggan().getNama();

            String platNomor =
                    transaksi.getKendaraan().getPlatNomor();

            String status =
                    transaksi.getStatusTransaksi();

            double totalTagihan =
                    transaksi.getTotalBiayaSewa()
                            + transaksi.getTotalDenda();

            System.out.printf(
                    "%-15s | %-15s | %-12s | %-10s | Rp %,.0f\n",
                    idTransaksi,
                    namaPelanggan,
                    platNomor,
                    status,
                    totalTagihan);

            if (status.equalsIgnoreCase("SELESAI")) {

                totalPendapatan += totalTagihan;

            }
        }
    }

    System.out.println("-------------------------------------------------------------------------");

    System.out.printf(
            "TOTAL PENDAPATAN : Rp %,.0f\n",
            totalPendapatan);

    System.out.println("=========================================================================");
}

}
