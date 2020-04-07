package cat.indiketa.degiro.model;

import lombok.Data;

/**
 *
 * @author indiketa
 */
@Data
public class DClient implements IValidable {

    private long id;
    private long intAccount;
    private String clientRole;
    private String username;
    private String displayName;
    private String email;
    private String effectiveClientRole;
    private String contractType;
    private boolean canUpgrade;
    private FirstContact firstContact;
    private Address address;
    private String phoneNumber;
    private String cellphoneNumber;
    private String locale;
    private String language;
    private String culture;
    private BankAccount bankAccount;
    private String memberCode;
    private boolean isAllocationAvailable;
    private boolean isIskClient;
    private boolean isWithdrawalAvailable;
    private boolean isCollectivePortfolio;
    private boolean isAmClientActive;

    @Override
    public boolean isInvalid() {
        return id == 0 || intAccount == 0 || clientRole == null || username == null || email == null;
    }

    @Data
    public static class Address {

        public String streetAddress;
        public String streetAddressNumber;
        public String streetAddressExt;
        public String zip;
        public String city;
        public String country;
        public String postalCode;


    }

    @Data
    public static class BankAccount {

        public long bankAccountId;
        public String status;
        public String iban;
        public String bic;
        public String name;
        public String number;
        public String sortCode;
    }

    @Data
    public static class FirstContact {

        public String firstName;
        public String lastName;
        public String displayName;
        public String dateOfBirth;
        public String nationality;
        public String sofiNumber;
        public String gender;
        public String birthday;
        public String placeOfBirth;
        public String countryOfBirth;

    }

}
