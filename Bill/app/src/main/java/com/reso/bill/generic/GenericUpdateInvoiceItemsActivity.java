package com.reso.bill.generic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.generic.GenericInvoiceItemsAdapter;
import util.ServiceUtil;
import util.Utility;

public class GenericUpdateInvoiceItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private GenericInvoiceItemsAdapter adapter;
    private Button saveBill;
    private Button addProduct;
    private AutoCompleteTextView productName;
    private BillInvoice invoice;
    private BillUser user;
    private BillUser customer;
    private TextView totalBillAmount;
    private List<BillItem> items;
    private BillItem selectedItem;
    private List<BillItem> invoiceItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_update_invoice_items);
        Utility.setActionBar("Bill details", getSupportActionBar());

        saveBill = (Button) findViewById(R.id.gn_btn_save_bill);

        saveBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBill();
            }
        });

        addProduct = (Button) findViewById(R.id.gn_btn_add_invoice_item);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (selectedItem != null) {
                        if (invoiceItems.size() == 0) {
                            updateInvoiceItems(invoiceItems);
                        } else {
                            if (isAlreadyAdded(selectedItem)) {
                                return;
                            }
                            updateInvoiceItems(invoiceItems);
                        }
                    } else {
                        if (TextUtils.isEmpty(productName.getText())) {
                            Utility.createAlert(GenericUpdateInvoiceItemsActivity.this, "Please enter a product name", "Error");
                            return;
                        }
                        BillItem newItem = new BillItem();
                        newItem.setName(productName.getText().toString());
                        newItem.setQuantity(BigDecimal.ONE);
                        newItem.setPrice(BigDecimal.ZERO);
                        if (isAlreadyAdded(newItem)) return;
                        invoiceItems.add(newItem);
                    }
                    if (adapter == null) {
                        adapter = new GenericInvoiceItemsAdapter(invoiceItems, GenericUpdateInvoiceItemsActivity.this, totalBillAmount);
                        recyclerView.setLayoutManager(new LinearLayoutManager(GenericUpdateInvoiceItemsActivity.this));
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.setItems(invoiceItems);
                        adapter.notifyDataSetChanged();
                    }
                    productName.setText("");
                } catch (Exception e) {

                } finally {
                    selectedItem = null;
                }

            }
        });

        productName = (AutoCompleteTextView) findViewById(R.id.et_product_name);

        /*productName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Utility.hideKeyboard(GenericUpdateInvoiceItemsActivity.this);
            }
        });*/

        totalBillAmount = (TextView) findViewById(R.id.txt_gn_bill_items_total);

        user = (BillUser) Utility.readObject(GenericUpdateInvoiceItemsActivity.this, Utility.USER_KEY);

        productName.setThreshold(2);

        productName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (items != null && items.size() > 0) {
                        String selected = adapterView.getItemAtPosition(i).toString();
                        selectedItem = (BillItem) Utility.findInStringList(items, selected);
                        //BillUser selectedCustomer = items.get(i);

                    }
                } catch (Exception e) {
                    System.out.println("Error in setting customer .." + e);
                }

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_update_invoice_items);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, GenericUpdateInvoiceItemsActivity.this);
    }


    private boolean isAlreadyAdded(BillItem item) {
        for (BillItem invItem : invoiceItems) {
            if (invItem.getParentItem() != null && item.getParentItem() != null && invItem.getParentItem().getId() == item.getId()) {
                Utility.createAlert(GenericUpdateInvoiceItemsActivity.this, "Product already added to the bill!", "Error");
                return true;
            } else if (invItem.getParentItem() != null && item.getName() != null && item.getName().equalsIgnoreCase(invItem.getParentItem().getName())) {
                Utility.createAlert(GenericUpdateInvoiceItemsActivity.this, "Product already added to the bill!", "Error");
                return true;
            } else if (invItem.getName() != null && item.getName() != null && invItem.getName().equalsIgnoreCase(item.getName())) {
                Utility.createAlert(GenericUpdateInvoiceItemsActivity.this, "Product already added to the bill!", "Error");
                return true;
            }
        }
        return false;
    }

    private void updateInvoiceItems(List<BillItem> invoiceItems) {
        BillItem invoiceItem = new BillItem();
        invoiceItem.setQuantity(BigDecimal.ONE);
        if (selectedItem.getPrice() != null) {
            invoiceItem.setPrice(invoiceItem.getQuantity().multiply(selectedItem.getPrice()));
        } else {
            invoiceItem.setPrice(BigDecimal.ZERO);
        }
        BillItem parent = new BillItem();
        parent.setId(selectedItem.getId());
        parent.setName(selectedItem.getName());
        invoiceItem.setParentItem(parent);
        invoiceItems.add(invoiceItem);
    }


    private void saveBill() {
        if (invoice == null) {
            invoice = new BillInvoice();
            invoice.setInvoiceDate(new Date());
        }
        if (customer == null || TextUtils.isEmpty(customer.getPhone())) {
            Utility.createAlert(GenericUpdateInvoiceItemsActivity.this, "Please enter a valid phone number!", "Error");
            return;
        }

        invoice.setAmount(Utility.getDecimal(totalBillAmount));

        if (invoice.getInvoiceItems() == null || invoice.getInvoiceItems().size() == 0) {
            invoice.setInvoiceItems(invoiceItems);
        } else {
            List<BillItem> deletedItems = new ArrayList<>();
            for (BillItem old : invoice.getInvoiceItems()) {
                boolean found = false;
                for (BillItem newItem : invoiceItems) {
                    if (newItem.getId() != null && old.getId() == newItem.getId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    old.setStatus(BillConstants.STATUS_DELETED);
                    deletedItems.add(old);
                }
            }
            invoiceItems.addAll(deletedItems);
            invoice.setInvoiceItems(invoiceItems);
        }

        BillServiceRequest request = new BillServiceRequest();
        request.setInvoice(invoice);
        request.setUser(customer);
        request.setBusiness(user.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Saving", GenericUpdateInvoiceItemsActivity.this);
        StringRequest myReq = ServiceUtil.getBusinessStringRequest("updateBill", billSavedListener(), ServiceUtil.createMyReqErrorListener(pDialog, GenericUpdateInvoiceItemsActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericUpdateInvoiceItemsActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> billSavedListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlertWithActivityFinish(GenericUpdateInvoiceItemsActivity.this, "Invoice saved successfully!", "Done", Utility.CUSTOMER_KEY, customer, GenericCreateBillActivity.class, GenericCreateBillActivity.FINISH_BILL_ACTIVITY);
                    if (serviceResponse.getInvoice() != null) {
                        customer.setCurrentInvoice(serviceResponse.getInvoice());
                        //Utility.nextFragmentPopBackstack(GenericUpdateInvoiceItemsActivity.this, GenericCreateBill.newInstance(customer), false);
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericUpdateInvoiceItemsActivity.this, serviceResponse.getResponse(), "Error");
                }


            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        if (customer == null) {
            customer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);
            if (customer != null) {
                invoice = customer.getCurrentInvoice();
            }

        }
        //loadBillDetails();
        prepareInvoice(invoice);
        //if (invoice == null || invoice.getId() == null) {
        loadBusinessItems(); //For Suggestions}

    }

    private void loadBusinessItems() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        //pDialog = Utility.getProgressDialogue("Loading..", GenericUpdateInvoiceItemsActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("loadBusinessItems", customersLoaded(), ServiceUtil.createMyReqErrorListener(pDialog, GenericUpdateInvoiceItemsActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericUpdateInvoiceItemsActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> customersLoaded() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    items = serviceResponse.getItems();
                    if (items != null && items.size() > 0) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GenericUpdateInvoiceItemsActivity.this, android.R.layout.select_dialog_item, Utility.convertToStringArrayList(items));
                        productName.setAdapter(adapter);
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    //productName.setada
                }
            }

        };
    }

    private void prepareInvoice(BillInvoice inv) {
        if (inv != null) {
            totalBillAmount.setText(CommonUtils.getStringValue(inv.getPayable()));
            if (inv.getInvoiceItems() != null && inv.getInvoiceItems().size() > 0) {
                copyAllItems(inv);
                adapter = new GenericInvoiceItemsAdapter(invoiceItems, GenericUpdateInvoiceItemsActivity.this, totalBillAmount);
                recyclerView.setLayoutManager(new LinearLayoutManager(GenericUpdateInvoiceItemsActivity.this));
                recyclerView.setAdapter(adapter);
            }
            if (inv.getStatus() != null && inv.getStatus().equalsIgnoreCase(BillConstants.INVOICE_STATUS_PAID)) {
                addProduct.setVisibility(View.GONE);
                productName.setEnabled(false);
                if (adapter != null) {
                    adapter.setDisableEdit(true);
                }
            }
        }
    }

    private void copyAllItems(BillInvoice inv) {
        if (inv.getInvoiceItems() != null && inv.getInvoiceItems().size() > 0) {
            for (BillItem item : inv.getInvoiceItems()) {
                try {
                    invoiceItems.add((BillItem) item.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    System.out.println("Cloning failed ..");
                }
            }

        }


    }
}
