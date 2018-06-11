package util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillLocation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohit on 11/27/2017.
 */

public class Utility {
   /* public  FirebaseFirestore db = FirebaseFirestore.getInstance();*/

    public static final String USER_KEY = "billUser";
    public static final String COLOR_BLUE = "#00A6FF";

    public static void createAlert(Context context, String message, String title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if (title != null) {
            alertDialogBuilder.setTitle(title);
        }
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

//Email validation

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean textViewFilled(TextView txtView) {
        return txtView.getText() != null && !TextUtils.isEmpty(txtView.getText());
    }

    public static BigDecimal getDecimal(TextView textView) {
        if (TextUtils.isEmpty(textView.getText())) {
            return null;
        }
        return new BigDecimal(textView.getText().toString());
    }


    public static void saveFile(Context context, Bitmap b, String picName) throws IOException {
        FileOutputStream fos;

        fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
        b.compress(Bitmap.CompressFormat.PNG, 100, fos);

        fos.close();

    }
//Internet Connection Check

    public static boolean isInternetOn(Context ctx) {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED || connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet


            return true;

        } else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED || connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {


            return false;
        }
        return false;
    }


    public static Bitmap loadBitmap(Context context, String picName) {
        Bitmap b = null;
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            Log.d("tag", "file not found");
            e.printStackTrace();
        } catch (IOException e) {
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

    @NonNull
    public static String createDate(int day, int month, int year) {
        month++;
        String dayString = "" + day;
        if (day < 10) {
            dayString = "0" + day;
        }

        String monthString = "" + month;
        if (month < 10) {
            monthString = "0" + month;
        }

        return year + "-" + monthString + "-" + dayString;
    }

    public static ProgressDialog getProgressDialogue(String message, Activity activity) {
        if (message == null) {
            message = "Loading ..";
        }
        ProgressDialog pDialog = new ProgressDialog(activity);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    public static void writeObject(Context context, String key, Object object) {
        try {
            FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
            System.out.println("Saved the object ..");
        } catch (Exception e) {
            System.err.print("Error while saving the object .." + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Object readObject(Context context, String key) {

        try {
            FileInputStream fis = context.openFileInput(key);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object object = ois.readObject();
            return object;
        } catch (Exception e) {
            System.err.print("Error while fetching the object .." + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void nextFragment(FragmentActivity activity, Fragment fragment) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    public static void downloadImage(final ImageView mImageView, Context activity, String url) {
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                mImageView.setImageBitmap(bitmap);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                //mImageView.setImageResource(R.drawable.image_load_error);
                System.err.println("Error loading images ..");
            }
        });
// Access the RequestQueue through your singleton class.
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(request);

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkcontactPermission(Activity activity) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static List<String> convertToStringArrayList(List<?> objects) {
        if (objects == null || objects.size() == 0) {
            return new ArrayList<>();
        }
        List<String> stringList = new ArrayList<>();
        for (Object o : objects) {
            if (o instanceof BillItem) {
                BillItem item = (BillItem) o;
                if (item.getParentItem() != null && item.getParentItem().getName() != null) {
                    stringList.add(item.getParentItem().getName());
                } else {
                    stringList.add(item.getName());
                }

            } else if (o instanceof BillLocation) {
                BillLocation loc = (BillLocation) o;
                stringList.add(loc.getName());
            }
        }
        return stringList;
    }

    public static Object findInStringList(List<?> objects, String selected) {
        if (objects == null || objects.size() == 0 || selected == null) {
            return null;
        }
        for (Object o : objects) {
            if (o instanceof BillItem) {
                BillItem item = (BillItem) o;
                String itemName = item.getName();
                if (item.getParentItem() != null && item.getParentItem().getName() != null) {
                    itemName = item.getParentItem().getName();
                }
                if (itemName != null && itemName.equals(selected)) {
                    return item;
                }
            }
        }
        return null;
    }

    public static String getItemImageURL(Integer parentItemId) {
        return ServiceUtil.ADMIN_URL + "getParentItemImage/" + parentItemId;
    }


    public static Integer getRootItemId(BillItem subItem) {
        Integer itemId;
        if (subItem.getParentItemId() != null) {
            itemId = subItem.getParentItemId();
        } else if (subItem.getParentItem() != null) {
            itemId = subItem.getParentItem().getId();
        } else {
            itemId = subItem.getId();
        }
        return itemId;
    }

    public static String getCustomerItemString(List<BillItem> items) {
        String result = "";
        for (BillItem subscribed : items) {
            String name = subscribed.getName();
            if (name == null) {
                name = subscribed.getParentItem().getName();
            }
            result = result.concat(name + " | ");
            //txtItemName.setText(txtItemName.getText().toString().concat(name + " | "));
        }
        return result;
    }


}
