package com.programmer2704.caffee;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.programmer2704.caffee.models.CheckUserResponse;
import com.programmer2704.caffee.models.User;
import com.programmer2704.caffee.retrofits.CaffeeAPI;
import com.programmer2704.caffee.utils.Common;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterA extends AppCompatActivity {

    CaffeeAPI mService;
    private static final int REQUEST_CODE = 1000;
    MaterialEditText editName, editAddress, editBirthdate;
    String numberIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_register);

        mService = Common.getApi();

        numberIntent = getIntent().getStringExtra("phone");

        editName = findViewById(R.id.EDIT_NAME);
        editAddress = findViewById(R.id.EDIT_ADDRESS);
        editBirthdate = findViewById(R.id.EDIT_BIRTHDATE);
        Button btnRegister = findViewById(R.id.BTN_REGISTER);

        editBirthdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        //event
        btnRegister.setOnClickListener(view -> {
            //todo close dialog
//            alertDialog.create().dismiss();

            if (TextUtils.isEmpty(editName.getText().toString()) ||
                    TextUtils.isEmpty(editAddress.getText().toString()) ||
                    TextUtils.isEmpty(editBirthdate.getText().toString())) {
                Toast.makeText(RegisterA.this, "Please fill them", Toast.LENGTH_SHORT).show();
                return;
            }


            final AlertDialog waitingDialog = new SpotsDialog(RegisterA.this);
            waitingDialog.show();
            waitingDialog.setMessage("Please wait mservice..");

// get user phone and check exists on server
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {

                    mService.checkUserExists(account.getPhoneNumber().toString()).enqueue(new Callback<CheckUserResponse>() {
                        @Override
                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                            CheckUserResponse userResponse = response.body();

                            if (userResponse.isExists()) {
                                // todo if user already exists, just start new activity
                                Toast.makeText(RegisterA.this, "Selamat datang", Toast.LENGTH_SHORT).show();

                            } else {
                                // todo else, need register
                                mService.registerNewUser(
//                                        account.getPhoneNumber().toString(),
                                        account.getPhoneNumber().getPhoneNumber(),
                                        editName.getText().toString(),
                                        editAddress.getText().toString(),
                                        editBirthdate.getText().toString()).enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {

                                        User user = response.body();

                                        if (TextUtils.isEmpty(user.getError_msg())) {
                                            Toast.makeText(RegisterA.this, "User register successful", Toast.LENGTH_SHORT).show();
                                            //todo start activity
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                    }
                                });

                            }
                        }

                        @Override
                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                        }
                    });

                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.d("ERROR", accountKitError.getErrorType().getMessage());
                }
            });
        });
    }
}
