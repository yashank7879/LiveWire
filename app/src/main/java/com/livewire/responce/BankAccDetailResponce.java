package com.livewire.responce;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mindiii on 11/21/18.
 */

public class BankAccDetailResponce {
   /* {
        "status": "success",
            "message": "Ok",
            "bank_detail": {
        "bankAccId": "1",
                "user_id": "4",
                "firstName": "anurag",
                "lastName": "jain",
                "accountNumber": "000123456789",
                "branch_code": "110000000",
                "accountType": "",
                "status": "0",
                "crd": "2019-04-02 05:33:10",
                "upd": "0000-00-00 00:00:00"
    }
    }*/

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
         * bankAccId : 1
         * user_id : 4
         * firstName : anurag
         * lastName : jain
         * accountNumber : 000123456789
         * branch_code : 110000000
         * accountType :
         * status : 0
         * crd : 2019-04-02 05:33:10
         * upd : 0000-00-00 00:00:00
         */

        @SerializedName("bankAccId")
        private String bankAccIdX;
        @SerializedName("user_id")
        private String user_idX;
        @SerializedName("firstName")
        private String firstNameX;
        @SerializedName("lastName")
        private String lastNameX;
        @SerializedName("accountNumber")
        private String accountNumberX;
        private String branch_code;
        @SerializedName("accountType")
        private String accountTypeX;
        @SerializedName("status")
        private String statusX;
        @SerializedName("crd")
        private String crdX;
        @SerializedName("upd")
        private String updX;

        public String getBankAccIdX() {
            return bankAccIdX;
        }

        public void setBankAccIdX(String bankAccIdX) {
            this.bankAccIdX = bankAccIdX;
        }

        public String getUser_idX() {
            return user_idX;
        }

        public void setUser_idX(String user_idX) {
            this.user_idX = user_idX;
        }

        public String getFirstNameX() {
            return firstNameX;
        }

        public void setFirstNameX(String firstNameX) {
            this.firstNameX = firstNameX;
        }

        public String getLastNameX() {
            return lastNameX;
        }

        public void setLastNameX(String lastNameX) {
            this.lastNameX = lastNameX;
        }

        public String getAccountNumberX() {
            return accountNumberX;
        }

        public void setAccountNumberX(String accountNumberX) {
            this.accountNumberX = accountNumberX;
        }

        public String getBranch_code() {
            return branch_code;
        }

        public void setBranch_code(String branch_code) {
            this.branch_code = branch_code;
        }

        public String getAccountTypeX() {
            return accountTypeX;
        }

        public void setAccountTypeX(String accountTypeX) {
            this.accountTypeX = accountTypeX;
        }

        public String getStatusX() {
            return statusX;
        }

        public void setStatusX(String statusX) {
            this.statusX = statusX;
        }

        public String getCrdX() {
            return crdX;
        }

        public void setCrdX(String crdX) {
            this.crdX = crdX;
        }

        public String getUpdX() {
            return updX;
        }

        public void setUpdX(String updX) {
            this.updX = updX;
        }
    }
}
