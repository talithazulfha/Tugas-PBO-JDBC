import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    private static int lastFakturNumber = 0;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        boolean lanjutTransaksi = true;
        boolean lanjutLogin = true;

        System.out.println();
        while (lanjutLogin) {
            String username = "";
            String password = "";
            String captcha = "";
            String z = Captcha.generateCaptcha();
            try {
                System.out.print("Username\t: ");
                username = scanner.next();
                System.out.print("Password\t: ");
                password = scanner.next();
                System.out.println("Kode captcha\t: " + z);
                System.out.print("Captcha\t\t: ");
                captcha = scanner.next();
                if (Login.AdminLogin(username, password, captcha, z)) {
                    System.out.println("Login successful!");
                    lanjutTransaksi = true;
                } else {
                    System.out.println("Invalid username, password, or captcha. Please try again.");
                    lanjutTransaksi = false;
                }
            } catch (InputMismatchException e) {
                System.out.println("Periksa kembali Username, Password, dan Captcha");
                scanner.next();
            }
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "")) {
                while (lanjutTransaksi) {
                    Integer noFaktur = ++lastFakturNumber;
                    System.out.println();
                    System.out.println("No. Faktur\t: " + noFaktur);

                    System.out.print("Nama Pelanggan\t: ");
                    String namaPelanggan = scanner.next();

                    System.out.print("No HP\t\t: ");
                    String noHp = scanner.next();

                    System.out.print("Alamat\t\t: ");
                    String alamat = scanner.next();

                    System.out.print("Kode Barang\t: ");
                    String kodeBarang = scanner.next();

                    System.out.print("Nama Barang\t: ");
                    String namaBarang = scanner.next();

                    boolean inputHargaValid = false;
                    double hargaBarang = 0;
                    while (!inputHargaValid) {
                        try {
                            System.out.print("Harga Barang\t: ");
                            hargaBarang = scanner.nextDouble();
                            inputHargaValid = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Input harga barang tidak valid. Silakan coba lagi.");
                            scanner.next();
                        }
                    }

                    int jumlahBarang = 0;
                    boolean inputJumlahValid = false;
                    while (!inputJumlahValid) {
                        try {
                            System.out.print("Jumlah Barang\t: ");
                            jumlahBarang = scanner.nextInt();
                            if (jumlahBarang <= 0) {
                                throw new ArithmeticException("Jumlah barang harus lebih dari 0.");
                            }
                            inputJumlahValid = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Input jumlah barang tidak valid. Silakan coba lagi.");
                            scanner.next();
                        } catch (ArithmeticException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }

                    System.out.print("Kasir\t\t: ");
                    String kasir = scanner.next();
                    Transaksi transaksi = new Transaksi(noFaktur, namaPelanggan, noHp, alamat, kodeBarang, namaBarang,
                            hargaBarang, jumlahBarang, kasir);
                    System.out.println();
                    transaksi.tampilkanDetailTransaksi();

                    System.out.println();
                    System.out.print("Lanjut ke transaksi berikutnya? (ya/tidak): ");

                    String jawaban = scanner.next().toLowerCase();
                    lanjutTransaksi = jawaban.equals("ya");

                    try { // Insert the transaction into the database
                        insertTransaksiIntoDatabase(connection, transaksi);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error connecting to the database. Transaction not saved.");
                    }
                }
                System.out.println("Terima kasih telah menggunakan program ini.");
                scanner.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error connecting to the database. Transaction not saved.");
            }
        }
    }

    private static void insertTransaksiIntoDatabase(Connection connection, Transaksi transaksi) throws SQLException {
        Timestamp waktu = Timestamp.from(Instant.now());
        String query = "INSERT INTO minimarket (waktu, noFaktur, namaPelanggan, noHp, alamat, kodeBarang, namaBarang, hargaBarang, jumlahBarang, kasir) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setTimestamp(1, waktu);
            preparedStatement.setInt(2, transaksi.getNoFaktur());
            preparedStatement.setString(3, transaksi.getNamaPelanggan());
            preparedStatement.setString(4, transaksi.getNoHp());
            preparedStatement.setString(5, transaksi.getAlamat());
            preparedStatement.setString(6, transaksi.getKodeBarang());
            preparedStatement.setString(7, transaksi.getNamaBarang());
            preparedStatement.setDouble(8, transaksi.getHargaBarang());
            preparedStatement.setInt(9, transaksi.getJumlahBarang());
            preparedStatement.setString(10, transaksi.getKasir());

            preparedStatement.executeUpdate();
        }
    }
}