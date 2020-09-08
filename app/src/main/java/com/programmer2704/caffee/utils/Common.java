package com.programmer2704.caffee.utils;

import com.programmer2704.caffee.models.User;
import com.programmer2704.caffee.retrofits.CaffeeAPI;
import com.programmer2704.caffee.retrofits.RetrofitClient;

//11:05:53 3027 todo 25 JULI 2019 Kam
public class Common {
    //baseUrl must end in /

    //todo when testing in emulator, use 10.0.2.2 as "localhost"
    private static final String BASE_URL = "http://10.0.2.2/caffee/";

    //todo when testing with phone, use your laptop IP not localhost or 127.0.0.1
//    private static final String BASE_URL = "http://192.168.100.25/caffee/";

    //14:11:08  todo 15 AGUSTUS 2019 Kam 3 0836
    public static User currentUser = null;

    public static CaffeeAPI getApi(){
        return RetrofitClient.getClient(BASE_URL).create(CaffeeAPI.class);
    }
}
