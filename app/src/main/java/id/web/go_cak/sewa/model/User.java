package id.web.go_cak.sewa.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("ID")
    public String ID;

    @SerializedName("nama")
    public String nama;

    @SerializedName("email")
    public String email;

    @SerializedName("telp")
    public String telp;

    @SerializedName("code")
    public String code;

    @SerializedName("result")
    public String result;

}