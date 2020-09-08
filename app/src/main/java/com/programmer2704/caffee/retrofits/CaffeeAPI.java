package com.programmer2704.caffee.retrofits;

import com.programmer2704.caffee.models.CheckUserResponse;
import com.programmer2704.caffee.models.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

//10:00:04 1 2800 todo 25 JULI 2019 Kam
public interface CaffeeAPI {
    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkUserExists(@Field("phone") String phone);
    
    @FormUrlEncoded
    @POST("register.php")
    Call<User> registerNewUser(
            @Field("phone") String phone, 
            @Field("name") String name, 
            @Field("address") String address, 
            @Field("birthdate") String birthdate
    );

    //14:06:06  todo 15 AGUSTUS 2019 Kam 03 3 auto login and get user info 0746
    @FormUrlEncoded
    @POST("getuser.php")
    Call<User> getUserInformation(@Field("phone") String phone);
}
