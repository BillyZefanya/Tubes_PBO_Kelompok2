public class MainArrayIndex {
    public static void main(String[] args) {
        String[] mahasiswa = {"Andi", "Budi", "Citra", "Dina", "Eka"};

        try {
            System.out.println("Mahasiswa: " + mahasiswa[9]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Peringkat yang dicari tidak tersedia.");
        }
    }
}