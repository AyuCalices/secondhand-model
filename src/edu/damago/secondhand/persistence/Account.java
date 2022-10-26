package edu.damago.secondhand.persistence;

public class Account {
    private char[] iban;
    private char[] bic;

    protected Account() {}

    public char[] getIban() {
        return iban;
    }

    public void setIban(char[] iban) {
        this.iban = iban;
    }

    public char[] getBic() {
        return bic;
    }

    public void setBic(char[] bic) {
        this.bic = bic;
    }
}
