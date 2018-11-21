package com.livewire.responce;

/**
 * Created by mindiii on 11/21/18.
 */

public class BankAccDetailResponce {
    /**
     * status : success
     * message : Ok
     * bank_detail : {"bankAccId":"2","user_id":"146","firstName":"yash","lastName":"ratho","dob":"2000-11-21","routingNumber":"110000000","accountNumber":"000123456789","postalCode":"10001","ssnLast":"0000","accountId":"acct_1DYo67FPTRUx22n5","accountType":"Joint Account","status":"0","crd":"2018-11-21 05:17:19","upd":"2018-11-21 05:18:01"}
     */

    private String status;
    private String message;
    private BankDetailBean bank_detail;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BankDetailBean getBank_detail() {
        return bank_detail;
    }

    public void setBank_detail(BankDetailBean bank_detail) {
        this.bank_detail = bank_detail;
    }

    public static class BankDetailBean {
        /**
         * bankAccId : 2
         * user_id : 146
         * firstName : yash
         * lastName : ratho
         * dob : 2000-11-21
         * routingNumber : 110000000
         * accountNumber : 000123456789
         * postalCode : 10001
         * ssnLast : 0000
         * accountId : acct_1DYo67FPTRUx22n5
         * accountType : Joint Account
         * status : 0
         * crd : 2018-11-21 05:17:19
         * upd : 2018-11-21 05:18:01
         */

        private String bankAccId;
        private String user_id;
        private String firstName;
        private String lastName;
        private String dob;
        private String routingNumber;
        private String accountNumber;
        private String postalCode;
        private String ssnLast;
        private String accountId;
        private String accountType;
        private String status;
        private String crd;
        private String upd;

        public String getBankAccId() {
            return bankAccId;
        }

        public void setBankAccId(String bankAccId) {
            this.bankAccId = bankAccId;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

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

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getRoutingNumber() {
            return routingNumber;
        }

        public void setRoutingNumber(String routingNumber) {
            this.routingNumber = routingNumber;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getSsnLast() {
            return ssnLast;
        }

        public void setSsnLast(String ssnLast) {
            this.ssnLast = ssnLast;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCrd() {
            return crd;
        }

        public void setCrd(String crd) {
            this.crd = crd;
        }

        public String getUpd() {
            return upd;
        }

        public void setUpd(String upd) {
            this.upd = upd;
        }
    }
}
