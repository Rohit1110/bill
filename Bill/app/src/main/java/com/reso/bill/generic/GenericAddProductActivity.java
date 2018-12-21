package com.reso.bill.generic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import model.VolleyMultipartRequest;
import util.BitmapHelper;
import util.ServiceUtil;
import util.Utility;

public class GenericAddProductActivity extends AppCompatActivity {

    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private EditText photoupload;
    private ImageView img;
    private String filestring;
    private BillItem item;
    private TextView productName;
    private TextView productDescription;
    private TextView productPrice;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private String filename;
    private Button save;
    private BillUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_add_product);
        Utility.setActionBar("Add/Edit Product", getSupportActionBar());

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);
        //Fetch item from the intent
        this.item = (BillItem) Utility.getIntentObject(BillItem.class, getIntent(), Utility.ITEM_KEY);

        photoupload = (EditText) findViewById(R.id.et_product_image);
        img = (ImageView) findViewById(R.id.selected_img);
        save = (Button) findViewById(R.id.btn_gn_save_product);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadBitmap();
            }
        });

        photoupload.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (photoupload.getRight() - photoupload.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Utility.checkcontactPermission(GenericAddProductActivity.this);
                        pickImage();

                        return true;
                    }
                }
                return false;
            }
        });

        productName = (TextView) findViewById(R.id.et_product_name);
        productDescription = (TextView) findViewById(R.id.et_product_description);
        productPrice = (TextView) findViewById(R.id.et_product_price);

        if (this.item != null) {
            productName.setText(this.item.getName());
            productDescription.setText(this.item.getDescription());
            if (this.item.getPrice() != null) {
                productPrice.setText(this.item.getPrice().toString());
            }
        }


        if (this.item != null && this.item.getId() != null) {
            Picasso.get().load(Utility.getBusinessItemImageURL(this.item.getId())).into(img);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

            Uri filePath = data.getData();

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting image to ImageView


                File myFile = new File(filePath.toString());
                //String path = myFile.getAbsolutePath();

                //bitmap = BitmapHelper.decodeFile(getRealPathFromURI(this, filePath), 200, 200, true);
                img.setImageBitmap(bitmap);


                if (filePath.toString().startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(filePath, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (filePath.toString().startsWith("file://")) {
                    filename = myFile.getName();
                }
                photoupload.setText(filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    private void uploadBitmap() {

        if (TextUtils.isEmpty(productName.getText())) {
            Utility.createAlert(this, "Please enter the product name!", "Error");
            return;
        }

        //getting the tag from the edittext
        //final String tags = editTextTags.getText().toString().trim();
        progressDialog = Utility.getProgressDialogue("Saving product ..", this);
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ServiceUtil.ROOT_URL + "updateBusinessItemImage", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                progressDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(new String(response.data), BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlertWithActivityFinish(GenericAddProductActivity.this, "Product saved successfully!", "Done", null, null, null, null);

                } else {
                    Utility.createAlert(GenericAddProductActivity.this, serviceResponse.getResponse(), "Error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Utility.createAlert(GenericAddProductActivity.this, "Error in saving ..", "Error");
            }
        }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //params.put("logo", imageString);
                //params.put("fileName", filename);
                if (item == null) {
                    item = new BillItem();
                }
                item.setName(productName.getText().toString());
                if (!TextUtils.isEmpty(productName.getText())) {
                    item.setDescription(productName.getText().toString());
                }
                item.setPrice(Utility.getDecimal(productPrice));
                params.put("item", ServiceUtil.toJson(item));
                params.put("businessId", user.getCurrentBusiness().getId().toString());
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                if (bitmap != null) {
                    params.put("image", new DataPart(imagename + ".png", BitmapHelper.getFileDataFromDrawable(bitmap)));
                }
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
}