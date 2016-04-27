package id.web.go_cak.sewa.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fachrifebrian on 4/27/16.
 */
public class Order {
    @SerializedName("jemput")
    public List<Jemput> jemput = new ArrayList<Jemput>();

    public class Jemput {
        @SerializedName("code")
        public String code;

        @SerializedName("result")
        public String result;

    }

}
