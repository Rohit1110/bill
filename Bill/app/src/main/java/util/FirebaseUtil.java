package util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Admin on 25/05/2018.
 */

public class FirebaseUtil {

    public static String getPhone() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        }
        //return  null;
        //return "+919923283604";
        return "9423040642";
        //return "+919623736773";
        //return "+919028928867";
        //return "+919423040642";
        //return "+919657881695";
        //return "1234567123";
        //return "1010111011";//"1010111010";
        //return "+919657881695"; //H
        //return "+919552521152"; //K
        //return "+918408918874"; //MDP
        //return "1112223330";
        //return "+919881128985";//GK
        //return "+917016397176";//Lalit
        //return "+919921239207"; //RC
        //return "+919881411400";//NJ
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static String getToken() {
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            System.out.println("Got the token => " + token);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


