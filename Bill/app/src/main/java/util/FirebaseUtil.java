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
        return  null;
        //return "+919923283604";
        //return "9423040642";
        //return "+919623736773";
        //return "+919028928867";
        //return "1122334455";
        //return "+919923283604";
        //return "1234567123";
        //return "1911191119";
        //return "+911090901025";
        //return "+919623490889";
        //return "1112223332";
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}


