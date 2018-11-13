package util;

import android.app.Activity;
import android.app.ProgressDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.rns.web.billapp.service.bo.domain.BillSector;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Admin on 24/05/2018.
 */

public class ServiceUtil {

    //public static String HOST = "http://payperbill.in:8080/billapp/"; //PROD IMP
    public static String HOST = "http://192.168.1.16:8080/billapp-service/";
    public static String ROOT_URL = HOST + "user/";
    public static String ADMIN_URL = HOST + "admin/";


    //public static final int SECTOR_ID = 2;
    public static final int SECTOR_ID = 4; //For Prod

    public static BillSector NEWSPAPER_SECTOR = new BillSector(SECTOR_ID);

    public static StringRequest getStringRequest(String method, com.android.volley.Response.Listener<String> successListener, Response.ErrorListener errorListener, final BillServiceRequest request) {
        StringRequest volleyRequest = getRequest(ROOT_URL + method, successListener, errorListener, request);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return volleyRequest;
    }

    /*public static StringRequest getAdminStringRequest(String method, Response.Listener<String> successListener, Response.ErrorListener errorListener, final ChatServiceRequest request) {
        return getRequest(ROOT_ADMIN_URL + method, successListener, errorListener, request);
    }*/

    public static StringRequest getRequest(final String method, Response.Listener<String> successListener, Response.ErrorListener errorListener, final BillServiceRequest request) {
        //System.out.print("Calling method " + method);
        return new StringRequest(Request.Method.POST, method, successListener, errorListener) {

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                String str = toJson(request);
                System.out.println("Method -- " + method + " -- Request == " + str);
                return str.getBytes();
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

    }

    public static String toJson(Object object) {
        System.out.println("Convering to JSON");
        return gsonBuilder().toJson(object);
    }

    public static Response.ErrorListener createMyReqErrorListener(final ProgressDialog pDialog, final Activity context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(pDialog != null) {
                    pDialog.dismiss();
                }
                String e = "";
                if(error != null) {
                    e = error.getMessage();
                }
                System.out.println("Error in executing service call .. " + error);
                Utility.createAlert(context, "Error connecting server! Please check your network connection ..", "Error");
            }
        };
    }

    public static Object fromJson(String response, Class<BillServiceResponse> billServiceResponseClass) {
        return gsonBuilder().fromJson(response, billServiceResponseClass);
    }

    public static Gson gsonBuilder() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
        //builder.setDateFormat(DateFormat.LONG);
        builder.registerTypeAdapter(Date.class, new JsonDateSerializer());
        return builder.create();
    }

    public static class JsonDateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String s = json.getAsJsonPrimitive().getAsString();
            long l = Long.parseLong(s);
            Date d = new Date(l);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                d = sdf.parse(sdf.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.print("Date --- " + d);
            return d;
        }
    }

    public static class JsonDateSerializer implements JsonSerializer<Date> {

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonPrimitive(src.getTime());
        }
    }
}
