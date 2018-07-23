package util;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Admin on 25/05/2018.
 */

public class FirebaseUtil {

    public static String getPhone() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        }
        //return  null;
        //return "9923283604";
        //return "9423040642";
        return "+919623736773";
    }

}


