package cat.indiketa.degiro.model;

/**
 *
 * @author indiketa
 */
public class DClient {

    public long id;
    public long intAccount;
    public String clientRole;
    public String username;
    public String displayName;
    public String email;
    public FirstContact firstContact;
    public Address address;
    public String phoneNumber;
    public String cellphoneNumber;
    public String locale;
    public String language;
    public String culture;
    public BankAccount bankAccount;
    public String memberCode;
    public boolean isAllocationAvailable;
    public boolean isIskClient;
    public boolean isWithdrawalAvailable;
    public boolean isCollectivePortfolio;
    public boolean isAmClientActive;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIntAccount() {
        return intAccount;
    }

    public void setIntAccount(long intAccount) {
        this.intAccount = intAccount;
    }

    public String getClientRole() {
        return clientRole;
    }

    public void setClientRole(String clientRole) {
        this.clientRole = clientRole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FirstContact getFirstContact() {
        return firstContact;
    }

    public void setFirstContact(FirstContact firstContact) {
        this.firstContact = firstContact;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public boolean isIsAllocationAvailable() {
        return isAllocationAvailable;
    }

    public void setIsAllocationAvailable(boolean isAllocationAvailable) {
        this.isAllocationAvailable = isAllocationAvailable;
    }

    public boolean isIsIskClient() {
        return isIskClient;
    }

    public void setIsIskClient(boolean isIskClient) {
        this.isIskClient = isIskClient;
    }

    public boolean isIsWithdrawalAvailable() {
        return isWithdrawalAvailable;
    }

    public void setIsWithdrawalAvailable(boolean isWithdrawalAvailable) {
        this.isWithdrawalAvailable = isWithdrawalAvailable;
    }

    public boolean isIsCollectivePortfolio() {
        return isCollectivePortfolio;
    }

    public void setIsCollectivePortfolio(boolean isCollectivePortfolio) {
        this.isCollectivePortfolio = isCollectivePortfolio;
    }

    public boolean isIsAmClientActive() {
        return isAmClientActive;
    }

    public void setIsAmClientActive(boolean isAmClientActive) {
        this.isAmClientActive = isAmClientActive;
    }

    public static class Address {

        public String streetAddress;
        public String streetAddressNumber;
        public String streetAddressExt;
        public String zip;
        public String city;
        public String country;
        public String postalCode;

        public String getStreetAddress() {
            return streetAddress;
        }

        public void setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
        }

        public String getStreetAddressNumber() {
            return streetAddressNumber;
        }

        public void setStreetAddressNumber(String streetAddressNumber) {
            this.streetAddressNumber = streetAddressNumber;
        }

        public String getStreetAddressExt() {
            return streetAddressExt;
        }

        public void setStreetAddressExt(String streetAddressExt) {
            this.streetAddressExt = streetAddressExt;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

    }

    public static class BankAccount {

        public String iban;
        public String bic;
        public String name;
        public String number;
        public String sortCode;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getSortCode() {
            return sortCode;
        }

        public void setSortCode(String sortCode) {
            this.sortCode = sortCode;
        }

    }

    public static class FirstContact {

        public String firstName;
        public String lastName;
        public String nationality;
        public String sofiNumber;
        public String gender;
        public String birthday;
        public String placeOfBirth;
        public String countryOfBirth;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getSofiNumber() {
            return sofiNumber;
        }

        public void setSofiNumber(String sofiNumber) {
            this.sofiNumber = sofiNumber;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getPlaceOfBirth() {
            return placeOfBirth;
        }

        public void setPlaceOfBirth(String placeOfBirth) {
            this.placeOfBirth = placeOfBirth;
        }

        public String getCountryOfBirth() {
            return countryOfBirth;
        }

        public void setCountryOfBirth(String countryOfBirth) {
            this.countryOfBirth = countryOfBirth;
        }

    }

}
