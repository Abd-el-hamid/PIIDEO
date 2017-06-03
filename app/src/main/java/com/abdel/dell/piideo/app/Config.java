package com.abdel.dell.piideo.app;

/**
 * Created by Ravi on 08/07/15.
 */
public class Config {

    // server URL configuration
    public static final String URL_SERVER = "http://hamidlz.altervista.org/piideo";
    public static final String URL_REQUEST_SMS = URL_SERVER + "/sms_verification/msg91/request_sms.php";
    public static final String URL_VERIFY_OTP = URL_SERVER + "/sms_verification/msg91/verify_otp.php";
    public static final String URL_VERIFY_CONTACTS = URL_SERVER+ "/verify_contacts.php";

    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "MSGIND";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
}
