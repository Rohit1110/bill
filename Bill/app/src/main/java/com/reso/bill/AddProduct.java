package com.reso.bill;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import util.Utility;


public class AddProduct extends Fragment {

    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    EditText photoupload;
    ImageView img;
    private String filestring;

    public static AddProduct newInstance() {
        AddProduct fragment = new AddProduct();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_add_product, container, false);
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);
        Utility.AppBarTitle("Add Product", getActivity());
        photoupload = (EditText) rootView.findViewById(R.id.et_product_image);
        img = (ImageView) rootView.findViewById(R.id.selected_img);


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

        return rootView;
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
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                System.out.println("SSSSSSSSSSSS " + data.getData().getPath());

                //aadharNumber.setText(data.getData().getPath());
                String filename = data.getData().getPath().substring(data.getData().getPath().lastIndexOf("/") + 1);
                photoupload.setText(filename);
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                //ImageView imageView = (ImageView) findViewById(R.id.imgView);
                img.setImageBitmap(BitmapFactory.decodeFile(picturePath));


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }
}
