package util;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Admin on 25/05/2018.
 */

public class FirebaseUtil {

    public static String getPhone() {
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        //return "9923283604";
        //return "9423040642";
    }

}


