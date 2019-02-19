package com.reso.bill;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import util.ServiceUtil;
import util.Utility;

public class PaymentSuccessfulActivity extends AppCompatActivity {

    private Button btnOk;
    private TextView paymentResult;
    private TextView paymentResultMessage;
    private TextView paidAmount;
    private ImageView paymentStatusIcon;
    private TextView progress;
    private boolean loading = false;
    private BillInvoice invoice;
    BillUser payee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);

        Utility.setActionBar("Payment result", getSupportActionBar());

        btnOk = findViewById(R.id.btn_payment_result_ok);
        paymentResult = findViewById(R.id.txt_payment_result);
        paymentResultMessage = findViewById(R.id.txt_payment_result_message);
        paidAmount = findViewById(R.id.txt_payment_result_paid_amount);
        paymentStatusIcon = findViewById(R.id.img_payment_result);
        progress = findViewById(R.id.txt_progress);

        btnOk.setEnabled(false);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (loading) {
            return false;
        }
        return Utility.backDefault(item, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        payee = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.DISTRIBUTOR_KEY);
        invoice = (BillInvoice) Utility.getIntentObject(BillInvoice.class, getIntent(), Utility.INVOICE_KEY);
        if (invoice == null | invoice.getId() == null) {
            Utility.createAlert(this, "Incorrect invoice details!", "Error");
            return;
        }
        if (BillConstants.INVOICE_STATUS_PAID.equalsIgnoreCase(invoice.getStatus())) {
            paymentResult.setText("Payment Successful");
            paymentResultMessage.setText("Payment of purchase invoice for " + CommonUtils.convertDate(invoice.getInvoiceDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR) + " to " + payee.getName() + " is successful");
        } else {
            paymentStatusIcon.setImageResource(R.drawable.ic_invoice_failed);
            paymentResult.setText("Payment Failed");
            paymentResultMessage.setText("Payment of purchase invoice for " + CommonUtils.convertDate(invoice.getInvoiceDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR) + " to " + payee.getName() + " is failed.");
        }
        paidAmount.setText(Utility.getDecimalString(invoice.getPayable()));
        payInvoice();
    }


    private void payInvoice() {
        if (invoice == null || invoice.getId() == null || payee == null) {
            return;
        }
        loading = true;
        if (BillConstants.INVOICE_STATUS_PAID.equals(invoice.getStatus())) {
            invoice.setPaymentMode(BillConstants.PAYMENT_ONLINE);
            invoice.setPaidAmount(invoice.getPayable());
        }
        invoice.setPaymentMedium("UPI");
        //invoice.setPaidDate(new Date());
        //}
        BillServiceRequest request = new BillServiceRequest();
        request.setInvoice(invoice);
        //request.setUser(customer);
        //pDialog = Utility.getProgressDialogue("Saving..", activity);
        StringRequest myReq = ServiceUtil.getStringRequest("updateBusinessInvoice", invoicePaidResponse(invoice), ServiceUtil.createMyReqErrorListener(null, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private Response.Listener<String> invoicePaidResponse(final BillInvoice invoice) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                loading = false;
                btnOk.setEnabled(true);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    progress.setText("Updated the server successfully");
                    //Utility.createAlert(activity, "Invoice paid successfully!", "Done");
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(PaymentSuccessfulActivity.this, serviceResponse.getResponse(), "Error");
                }

            }

        };
    }
}
