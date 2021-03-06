package com.rns.web.billapp.service.util;

public interface BillConstants {

    String ROOT_FOLDER_LOCATION = "/home/service/BillData/";

    String DATE_FORMAT = "yyyy-MM-dd";
    String DATE_FORMAT_DISPLAY_NO_YEAR = "MMM dd";
    String DATE_FORMAT_DISPLAY_NO_YEAR_TIME = "MMM dd hh:mm";
    Integer STATUS_OK = 200;
    String RESPONSE_OK = "OK";
    String REQUEST_TYPE_EMAIL = "EMAIL";

    Integer ERROR_CODE_FATAL = -999;
    Integer ERROR_CODE_GENERIC = -111;
    Integer ERROR_NOT_APPROVED = -222;
    int WARNING_CODE_1 = 101;
    int WARNING_CODE_2 = 102;
    int WARNING_CODE_3 = 103;
    int WARNING_CODE_4 = 104;
    String PAYMENT_MEDIUM_HDFC = "HDFC";
    String PAYMENT_MEDIUM_INSTA = "INSTA";


    String WARNING_NO_BUSINESS = "No business information found. Please complete your business details.";
    String WARNING_NO_FINANCIALS = "No financial/ bank information found. Please complete your bank details to enable payment.";

    String ERROR_INSUFFICIENT_FIELDS = "Insufficient fields! Please send all the required fields!";
    String ERROR_NO_USER = "User profile was not found!";
    String ERROR_IN_PROCESSING = "Some error occurred while processing ..";
    String ERROR_MOBILE_PRESENT = "The mobile number is already registered!";
    String ERROR_INVALID_ITEM = "The product you selected does not exist!";
    String ERROR_USER_NOT_APPROVED = "Your profile is not approved by the Admin team yet. You'll get a confirmation soon as we do.";
    String ERROR_INVOICE_NOT_FOUND = "Invalid invoice details!";

    String MSG_REGISTERED_SUCCESS = "Your profile has been registered successfully! Our team will review your profile and contact you further to approve it.";

    double PAYMENT_CHARGE_PERCENT = 0.02;
    double PAYMENT_CHARGE_FIXED = 3;
    String PAYMENT_ONLINE = "Online";
    String PAYMENT_OFFLINE = "Offline";
    String PAYMENT_STATUS_CREDIT = "Credit";

    String STATUS_DELETED = "D";
    String STATUS_ACTIVE = "A";
    String STATUS_PENDING = "P";
    String INVOICE_STATUS_PENDING = "Pending";
    String INVOICE_STATUS_PAID = "Credit";
    String INVOICE_STATUS_DELETED = "Deleted";

    String USER_DB_ATTR_PHONE = "phone";
    String ID_ATTR = "id";
    String LOG_CHANGE_TEMP = "TEMP";
    String LOG_CHANGE_PERM = "PERM";
    String FREQ_DAILY = "DAILY";
    String FREQ_MONTHLY = "MONTHLY";
    String FREQ_WEEKLY = "WEELKLY";

    String MAIL_TYPE_INVOICE = "InvoiceMail";
    String MAIL_TYPE_PAYMENT_RESULT = "PaymentMail";
    String MAIL_TYPE_PAYMENT_RESULT_VENDOR = "PaymentMailVendor";
    String MAIL_TYPE_REGISTRATION = "RegistrationSuccess";
    String MAIL_TYPE_APPROVAL = "ProfileApproved";
    String MAIL_TYPE_NEW_CUSTOMER = "CustomerAdded";
    String MAIL_TYPE_PAUSE_CUSTOMER = "PauseDeliveryCustomer";
    String MAIL_TYPE_PAUSE_BUSINESS = "PauseDeliveryBusiness";

    String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    String FRAMEWORK_RECURRING = "RECURRING";
    String FRAMEWORK_GENERIC = "GENERIC";


}
