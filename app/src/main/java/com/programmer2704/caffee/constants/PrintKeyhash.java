package com.programmer2704.caffee.constants;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PrintKeyhash {
    public static void print(Context context){
        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(
                            "com.programmer2704.caffee",
                            PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d(TAG, "print: ");
                Log.d("KEYHASH", "print: "+ Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = PrintKeyhash.class.getSimpleName();
}
