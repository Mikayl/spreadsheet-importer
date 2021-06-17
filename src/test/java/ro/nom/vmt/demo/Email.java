package ro.nom.vmt.demo;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */


public class Email {
    private String address;
    private String provider;
    private String tld;

    public Email(String email) {
        try {
            this.address = email.split("@")[0];
            this.provider = email.split("@")[1].split("\\.", 2)[0];
            this.tld = email.split("@")[1].split("\\.", 2)[1];
        } catch (Exception e) {
            throw new RuntimeException("Email value \"" + email + "\" does not respect the expected format");
        }
    }

    public Email(String address, String provider, String tld) {
        this.address = address;
        this.provider = provider;
        this.tld = tld;
    }


    @Override
    public String toString() {
        return address + "@" + provider + "." + tld;
    }

    public String getAddress() {
        return address;
    }

    public String getProvider() {
        return provider;
    }

    public String getTld() {
        return tld;
    }
}
