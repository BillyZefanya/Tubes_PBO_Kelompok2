public class MainDataNull {

    public static void main(String[] args) {

        String nama = null;

        try {
            System.out.println(nama.toUpperCase());
        } catch (NullPointerException e) {
            System.out.println("Nama masih kosong!");
        }

    }

}