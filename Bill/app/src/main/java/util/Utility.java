package util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.flurry.android.FlurryAgent;
import com.reso.bill.R;
import com.reso.bill.components.InputStreamVolleyRequest;
import com.reso.bill.generic.GenericDashboard;
import com.reso.bill.generic.GenericInvoices;
import com.reso.bill.generic.GenericNewDashboard;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillLocation;
import com.rns.web.billapp.service.bo.domain.BillSector;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.BillFilter;

/**
 * Created by Rohit on 11/27/2017.
 */

public class Utility {
   /* public  FirebaseFirestore db = FirebaseFirestore.getInstance();*/

    public static final String USER_KEY = "billUser";
    public static final String ITEM_KEY = "Item";
    public static final String INVOICE_KEY = "Invoice";
    public static final String CUSTOMER_KEY = "Customer";
    public static final String BUSINESS_KEY = "Business";
    public static final String GROUP_KEY = "Group";
    public static final String DISTRIBUTOR_KEY = "Distributor";

    public static final String INTENT_QUICK_BILL = "quickBill";


    public static final String COLOR_BLUE = "#00A6FF";
    public static final String TITLE_FONT = "#343F4B";
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    public static final int MY_PERMISSIONS_REQUEST_CONTACTS = 2;
    public static final int MY_PERMISSIONS_READ_CONTACTS = 3;
    public static final int MY_PERMISSIONS_WRITE_STORAGE = 4;
    public static final int MY_PERMISSIONS_BLUETOOTH = 5;
    public static final String PREF_NAME = "PayPerBill";
    private static int selectedElement = 0;
    public static int LIST_OPT_DELETE = 0;
    public static String[] LIST_OPTIONS = {"Delete"};
    //int selectedElement = 1; //global variable to store state
    public static final String DATE_FORMAT_DISPLAY = "MMM dd";
    public static final int RESULT_PICK_CONTACT = 1;

    public static final int MENU_ITEM_SEARCH = 4;
    public static final int MENU_ITEM_SAVE = 1;
    public static final int MENU_ITEM_FILTER = 2;
    public static final int MENU_ITEM_EXPORT = 3;
    public static final int MENU_ITEM_PRINT = 5;

