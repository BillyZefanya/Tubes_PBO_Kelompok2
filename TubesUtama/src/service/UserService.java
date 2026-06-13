package service;

import model.User;
import persistence.JsonUser;

import java.util.List;

public class UserService {
    private List<User> daftarUser;
    private int percobaanGagal;
    private static final int MAKS_PERCOBAAN = 3;

    public UserService() {
        JsonUser jsonUser = new JsonUser();
        this.daftarUser = jsonUser.muatDataUser();
        this.percobaanGagal = 0;
    }

    // Fungsi untuk melakukan validasi login
    // Me-return objek User jika berhasil, atau null jika gagal
    public User login(String username, String password) {
        if (isTerkunci()) {
            System.out.println("Akun terkunci karena gagal login 3 kali.");
            return null;
        }

        for (User user : daftarUser) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                // Reset percobaan gagal jika berhasil login
                percobaanGagal = 0;
                return user;
            }
        }

        // Jika tidak ketemu
        percobaanGagal++;
        int sisaPercobaan = MAKS_PERCOBAAN - percobaanGagal;
        System.out.println("Username atau password salah!");
        if (sisaPercobaan > 0) {
            System.out.println("Sisa percobaan: " + sisaPercobaan);
        } else {
            System.out.println("Akses ditolak! Anda telah gagal login 3 kali berturut-turut.");
        }
        
        return null;
    }

    public boolean isTerkunci() {
        return percobaanGagal >= MAKS_PERCOBAAN;
    }
}
