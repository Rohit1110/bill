package util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Rohit on 11/27/2017.
 */

public class Utility {
   /* public  FirebaseFirestore db = FirebaseFirestore.getInstance();*/

    public static void createAlert(Context context, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static void saveFile(Context context, Bitmap b, String picName) throws IOException {
        FileOutputStream fos;

        fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
        b.compress(Bitmap.CompressFormat.PNG, 100, fos);

        fos.close();

    }
//Internet Connection Check

    public static  boolean isInternetOn(Context ctx) {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {


            return false;
        }
        return false;
    }


    public static Bitmap loadBitmap(Context context, String picName){
        Bitmap b = null;
        FileInputStream fis=null;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
        }
        catch (FileNotFoundException e) {
            Log.d("tag", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("tag", "io exception");
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

}
