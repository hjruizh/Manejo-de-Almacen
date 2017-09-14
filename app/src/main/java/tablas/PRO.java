package tablas;

/**
 * Created by extre_000 on 9/2/2017.
 */
public class PRO {
    private int pk;
    private String codigo;
    private String nombre;
    private String saldoD;
    private String saldoB;
    private String email;
    private String dviFk;
    private String pagado;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSaldoD() {
        return saldoD;
    }

    public void setSaldoD(String saldo) {
        this.saldoD = saldo;
    }

    public String getSaldoB() {
        return saldoB;
    }

    public void setSaldoB(String saldo) {
        this.saldoB = saldo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDviFk() {
        return dviFk;
    }

    public void setDviFk(String dviFk) {
        this.dviFk = dviFk;
    }

    public String getPagado() {
        return pagado;
    }

    public void setPagado(String pagado) {
        this.pagado = pagado;
    }
}
