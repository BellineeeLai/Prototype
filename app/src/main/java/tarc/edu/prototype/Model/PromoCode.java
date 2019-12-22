package tarc.edu.prototype.Model;

public class PromoCode {
    private String code;
    private double value;

    public PromoCode() {
    }

    public PromoCode(String code, double value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
