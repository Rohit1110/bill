package com.reso.bill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.generic.GenericDashboard;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillFile;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import model.VolleyMultipartRequest;
import util.BitmapHelper;
import util.ServiceUtil;
import util.Utility;


public class AddBusinessLogo extends Fragment {

    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private EditText photoupload;
    private ImageView img;
    private String filestring;
    private Button save;
    private BillUser user;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private String filename;

    public static AddBusinessLogo newInstance() {
        AddBusinessLogo fragment = new AddBusinessLogo();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_add_product, container, false);
        View rootView = inflater.inflate(R.layout.fragment_add_logo, container, false);
        Utility.AppBarTitle("Business Logo", getActivity());
        photoupload = (EditText) rootView.findViewById(R.id.et_business_logo);
        img = (ImageView) rootView.findViewById(R.id.img_uploaded_logo);


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
                        Utility.checkcontactPermission(getActivity());
                        pickImage();

                        return true;
                    }
                }
                return false;
            }
        });

        save = (Button) rootView.findViewById(R.id.btn_save_business_logo);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadBitmap();
            }
        });

        user = (BillUser) Utility.readObject(getActivity(), Utility.USER_KEY);

        if (user.getCurrentBusiness().getLogo() != null && user.getCurrentBusiness().getLogo().getFileName() != null) {
            Picasso.get().load(ServiceUtil.ROOT_URL + "getImage/logo/" + user.getCurrentBusiness().getId()).into(img);
        }

        return rootView;
    }

    private void saveLogo() {

        progressDialog = Utility.getProgressDialogue("Saving..", getActivity());
        //converting image to base64 string
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();*/
        final String imageString = BitmapHelper.encodeTobase64(bitmap);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServiceUtil.ROOT_URL + "updateBusinessLogo", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlert(getActivity(), "Business logo uploaded successfully!", "Done");
                    BillFile logo = new BillFile();
                    logo.setFileName(filename);
                    user.getCurrentBusiness().setLogo(logo);
                    //Utility.writeObject(getActivity(), Utility.USER_KEY, serviceResponse.getUser());
                    updateParentLogo();

                } else {
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Utility.createAlert(getActivity(), "Error while uploading the image!", "Error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("logo", imageString);
                params.put("fileName", filename);
                BillUser currentUser = Utility.getBasicUser(user);
                params.put("user", ServiceUtil.toJson(currentUser));
                return params;
            }

        };

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);

    }

    private void updateParentLogo() {
        if (getActivity() != null) {
            if (getActivity() instanceof GenericDashboard) {
                GenericDashboard activity = (GenericDashboard) getActivity();
                activity.updateBusinessLogo(user);
            } else {
                GenericDashboard activity = (GenericDashboard) getActivity();
                activity.updateBusinessLogo(user);
            }
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
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting image to ImageView


                File myFile = new File(filePath.toString());
                //String path = myFile.getAbsolutePath();

                //bitmap = BitmapHelper.decodeFile(getRealPathFromURI(getActivity(), filePath), 200, 200, true);
                img.setImageBitmap(bitmap);


                if (filePath.toString().startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getActivity().getContentResolver().query(filePath, null, null, null, null);
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

            /*try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                System.out.println("SSSSSSSSSSSS " + data.getData().getPath());

                //aadharNumber.setText(data.getData().getPath());
                filename = data.getData().getPath().substring(data.getData().getPath().lastIndexOf("/") + 1);
                photoupload.setText(filename);
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                //ImageView imageView = (ImageView) findViewById(R.id.imgView);
                bitmap = BitmapFactory.decodeFile(picturePath);
                img.setImageBitmap(bitmap);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("ERROR", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void uploadBitmap() {

        //getting the tag from the edittext
        //final String tags = editTextTags.getText().toString().trim();
        progressDialog = Utility.getProgressDialogue("Saving logo ..", getActivity());
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ServiceUtil.ROOT_URL + "updateBusinessLogo", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                progressDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(new String(response.data), BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlert(getActivity(), "Business logo uploaded successfully!", "Done");
                    BillFile logo = new BillFile();
                    logo.setFileName(filename);
                    user.getCurrentBusiness().setLogo(logo);
                    //Utility.writeObject(getActivity(), Utility.USER_KEY, serviceResponse.getUser());
                    updateParentLogo();
                } else {
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Utility.createAlert(getActivity(), "Error in uploading logo ..", "Error");
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
                BillUser currentUser = Utility.getBasicUser(user);
                params.put("user", ServiceUtil.toJson(currentUser));
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("logo", new DataPart(imagename + ".png", BitmapHelper.getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);
    }


}
