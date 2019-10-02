package com.rns.web.billapp.service.domain;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.rns.web.billapp.service.bo.domain.BillUser;

import java.io.Serializable;
import java.util.List;

import util.Utility;

/**
 * Created by Admin on 21/12/2018.
 */

public class BillCache implements Serializable {

    public static final String BILL_CACHE_KEY = "BillCache";
    private List<BillUser> deliveries;
    private List<BillUser> customers;

    public BillCache() {
    }

    public BillCache(List<BillUser> deliveries, List<BillUser> customers) {
        this.deliveries = deliveries;
        this.customers = customers;
    }


    public List<BillUser> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<BillUser> deliveries) {
        this.deliveries = deliveries;
    }

    public void setCustomers(List<BillUser> customers) {
        this.customers = customers;
    }

    public List<BillUser> getCustomers() {
        return customers;
    }

    public static void addDeliveries(List<BillUser> deliveries, Activity activity) {
        BillCache cache = getBillCache(activity);
        cache.setDeliveries(deliveries);
        //Utility.writeObject(activity, BILL_CACHE_KEY, cache);
        saveCache(activity, cache);
    }

    @NonNull
    private static BillCache getBillCache(Activity activity) {
        BillCache cache = readCache(activity);
        if (cache == null) {
            cache = new BillCache();
        }
        return cache;
    }

    private static BillCache readCache(Activity activity) {
        //return (BillCache) Utility.readObject(activity, BILL_CACHE_KEY);
        return (BillCache) Utility.readFromSharedPref(activity, BILL_CACHE_KEY, BillCache.class);
    }

    public static List<BillUser> getDeliveries(Activity activity) {
        long start = System.currentTimeMillis();
        BillCache cache = readCache(activity);
        long end = System.currentTimeMillis();
        System.out.println("Time => " + (end - start));
        if (cache == null) {
            return null;
        }
        return cache.getDeliveries();
    }

    public static void addCustomers(List<BillUser> customers, Activity activity) {
        BillCache cache = getBillCache(activity);
        cache.setCustomers(customers);
        //Utility.writeObject(activity, BILL_CACHE_KEY, cache);
        saveCache(activity, cache);
    }

    private static void saveCache(Activity activity, BillCache cache) {
        Utility.saveToSharedPref(activity, BILL_CACHE_KEY, cache);
    }

    public static List<BillUser> getCustomers(Activity activity) {
        BillCache cache = readCache(activity);
        if (cache == null) {
            return null;
        }
        return cache.getCustomers();
    }

}
