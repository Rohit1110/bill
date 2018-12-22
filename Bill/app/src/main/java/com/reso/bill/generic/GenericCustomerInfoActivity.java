package com.reso.bill.generic;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillLocation;
import com.rns.web.billapp.service.bo.domain.BillSubscription;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

public class GenericCustomerInfoActivity extends AppCompatActivity {

    private Button fabsubscription;
    private List<BillCustomer> list = new ArrayList<>();
    private LinearLayout layout;
    private BillUser customer;
    private EditText name, contact, email, address, serviceCharge;
    private Spinner areas;
    private BillUser user;
    private ProgressDialog pDialog;
    private CheckBox addToContacts;
    //private CheckBox hideBillDetails;
    private RadioButton showFullBill;
    private RadioButton hideFullBill;
    private Integer customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_customer_info);

        customer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);
        Utility.setActionBar("Edit Customer Info", getSupportActionBar());

        fabsubscription = (Button) findViewById(R.id.btn_gn_save_product);
        fabsubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillSubscription subscription = null;
                if (customer == null) {
                    customer = new BillUser();
                    subscription = new BillSubscription();
                } else {
                    subscription = customer.getCurrentSubscription();
                    if (subscription == null) {
                        subscription = new BillSubscription();
                    }
                }
                /*if (!Utility.isValidEmail(email.getText().toString())) {
                    email.setError("Please enter a valid email");
                    return;
                }*/
                if (name.getText().toString().equals("")) {
                    name.setError("Please enter name");
                    return;
                }
                if (address.getText().toString().equals("")) {
                    address.setError("Please enter address");
                    return;
                }
                /*if (serviceCharge.getText().toString().equals("")) {
                    serviceCharge.setError("Please enter serviceCharge");
                    return;
                }*/


                if (!Utility.textViewFilled(contact) || contact.getText().toString().length() < 10) {
                    contact.setError("Please enter a valid mobile number");
                    return;
                }
                int selectedItemOfMySpinner = areas.getSelectedItemPosition();
                String actualPositionOfMySpinner = (String) areas.getItemAtPosition(selectedItemOfMySpinner);

                if (actualPositionOfMySpinner.isEmpty()) {
                    Utility.createAlert(GenericCustomerInfoActivity.this, "Please select a location!", "Error");
                    return;
                }
                customer.setName(name.getText().toString());
                customer.setEmail(email.getText().toString());
                customer.setPhone(contact.getText().toString());
                customer.setAddress(address.getText().toString());
                if (hideFullBill.isChecked()) {
                    customer.setShowBillDetails("N");
                } else if (showFullBill.isChecked()) {
                    customer.setShowBillDetails("Y");
                }
                subscription.setServiceCharge(Utility.getDecimal(serviceCharge));



               /* if (areas.getSelectedItem() == null || areas.getSelectedItem().toString().trim().length() == 0) {
                    Utility.createAlert(GenericCustomerInfoActivity.this, "Please select a location!", "Error");
                    return;
                }*/
                subscription.setArea(findArea());
                customer.setCurrentSubscription(subscription);
                saveCustomer();

            }


        });

        /*layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddSubcription fragment = new AddSubcription();
                FragmentTransaction ft = GenericCustomerInfoActivity.this.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();

            }
        });*/

        name = (EditText) findViewById(R.id.et_product_name);
        email = (EditText) findViewById(R.id.et_customer_email);
        contact = (EditText) findViewById(R.id.et_product_description);
        address = (EditText) findViewById(R.id.et_customer_address);
        areas = (Spinner) findViewById(R.id.sp_customer_area);
        serviceCharge = (EditText) findViewById(R.id.et_customer_service_charge);
        addToContacts = (CheckBox) findViewById(R.id.chk_add_to_contact);
        showFullBill = (RadioButton) findViewById(R.id.radio_show_full_bill);
        hideFullBill = (RadioButton) findViewById(R.id.radio_show_only_summary);

        user = (BillUser) Utility.readObject(GenericCustomerInfoActivity.this, Utility.USER_KEY);

        setCustomerInfo();

        contactExists();

        name.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (name.getRight() - name.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Utility.checkcontactPermission(GenericCustomerInfoActivity.this);
                        //identy = "aadhar";
                        // aadharNumber.setText(identy);
                        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        startActivityForResult(contactPickerIntent, Utility.RESULT_PICK_CONTACT);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void setCustomerInfo() {
        if (customer != null) {
            name.setText(customer.getName());
            email.setText(customer.getEmail());
            contact.setText(customer.getPhone());
            address.setText(customer.getAddress());
            if (customer.getServiceCharge() != null) {
                serviceCharge.setText(customer.getServiceCharge().toString());
            }
            if (customer.getShowBillDetails() != null && "N".equals(customer.getShowBillDetails())) {
                hideFullBill.setChecked(true);
            } else if (customer.getShowBillDetails() != null && "Y".equals(customer.getShowBillDetails())) {
                showFullBill.setChecked(true);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GenericCustomerInfoActivity.this.RESULT_OK) {
            switch (requestCode) {
                case Utility.RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }

        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String cname = null;
            Uri uri = data.getData();
            cursor = GenericCustomerInfoActivity.this.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();

            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            phoneNo = cursor.getString(phoneIndex);
            cname = cursor.getString(nameIndex);

            name.setText(cname);
            contact.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private BillLocation findArea() {
        if (user != null && user.getCurrentBusiness() != null && user.getCurrentBusiness().getBusinessLocations() != null) {
            for (BillLocation loc : user.getCurrentBusiness().getBusinessLocations()) {
                if (loc.getName().equals(areas.getSelectedItem().toString())) {
                    return loc;
                }
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLocations();
    }

    private void loadLocations() {
        if (user != null && user.getCurrentBusiness() != null && user.getCurrentBusiness().getBusinessLocations() != null) {
            List<String> locationsList = new ArrayList<>();
            for (BillLocation loc : user.getCurrentBusiness().getBusinessLocations()) {
                locationsList.add(loc.getName());
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(GenericCustomerInfoActivity.this, android.R.layout.simple_spinner_item, locationsList); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            areas.setAdapter(spinnerArrayAdapter);
            if (customer != null && customer.getCurrentSubscription() != null && customer.getCurrentSubscription().getArea() != null) {
                areas.setSelection(locationsList.indexOf(customer.getCurrentSubscription().getArea().getName()));
            }
        }
    }

    private void saveCustomer() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        request.setUser(customer);
        pDialog = Utility.getProgressDialogue("Saving customer..", GenericCustomerInfoActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomer", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, GenericCustomerInfoActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericCustomerInfoActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (serviceResponse.getUser() != null) {
                        customerId = serviceResponse.getUser().getId();
                    }
                    if (addToContacts.isChecked() && customer.getPhone() != null) {
                        if (ContextCompat.checkSelfPermission(GenericCustomerInfoActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(GenericCustomerInfoActivity.this, new String[]{android.Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, Utility.MY_PERMISSIONS_REQUEST_CONTACTS);
                        } else {
                            //You already have permission
                            try {
                                addContact(customer);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        getNextIntent();
                    }

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericCustomerInfoActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

    /*@NonNull
    private Fragment getNextFragment() {
        if (customer != null && customer.getId() == null && customerId != null) {
            customer.setId(customerId);
            if (customer.getCurrentSubscription() != null) {
                customer.getCurrentSubscription().setId(customerId);
            }
            return CustomerProfileFragment.newInstance(customer);
        }
        return new CustomerList();
    }*/

    private void getNextIntent() {
        String message = "Customer details updated successfully!";
        String title = "Done";
        if (customer != null && customer.getId() == null && customerId != null) {
            customer.setId(customerId);
            if (customer.getCurrentSubscription() != null) {
                customer.getCurrentSubscription().setId(customerId);
            }
            Utility.createAlertWithActivityFinish(this, message, title, Utility.CUSTOMER_KEY, customer, GenericCustomerProfileActivity.class, null);
            return;
        }
        Utility.createAlertWithActivityFinish(this, message, title, null, null, null, null);
    }

    /*private void insertContactPhoneNumber(String phoneNumber) {
        Uri addContactsUri = ContactsContract.Data.CONTENT_URI;

        // Add an empty contact and get the generated id.
        long rowContactId = getRawContactId();
        // Create a ContentValues object.
        ContentValues contentValues = new ContentValues();

        // Each contact must has an id to avoid java.lang.IllegalArgumentException: raw_contact_id is required error.
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rowContactId);

        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);

        // Put phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);

        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        // Insert new contact data into phone contact list.
        GenericCustomerInfoActivity.this.getContentResolver().insert(addContactsUri, contentValues);

        System.out.println("Added to contacts!");

        Utility.nextFragment(GenericCustomerInfoActivity.this, new CustomerList());

    }*/

    private void addContact(BillUser customer) {
        ContentValues values = new ContentValues();
        values.put(Contacts.People.NUMBER, customer.getPhone());
        values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
        values.put(Contacts.People.LABEL, customer.getName());
        values.put(Contacts.People.NAME, customer.getName());
        Uri dataUri = GenericCustomerInfoActivity.this.getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, customer.getPhone());
        updateUri = GenericCustomerInfoActivity.this.getContentResolver().insert(updateUri, values);
        //Utility.nextFragment(GenericCustomerInfoActivity.this, getNextFragment());
        //Utility.createAlertWithActivityFinish(GenericCustomerInfoActivity.this, "Customer details updated successfully!", "Done", null, null, null, null);
        getNextIntent();
    }

    public boolean contactExists() {

        try {

            if (customer == null) {
                return false;
            }

            if (ContextCompat.checkSelfPermission(GenericCustomerInfoActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GenericCustomerInfoActivity.this, new String[]{android.Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, Utility.MY_PERMISSIONS_READ_CONTACTS);
            } else {
                Cursor cursor = GenericCustomerInfoActivity.this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.NUMBER + "=" + customer.getPhone(), null, null);
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    System.out.println("Contact found .." + name + " size - " + cursor.getCount());
                    addToContacts.setChecked(true);
                    addToContacts.setEnabled(false);
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    private long getRawContactId() {
        // Inser an empty contact.
        ContentValues contentValues = new ContentValues();
        Uri rawContactUri = GenericCustomerInfoActivity.this.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        // Get the newly created contact raw id.
        long ret = ContentUris.parseId(rawContactUri);
        return ret;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addContact(customer);
                } else {
                    //Utility.nextFragment(GenericCustomerInfoActivity.this, getNextFragment());
                    finish();
                }
                return;
            }
            case Utility.MY_PERMISSIONS_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contactExists();
                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
