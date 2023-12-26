public class Barang {
    public String namaBarang;
    public double hargaBarang;

    public Barang(String namaBarang, double hargaBarang) {
        this.namaBarang = namaBarang;
        this.hargaBarang = hargaBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public double getHargaBarang() {
        return hargaBarang;
    }

    public void setHargaBarang(double hargaBarang) {
        this.hargaBarang = hargaBarang;
    }
}