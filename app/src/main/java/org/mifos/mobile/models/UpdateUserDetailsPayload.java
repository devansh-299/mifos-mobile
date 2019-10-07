package org.mifos.mobile.models;

import com.google.gson.annotations.SerializedName;

public class UpdateUserDetailsPayload {

    @SerializedName("officeName")
    String office;

    @SerializedName("contactNumber")
    String contactNumber;

    public String getOffice (){
        return office;
    }
    public String getContactNumber(){
        return contactNumber;
    }

    public void setOffice(String newOffice){
        office = newOffice;

    }
    public void setContactNumber(String newContactNumber){
        contactNumber= newContactNumber;
    }

}
