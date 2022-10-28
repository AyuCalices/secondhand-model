package edu.damago.secondhand.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Account {
    @Column(nullable = false, updatable = true)
    private String iban;
    @Column(nullable = false, updatable = true)
    private String bic;

    protected Account() {}

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }
}