    public static void createAlert(Context context, String message, String title) {

        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            if (title != null) {
                alertDialogBuilder.setTitle(title);
            }
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createAlertWithActivityFinish(final Activity activity, String message, String title, final String key, final Object object, final Class<?> cls, final String finishIntent) {
        try {


            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            if (title != null) {
                alertDialogBuilder.setTitle(title);
            }
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if (object == null) {
                        activity.finish();
                    } else {
                        if (finishIntent != null) {
                            Intent intent = new Intent(finishIntent);
                            activity.sendBroadcast(intent);
                        }
                        activity.startActivity(nextIntent(activity, cls, false, object, key));
                    }

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            if (finishIntent != null) {
                alertDialog.setCancelable(false);
            }
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static void AppBarTitle(String title, Activity context) {
        //size = 24
        context.setTitle(Html.fromHtml("<font color='#343F4B' ><small>" + title + "</small></font>"));
        //context.setTitle("My Products");
    }

    public static void AppBarTitleActivity(String title, Activity context) {
        context.setTitle(Html.fromHtml("<font color='#343F4B' >" + title + "</font>"));
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
        //pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    public static void writeObject(Activity context, String key, Object object) {
        try {

            saveToSharedPref(context, key, object);

            /*FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();*/
            System.out.println("Saved the object ..");
        } catch (Exception e) {
            System.err.print("Error while saving the object .." + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Object readObject(Activity context, String key) {

        try {

            BillUser user = (BillUser) readFromSharedPref(context, key, BillUser.class);
            if (user == null) {
                FileInputStream fis = context.openFileInput(key);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Object object = ois.readObject();
                return object;
            } else {
                return user;
            }
        } catch (Exception e) {
            System.err.print("Error while fetching the object .." + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void saveToSharedPref(Activity activity, String keyString, Object object) {
        try {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(keyString, ServiceUtil.toJson(object));
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object readFromSharedPref(Activity activity, String key, Class<?> cls) {
        try {
            if (activity == null || key == null) {
                return null;
            }
            SharedPreferences sharedPreferences = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            return ServiceUtil.fromJson(sharedPreferences.getString(key, null), cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void nextFragment(FragmentActivity activity, Fragment fragment) {
        try {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_layout, fragment);
            ft.addToBackStack(fragment.getClass().getName());
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void nextFragmentPopBackstack(FragmentActivity activity, Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = activity.getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, fragment);
        if (addToBackStack) {
            ft.addToBackStack(fragment.getClass().getName());
        }
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
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

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
            } else if (o instanceof BillSector) {
                BillSector sector = (BillSector) o;
                stringList.add(sector.getName());
            } else if (o instanceof BillUser) {
                BillUser user = (BillUser) o;
                if (user.getName() != null) {
                    stringList.add(user.getName());
                }

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
            } else if (o instanceof BillSector) {
                BillSector sector = (BillSector) o;
                if (sector.getName() != null && selected.equals(sector.getName())) {
                    return sector;
                }
            } else if (o instanceof BillUser) {
                BillUser user = (BillUser) o;
                System.out.println("Selected user .. " + selected + " compare with " + user.getName());
                if (user.getName() != null && selected.equalsIgnoreCase(user.getName())) {
                    return user;
                }
            }
        }
        return null;
    }

    public static String getItemImageURL(Integer parentItemId) {
        return ServiceUtil.ADMIN_URL + "getParentItemImage/" + parentItemId;
    }

    public static String getBusinessItemImageURL(Integer itemId) {
        return ServiceUtil.ADMIN_URL + "getBusinessItemImage/" + itemId;
    }

    @NonNull
    public static BillUser getBasicUser(BillUser user) {
        BillUser currentUser = new BillUser();
        currentUser.setId(user.getId());
        currentUser.setPhone(user.getPhone());
        BillBusiness business = new BillBusiness();
        business.setId(user.getCurrentBusiness().getId());
        currentUser.setCurrentBusiness(business);
        return currentUser;
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

    public static List<String> createYearsArray() {
        //+- 2 years
        List<String> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        list = new ArrayList<>();
        list.add("Select year");
        list.add(String.valueOf(cal.get(Calendar.YEAR)));
        cal.add(Calendar.YEAR, -1);
        list.add(String.valueOf(cal.get(Calendar.YEAR)));
        cal.add(Calendar.YEAR, -1);
        list.add(String.valueOf(cal.get(Calendar.YEAR)));
        return list;

    }


    public static boolean validateDecimal(TextView value) {
        if (TextUtils.isEmpty(value.getText())) {
            return false;
        }
        return (value.getText().toString().matches("\\d*\\.?\\d+"));
    }

   /* public static void filter(final String text,Activity activity) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // Clear the filter list
                    filterList.clear();

                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {

                        *//*hideicon = true;
                        invalidateOptionsMenu();*//*

                        filterList.addAll(list);


                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (BillCustomer item : list) {
                            if (item.getUser().getName().toLowerCase().contains(text.toLowerCase()) *//*|| comparePhone(item, text)*//*) {
                                // Adding Matched items
                                filterList.add(item);
                            }

                        }
                    }

                    // Set on UI Thread
                    (activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Notify the List that the DataSet has changed...
                           *//* adapter = new ContactListAdapter(SearchAppointmentActivity.this, filterList);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(SearchAppointmentActivity.this, 1);
                            recyclerView_contact.setLayoutManager(mLayoutManager);
                            recyclerView_contact.setAdapter(adapter);*//*
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(new CustomerListAdapter(filterList, getActivity(), user));


                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error in filter contacts");
                    e.printStackTrace();
                }


            }
        }).start();

    }*/

    public static AlertDialog.Builder SingleChoiceWithRadioButton(final Activity activity) {
        AlertDialog alert;
        final String[] selectFruit = new String[]{"Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Your Choice");


        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;
    }

    public static int indexOf(List<BillItem> items, BillItem indexOfItem) {
        if (items == null || indexOfItem == null) {
            return -1;
        }
        int position = 0;
        for (BillItem item : items) {
            if (item.getId().intValue() == item.getId().intValue()) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public static String getItemName(BillItem item) {
        if (item == null) {
            return "";
        }
        if (item.getName() != null) {
            return item.getName();
        }
        if (item.getParentItem() != null && item.getParentItem().getName() != null) {
            return item.getParentItem().getName();
        }
        return "";
    }

    public static String getFramework(BillUser user) {
        if (user == null || user.getCurrentBusiness() == null || user.getCurrentBusiness().getBusinessSector() == null || user.getCurrentBusiness().getBusinessSector().getFramework() == null) {
            return BillConstants.FRAMEWORK_RECURRING;
        }
        return user.getCurrentBusiness().getBusinessSector().getFramework();
    }

    public static boolean isNewspaper(BillUser user) {
        if (user == null || user.getCurrentBusiness() == null || user.getCurrentBusiness().getBusinessSector() == null || user.getCurrentBusiness().getBusinessSector().getName() == null) {
            return false;
        }
        return user.getCurrentBusiness().getBusinessSector().getName().equalsIgnoreCase("Newspaper");
    }


    public static Intent getDashboardIntent(Activity currentActivity, BillUser user) {
        if (BillConstants.FRAMEWORK_GENERIC.equals(Utility.getFramework(user))) {
            return new Intent(currentActivity, GenericDashboard.class);
        }
        //return new Intent(currentActivity, Dashboard.class);
        return new Intent(currentActivity, GenericDashboard.class);
    }

    public static AppCompatActivity castActivity(Activity activity) {
        //if (activity instanceof GenericDashboard) {
        return (GenericDashboard) activity;
        //}
        //return (Dashboard) activity;
    }

    public static Fragment getHomeFragment(BillUser user) {
        if (BillConstants.FRAMEWORK_GENERIC.equals(Utility.getFramework(user))) {
            return GenericInvoices.newInstance();
        }
//        return HomeFragment.newInstance(user);
        return GenericNewDashboard.newInstance(user);
    }

    public static String getDecimalText(BigDecimal decimal) {
        if (decimal == null) {
            return "";
        }
        return decimal.stripTrailingZeros().toPlainString();
    }

    public static String getDecimalString(BigDecimal decimal) {
        try {
            if (decimal == null) {
                return "0";
            }
            DecimalFormat decimalFormat = new DecimalFormat("0.#####");
            String result = decimalFormat.format(decimal);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getText(Integer value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static String getText(Long value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static void changeDrawer(final FragmentActivity activity, final Fragment backFragment) {
        final GenericDashboard dashboard = (GenericDashboard) activity;
        dashboard.getToolbar().setNavigationIcon(R.drawable.ic_action_arrow_back);
        //dashboard.getBottomNavigationView().setVisibility(View.GONE);
        //dashboard.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dashboard.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dashboard.setDrawer();
                Utility.nextFragmentPopBackstack(activity, backFragment, false);
            }
        });
        dashboard.setDrawerChanged(true);
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static Intent nextIntent(Activity parent, Class<?> cls, boolean addToBackStack) {
        Intent intent = new Intent(parent, cls);
        if (!addToBackStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }
        return intent;

    }

    public static Intent nextIntent(Activity parent, Class<?> cls, boolean addToBackStack, Object obj, String key) {
        Intent intent = new Intent(parent, cls);
        putIntenObject(obj, key, intent);
        if (!addToBackStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }
        return intent;

    }

    public static void putIntenObject(Object obj, String key, Intent intent) {
        if (obj != null) {
            intent.putExtra(key, ServiceUtil.toJson(obj));
        }
    }


    public static Object getIntentObject(Class<?> clsName, Intent intent, String key) {
        String json = intent.getStringExtra(key);
        return ServiceUtil.fromJson(json, clsName);
    }


    public static void setActionBar(String title, ActionBar actionBar) {
        actionBar.setCustomView(R.layout.layout_app_bar);
        //actionBar.setTitle("My Products");
        actionBar.setTitle(Html.fromHtml("<font color='#343F4B' size = '18' ><small>" + title + "</small></font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
    }

    public static boolean backDefault(MenuItem item, Activity activity) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Back button click
                activity.finish();
                return true;

        }
        return false;
    }

    public static String getLocationString(List<BillLocation> businessLocations) {
        if (businessLocations == null || businessLocations.size() == 0) {
            return "No locations found";
        }
        StringBuilder locBuilder = new StringBuilder();
        for (int i = 0; i < businessLocations.size(); i++) {
            locBuilder.append(businessLocations.get(i).getName());
            if (i < (businessLocations.size() - 1)) {
                locBuilder.append(", ");
            }
        }
        return locBuilder.toString();
    }

    public static String invoicePurpose(BillInvoice currentInvoice) {
        String purpose = "";
        if (currentInvoice.getMonth() != null && currentInvoice.getYear() != null) {
            purpose = "Invoice for " + BillConstants.MONTHS[currentInvoice.getMonth() - 1] + " " + currentInvoice.getYear();
        } else {
            purpose = "Invoice #" + CommonUtils.getStringValue(currentInvoice.getId());
        }
        return purpose;
    }

    public static void showHelpfulToast(ImageView imageView, final String message, final Activity activity) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean isAlpha(String name) {
        return name.matches("[a-zA-Z ]+");
    }

    public static String getUPIString(BillUser user, BillInvoice invoice) {
        if (user == null | user.getFinancialDetails() == null || TextUtils.isEmpty(user.getFinancialDetails().getVpa())) {
            return null;
        }
        if (invoice == null || invoice.getPayable() == null) {
            return null;
        }
        String userId = String.format("%04d", user.getId());
        if (userId.length() > 4) {
            userId = userId.substring(userId.length() - 4, userId.length());
        }
        userId = "0000";
        String txnNote = "Purchase for " + CommonUtils.convertDate(invoice.getInvoiceDate(), DATE_FORMAT_DISPLAY);
        String UPI = "upi://pay?pa=" + user.getFinancialDetails().getVpa() + "&pn=" + user.getName() + "&mc=" + userId + "&tid=" + new Date().getTime() + "&tr=" + invoice.getId() + "&tn=" + txnNote + "&am=" + invoice.getPayable() + "&cu=INR";
                /*+ "&refUrl=" + refUrl*/
        ;
        return UPI.replace(" ", "+");
    }

    public static Integer getCurrentUserPosition(BillUser currentUser, List<BillUser> users) {
        try {
            if (currentUser == null || users == null | users.size() == 0 || currentUser.getId() == null) {
                return null;
            }
            int index = 0;
            for (BillUser user : users) {
                if (user.getId().intValue() == currentUser.getId().intValue()) {
                    System.out.println("Current user found ... " + user.getName());
                    return index;
                }
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void logFlurry(String event, BillUser user) {
        try {
            if (user == null) {
                return;
            }
            Map<String, String> articleParams = new HashMap<String, String>();
            articleParams.put("Phone", user.getPhone());
            FlurryAgent.logEvent(event, articleParams);
            FlurryAgent.endTimedEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static BigDecimal getCostPrice(BillItem item) {
        BigDecimal price = BigDecimal.ZERO;
        if (item.getPrice() != null) {
            price = item.getPrice();
            System.out.println("The item price set as =>" + item.getPrice());
        } else if (item.getCostPrice() != null) {
            price = item.getCostPrice();
            System.out.println("The item price set as =>" + item.getCostPrice());
        } else if (item.getParentItem() != null && item.getParentItem().getCostPrice() != null) {
            price = item.getParentItem().getCostPrice();
            System.out.println("The item price set as =>" + item.getCostPrice());
        }
        return price;
    }

    public static void dismiss(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void exportPendingInvoices(BillFilter filter, BillUser user, Activity activity, String params, String name) {
        try {
            Integer groupId = 0;
            if (filter != null && filter.getGroup() != null) {
                groupId = filter.getGroup().getId();
            }
            exportPdf("PENDING", user.getCurrentBusiness().getId(), groupId, params, activity, name);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void exportPdf(String type, Integer businessId, Integer groupId, String params, final Activity activity, final String fileName) {
        if (TextUtils.isEmpty(params)) {
            params = "NA";
        }
        final ProgressDialog pd = Utility.getProgressDialogue("Downloading ..", activity);
        String mUrl = ServiceUtil.ROOT_URL + "export/" + type + "/" + businessId + "/" + groupId + "/" + params;
        System.out.println("Calling URL =>" + mUrl);
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, mUrl, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                // TODO handle the response
                try {
                    pd.dismiss();
                    if (response != null) {

                        FileOutputStream outputStream;
                        //File file = new File();
                        String name = Environment.getExternalStorageDirectory() + "/" + fileName + ".pdf";

                        outputStream = new FileOutputStream(name);
                        outputStream.write(response);
                        outputStream.close();
                        Toast.makeText(activity, "Download complete ... " + name, Toast.LENGTH_LONG).show();

                        Intent target = new Intent(Intent.ACTION_VIEW);
                        //FileInputStream io = getActivity().openFileInput(name);

                        File storedFile = new File(name);
                        //target.setDataAndType(Uri.fromFile(storedFile), "application/pdf");
                        //target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Uri apkURI = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", storedFile);
                        target.setDataAndType(apkURI, "application/pdf");
                        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        Intent intent = Intent.createChooser(target, "Open File");
                        try {
                            activity.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // Instruct the user to install a PDF reader here, or something
                            e.printStackTrace();
                            Toast.makeText(activity, "Cannot open file!", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                pd.dismiss();
                error.printStackTrace();
            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(activity, new HurlStack());
        mRequestQueue.add(request);
        pd.show();

    }


    public static boolean isDistributor(BillUser user) {
        return user != null && user.getCurrentBusiness() != null && user.getCurrentBusiness().getType() != null && "Distributor".equals(user.getCurrentBusiness().getType());
    }

}
