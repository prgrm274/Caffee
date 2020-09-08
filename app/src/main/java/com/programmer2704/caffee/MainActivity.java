package com.programmer2704.caffee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.programmer2704.caffee.databinding.ActivityMainBinding;
import com.programmer2704.caffee.models.CheckUserResponse;
import com.programmer2704.caffee.models.User;
import com.programmer2704.caffee.retrofits.CaffeeAPI;
import com.programmer2704.caffee.utils.Common;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding b;
    private static final int REQUEST_CODE = 1000;
    CaffeeAPI mService;

    private static final String EMAIL = "email";
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        View view = b.getRoot();
        setContentView(view);

        mService = Common.getApi();

        b.btnContinue.setOnClickListener(v -> startLoginPage(LoginType.PHONE));

        //14:24:41 15 AGUSTUS 2019 Kam 3 1112
        if (AccountKit.getCurrentAccessToken() != null) {
            //auto login
            final AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
            alertDialog.show();
            alertDialog.setMessage("Please wait..");

            // get user phone and check exists on server
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                public void onSuccess(final Account account) {

                    mService.checkUserExists(
                            account.getPhoneNumber().toString()).enqueue(
                                    new Callback<CheckUserResponse>() {
                        public void onResponse(Call<CheckUserResponse> call,
                                               Response<CheckUserResponse> response) {
                            CheckUserResponse userResponse = response.body();

                            if (userResponse.isExists()) {
                                mService.getUserInformation(account.getPhoneNumber().toString()).enqueue(new Callback<User>() {
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        // todo if user already exists, just start new activity
                                        alertDialog.dismiss();

                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        finish();

                                        toast("Selamat datang, check user exists success");

                                    }

                                    public void onFailure(Call<User> call, Throwable t) {
                                        toast(t.getMessage());
                                    }
                                });

                            } else {
                                // todo else, need register
                                alertDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Daftar dulu", Toast.LENGTH_SHORT).show();
                                showRegisterDialog(account.getPhoneNumber().toString());

                                b.linearMainRegister.setVisibility(View.VISIBLE);
                                b.btnContinue.setVisibility(View.GONE);

//                                        showRegister(account.getPhoneNumber().getPhoneNumber());
                            }
                        }

                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                        }
                    });

                }

                public void onError(AccountKitError accountKitError) {
                    Log.d("ERROR", accountKitError.getErrorType().getMessage());
                }
            });
        }


        //11:40:48 stlh daftar dan atur fesbuk dev todo 25 JULI 2019 Kam
        //cuma gunakan sekali untuk register keyhash app, setelah itu hapus metode ini ga apa
        //printKeyHash();

        callbackManager = CallbackManager.Factory.create();
        b.loginLayoutId.loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // Callback registration
        b.loginLayoutId.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }


    private void startLoginPage(LoginType loginType) {
        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder
                builder = new AccountKitConfiguration
                .AccountKitConfigurationBuilder(
                loginType,
                AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                builder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {

            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (result.getError() != null) {
                toast(result.getError().getErrorType().getMessage());

            } else if (result.wasCancelled()) {
                toast("Cancel");

            } else {

                if (result.getAccessToken() != null) {

                    final AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
                    alertDialog.show();
                    alertDialog.setMessage("Please wait.." + result.getAccessToken().getAccountId());

                    // get user phone and check exists on server
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        public void onSuccess(final Account account) {

                            mService.checkUserExists(account.getPhoneNumber().toString()).enqueue(new Callback<CheckUserResponse>() {
                                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                    CheckUserResponse userResponse = response.body();

                                    if (userResponse.isExists()) {
                                        //14:21:17  todo 15 AGUSTUS 2019 Kam 3 1021
                                        mService.getUserInformation(account.getPhoneNumber().toString()).enqueue(new Callback<User>() {
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                // todo if user already exists, just start new activity
                                                alertDialog.dismiss();

                                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                finish();

                                                toast("Selamat datang, check user exists success");

                                            }

                                            public void onFailure(Call<User> call, Throwable t) {
                                                toast(t.getMessage());
                                            }
                                        });

                                    } else {
                                        // todo else, need register
                                        alertDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Daftar dulu", Toast.LENGTH_SHORT).show();
                                        showRegisterDialog(account.getPhoneNumber().toString());

                                        b.linearMainRegister.setVisibility(View.VISIBLE);
                                        b.btnContinue.setVisibility(View.GONE);

//                                        showRegister(account.getPhoneNumber().getPhoneNumber());
                                    }
                                }

                                public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                }
                            });
                        }

                        public void onError(AccountKitError accountKitError) {
                            Log.d("ERROR", accountKitError.getErrorType().getMessage());
                        }
                    });


                    //10:50:41 ini untuk tes login account kit, belum nyampek phpnya todo 26 JULI 2019 Jum
                    //ini bisa, berarti masalahnya di dialog atau php
                    /*Intent intent = new Intent(this, RegisterA.class);
                    intent.putExtra("phone", account.getPhoneNumber().getPhoneNumber());
                    startActivity(intent);*/

                } else {
                    toast(String.format("Success: %s..", result.getAuthorizationCode().substring(0, 10)));
                }

            }
        }
    }


    private void showRegister(final String phone) {
        final MaterialEditText editName = findViewById(R.id.EDIT_NAME);
        final MaterialEditText editAddress = findViewById(R.id.EDIT_ADDRESS);
        final MaterialEditText editBirthdate = findViewById(R.id.EDIT_BIRTHDATE);
        Button btnRegister = findViewById(R.id.BTN_REGISTER);

        editBirthdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        //event
        btnRegister.setOnClickListener(view -> {
            if (TextUtils.isEmpty(editName.getText().toString()) ||
                    TextUtils.isEmpty(editAddress.getText().toString()) ||
                    TextUtils.isEmpty(editBirthdate.getText().toString())) {
                Toast.makeText(MainActivity.this, "Please fill them", Toast.LENGTH_SHORT).show();
                return;
            }

            final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
            waitingDialog.show();
            waitingDialog.setMessage("Please wait mservice..");

            mService.registerNewUser(phone, editName.getText().toString(), editAddress.getText().toString(), editBirthdate.getText().toString()).enqueue(new Callback<User>() {
                public void onResponse(Call<User> call, Response<User> response) {
                    waitingDialog.dismiss();

                    User user = response.body();

                    if (TextUtils.isEmpty(user.getError_msg())) {
                        Toast.makeText(MainActivity.this, "User register successful", Toast.LENGTH_SHORT).show();
                        //todo start activity
                    }
                }

                public void onFailure(Call<User> call, Throwable t) {
                    waitingDialog.dismiss();
                }
            });
        });

    }

    private void showRegisterDialog(final String phone) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("REGISTER");

        LayoutInflater inflater = this.getLayoutInflater();
        View registerView = inflater.inflate(R.layout.a_register, null);

        final MaterialEditText editName = registerView.findViewById(R.id.EDIT_NAME);
        final MaterialEditText editAddress = registerView.findViewById(R.id.EDIT_ADDRESS);
        final MaterialEditText editBirthdate = registerView.findViewById(R.id.EDIT_BIRTHDATE);
        Button btnRegister = registerView.findViewById(R.id.BTN_REGISTER);

        editBirthdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        alertDialog.setView(registerView);
        alertDialog.show();

        //event
        btnRegister.setOnClickListener(view -> {
            //todo close dialog
            alertDialog.create().dismiss();

            if (TextUtils.isEmpty(editName.getText().toString()) ||
                    TextUtils.isEmpty(editAddress.getText().toString()) ||
                    TextUtils.isEmpty(editBirthdate.getText().toString())) {
                Toast.makeText(MainActivity.this, "Please fill them", Toast.LENGTH_SHORT).show();
                return;
            }


            final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
            waitingDialog.show();
            waitingDialog.setMessage("Please wait mservice..");

            mService.registerNewUser(phone, editName.getText().toString(), editAddress.getText().toString(), editBirthdate.getText().toString()).enqueue(new Callback<User>() {
                public void onResponse(Call<User> call, Response<User> response) {
                    waitingDialog.dismiss();

                    User user = response.body();

                    if (TextUtils.isEmpty(user.getError_msg())) {
                        Toast.makeText(MainActivity.this, "User register successful", Toast.LENGTH_SHORT).show();

                        //14:14:03  todo 15 AGUSTUS 2019 Kam 3 0906
                        Common.currentUser = response.body();

                        //todo start activity
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    }
                }

                public void onFailure(Call<User> call, Throwable t) {
                    waitingDialog.dismiss();
                }
            });
        });

    }


    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.programmer2704.caffee", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
