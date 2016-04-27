package id.web.go_cak.sewa.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fachrifebrian on 4/27/16.
 */
public class Distance {
    @SerializedName("lokasi")
    public List<Lokasi> lokasi = new ArrayList<>();

    public class Lokasi {
        @SerializedName("distance")
        public String distance;

        @SerializedName("distanceValue")
        public Integer distanceValue;

        @SerializedName("time")
        public String time;

        @SerializedName("timeValue")
        public Integer timeValue;

        @SerializedName("payment")
        public Integer payment;

        @SerializedName("showpayment")
        public String showpayment;

        @SerializedName("code")
        public String code;

        @SerializedName("result")
        public String result;

    }

}
