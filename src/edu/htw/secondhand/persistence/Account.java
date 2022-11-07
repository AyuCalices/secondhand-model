package edu.htw.secondhand.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class Account {
    @NotNull @Size(max = 32)
    @Column(nullable = false, updatable = true, length = 32)
    private String iban;
    @NotNull @Size(max = 11)
    @Column(nullable = false, updatable = true, length = 11)
    private String bic;

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
