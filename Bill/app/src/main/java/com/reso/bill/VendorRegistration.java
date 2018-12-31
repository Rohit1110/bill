package com.reso.bill;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.components.MultiSelectionSpinner;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillLocation;
import com.rns.web.billapp.service.bo.domain.BillSector;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import adapters.LocationAdapter;
import util.FirebaseUtil;
import util.ServiceUtil;
import util.Utility;

public class VendorRegistration extends AppCompatActivity {

    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private Button register;
    private EditText name, /*panNumber, aadharNumber,*/
            email, phone, businessName;
    private MultiSelectionSpinner areas;
    private LocationAdapter adapter;
    private ProgressDialog pDialog;
    private boolean saveRequest;
    private static final int REQUEST_PICK_FILE = 1;
    private File selectedFile;
    String identy;
    private BillUser user;
    //private EditText businessLicense;
    private TextView tnc;
    private Spinner sectors;
    private ProgressDialog pDialogSectors;
    private List<BillSector> sectorsList;
    private Spinner cities;
    private EditText address;
    private List<BillLocation> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender_registration);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.vtoolbar);
        setSupportActionBar(toolbar);*/


        /*toolbar.setNavigationIcon(R.mipmap.backarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = Utility.getDashboardIntent(VendorRegistration.this, user);
                startActivity(i);

            }
        });*/

        Utility.setActionBar("User Information", getSupportActionBar());

        register = (Button) findViewById(R.id.fab_vregister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*BillUser requestUser = new BillUser();
                if(requestUser != null) {
                    requestUser.setId(user.getId());
                }*/
                if (!name.getText().toString().equals("") && /*!panNumber.getText().toString().equals("") && !aadharNumber.getText().toString().equals("") &&*/ !businessName.getText().toString().equals("")) {
                    if (Utility.isValidEmail(email.getText().toString())) {
                        int selectedItemOfMySpinner = areas.getSelectedItemPosition();
                        String actualPositionOfMySpinner = (String) areas.getItemAtPosition(selectedItemOfMySpinner);

                        if (actualPositionOfMySpinner == null || actualPositionOfMySpinner.isEmpty()) {
                            Toast.makeText(VendorRegistration.this, "Please select a location!", Toast.LENGTH_LONG);
                            return;
                        }
                        List<BillLocation> billLocations = areas.selectedLocations();
                        if (!"Other".equals(cities.getSelectedItem()) && (billLocations == null || billLocations.size() == 0)) {
                            Toast.makeText(VendorRegistration.this, "Please select atleast one location!", Toast.LENGTH_LONG);
                            return;
                        }
                        if (sectors.isEnabled() && sectors.getSelectedItem().toString().trim().length() == 0) {
                            Toast.makeText(VendorRegistration.this, "Please select area of business!", Toast.LENGTH_LONG);
                            return;
                        }
                        BillUser requestUser = new BillUser();
                        if (user != null) {
                            requestUser.setId(user.getId());
                        }
                        requestUser.setName(name.getText().toString());
                        requestUser.setEmail(email.getText().toString());
                        //requestUser.setPanDetails(panNumber.getText().toString());
                        requestUser.setPhone(FirebaseUtil.getPhone());
                        //requestUser.setAadharNumber(aadharNumber.getText().toString());
                        BillBusiness business = new BillBusiness();
                        if (user != null && user.getCurrentBusiness() != null) {
                            business.setId(user.getCurrentBusiness().getId());
                        }
                        business.setName(businessName.getText().toString());
                        //business.setIdentificationNumber(businessLicense.getText().toString());
                        business.setBusinessLocations(billLocations);
                        business.setAddress(address.getText().toString());
                        if (sectors.isEnabled()) {
                            if (sectors.getSelectedItemPosition() == 0) {
                                Toast.makeText(VendorRegistration.this, "Select an area of business", Toast.LENGTH_LONG).show();
                                return;
                            }
                            business.setBusinessSector((BillSector) Utility.findInStringList(sectorsList, sectors.getSelectedItem().toString()));
                        }

                        requestUser.setCurrentBusiness(business);
                        saveUserInfo(requestUser);

                    } else {
                        Toast.makeText(VendorRegistration.this, "Enter valid email", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(VendorRegistration.this, "Some fields are missing", Toast.LENGTH_LONG).show();
                }
                //saveUserInfo(requestUser);
            }
        });

        name = (EditText) findViewById(R.id.et_name);
        //panNumber = (EditText) findViewById(R.id.et_pan_number);
        //aadharNumber = (EditText) findViewById(R.id.et_aadhar_number);
        phone = (EditText) findViewById(R.id.et_phone);
        email = (EditText) findViewById(R.id.et_email);
        businessName = (EditText) findViewById(R.id.et_business_name);
        //businessLicense = (EditText) findViewById(R.id.et_business_license);
        phone.setText(FirebaseUtil.getPhone());
        phone.setEnabled(false);

        areas = (MultiSelectionSpinner) findViewById(R.id.sp_area);

        sectors = (Spinner) findViewById(R.id.sp_select_sector);
        cities = (Spinner) findViewById(R.id.sp_select_city);
        address = (EditText) findViewById(R.id.et_business_address);
        //adapter = new LocationAdapter(this, R.layout.spinner_multi_select, new ArrayList<BillLocation>(), VendorRegistration.this);
        //areas.setAdapter(adapter);
        /*aadharNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    *//*if (event.getRawX() >= (aadharNumber.getRight() - aadharNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Utility.checkcontactPermission(VendorRegistration.this);
                        pickImage();
                        identy = "aadhar";
                        // aadharNumber.setText(identy);
                        return true;
                    }*//*
                }
                return false;
            }
        });


        panNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    *//*if (event.getRawX() >= (panNumber.getRight() - panNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Utility.checkcontactPermission(VendorRegistration.this);
                        pickImage();
                        identy = "pan";
                        return true;
                    }*//*
                }
                return false;
            }
        });*/

        cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("City selected => " + cities.getSelectedItem());
                List<BillLocation> cityLocations = new ArrayList<>();
                for (BillLocation loc : locations) {
                    if (loc.getCity() != null && loc.getCity().equals(cities.getSelectedItem())) {
                        cityLocations.add(loc);
                    }
                }
                if (cityLocations.size() > 0) {
                    setLocationsAdapter(cityLocations);
                    areas.setVisibility(View.VISIBLE);
                } else {
                    areas.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loadLocations();

        Object o = Utility.readObject(VendorRegistration.this, Utility.USER_KEY);
        if (o != null) {
            user = (BillUser) o;
        }


        if (user != null) {
            name.setText(user.getName());
            email.setText(user.getEmail());
            //aadharNumber.setText(user.getAadharNumber());
            //panNumber.setText(user.getPanDetails());
            if (user.getCurrentBusiness() != null) {
                businessName.setText(user.getCurrentBusiness().getName());
                //businessLicense.setText(user.getCurrentBusiness().getIdentificationNumber());
                address.setText(user.getCurrentBusiness().getAddress());

                if (user.getCurrentBusiness().getBusinessSector() != null) {
                    sectors.setEnabled(false);
                } else {
                    loadSectors();
                }
                if (user.getCurrentBusiness().getBusinessLocations() != null && user.getCurrentBusiness().getBusinessLocations().size() > 0) {
                    cities.setVisibility(View.GONE);
                } else {
                    areas.setVisibility(View.GONE);
                }
            }

        } else {
            loadSectors();
            areas.setVisibility(View.GONE);
        }


        tnc = (TextView) findViewById(R.id.txt_tnc);
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tncI = new Intent(VendorRegistration.this, TermsNConditionsActivity.class);
                startActivity(tncI);
            }
        });

    }


    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = VendorRegistration.this.getContentResolver().openInputStream(data.getData());
                System.out.println("SSSSSSSSSSSS " + data.getData().getPath());
                if (identy.equals("aadhar")) {
                    //aadharNumber.setText(data.getData().getPath());
                    String filename = data.getData().getPath().substring(data.getData().getPath().lastIndexOf("/") + 1);
                    //aadharNumber.setText(filename);
                }
                if (identy.equals("pan")) {
                    // panNumber.setText(data.getData().getPath());
                    String filename = data.getData().getPath().substring(data.getData().getPath().lastIndexOf("/") + 1);
                    //panNumber.setText(filename);
                }

                identy = data.getData().getPath();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    private void saveUserInfo(BillUser user) {
        saveRequest = true;
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(user);
        pDialog = Utility.getProgressDialogue("Saving..", this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateUserProfile", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, VendorRegistration.this), request);
        RequestQueue queue = Volley.newRequestQueue(VendorRegistration.this);
        queue.add(myReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
    }

    //Services

    private void loadLocations() {
        saveRequest = false;
        pDialog = Utility.getProgressDialogue("Loading locations", VendorRegistration.this);
        BillServiceRequest request = new BillServiceRequest();
        StringRequest myReq = ServiceUtil.getStringRequest("getAllAreas", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, VendorRegistration.this), request);
        RequestQueue queue = Volley.newRequestQueue(VendorRegistration.this);
        queue.add(myReq);
    }

    //

    private void loadSectors() {
        pDialogSectors = Utility.getProgressDialogue("Loading sectors", VendorRegistration.this);
        BillServiceRequest request = new BillServiceRequest();
        StringRequest myReq = ServiceUtil.getStringRequest("getAllSectors", sectorsListener(), ServiceUtil.createMyReqErrorListener(pDialog, VendorRegistration.this), request);
        RequestQueue queue = Volley.newRequestQueue(VendorRegistration.this);
        queue.add(myReq);
    }

    private Response.Listener<String> sectorsListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response sectors:" + response);
                pDialogSectors.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    System.out.println("Sectors loaded successfully!");

                    sectorsList = serviceResponse.getSectors();
                    List<String> strings = Utility.convertToStringArrayList(sectorsList);
                    if (strings == null) {
                        strings = new ArrayList<>();
                    }
                    strings.add(0, "Select sector");
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(VendorRegistration.this, android.R.layout.simple_spinner_item, strings);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    VendorRegistration.this.sectors.setAdapter(adapter);
                    //sectors.setSelection(Calendar.getInstance().get(Calendar.MONTH) + 1);

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    if (saveRequest) {
                        Utility.createAlert(VendorRegistration.this, serviceResponse.getResponse(), "Error");
                    }

                }

            }

        };
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {

                    if (saveRequest) {
                        System.out.println("User saved successfully!");
                        if (user != null && user.getId() != null) {
                            Utility.createAlertWithActivityFinish(VendorRegistration.this, "Saved successfully!", "Done", null, null, null, null);
                        } else {
                            startActivity(new Intent(VendorRegistration.this, MainActivity.class));
                        }

                    } else {
                        System.out.println("Locations loaded successfully!");
                        locations = prepareLocations(serviceResponse);
                        setLocationsAdapter(locations);
                        prepareCities(locations);

                        if (user != null && user.getCurrentBusiness() != null && user.getCurrentBusiness().getBusinessLocations() != null && user.getCurrentBusiness().getBusinessLocations().size() > 0) {
                            areas.setSelection(Utility.convertToStringArrayList(user.getCurrentBusiness().getBusinessLocations()));
                        }
                    }

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    if (saveRequest) {
                        Utility.createAlert(VendorRegistration.this, serviceResponse.getResponse(), "Error");
                    }

                }

            }

        };
    }

    private void setLocationsAdapter(List<BillLocation> locations) {
        adapter = new LocationAdapter(VendorRegistration.this, R.layout.spinner_multi_select, locations, VendorRegistration.this);
        areas.setLocations(locations);
    }

    private void prepareCities(List<BillLocation> locations) {
        List<String> citiesList = new ArrayList<>();
        citiesList.add("Select city");
        if (locations == null || locations.size() == 0) {
            return;
        }
        for (BillLocation location : locations) {
            if (!citiesList.contains(location.getCity())) {
                citiesList.add(location.getCity());
            }
        }
        citiesList.add("Other");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(VendorRegistration.this, R.layout.spinner_basic_text_white, citiesList);
        cities.setAdapter(adapter);
    }

    private List<BillLocation> prepareLocations(BillServiceResponse serviceResponse) {
        if (serviceResponse == null || serviceResponse.getLocations() == null) {
            return null;
        }
        if (user == null || user.getCurrentBusiness() == null || user.getCurrentBusiness().getBusinessLocations() == null || user.getCurrentBusiness().getBusinessLocations().size() == 0) {
            return serviceResponse.getLocations();
        }

        for (BillLocation original : serviceResponse.getLocations()) {
            for (BillLocation loc : user.getCurrentBusiness().getBusinessLocations()) {
                if (original.getId() == loc.getId()) {
                    original.setStatus("Selected");
                }
            }
        }
        return serviceResponse.getLocations();
    }

}
