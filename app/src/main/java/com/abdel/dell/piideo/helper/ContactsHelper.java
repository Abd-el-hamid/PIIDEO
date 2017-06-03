package com.abdel.dell.piideo.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.abdel.dell.piideo.model.Contact;

import java.util.ArrayList;

/**
 * Created by DELL on 14/05/2017.
 */

public class ContactsHelper {

    public static ArrayList<Contact> getContacts(Context context) {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        String phoneNumber = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        StringBuffer output;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            int counter = 0;
            while (cursor.moveToNext()) {
                output = new StringBuffer();
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Contact contact = new Contact();
//                    output.append("\n First Name:" + name);
                    contact.setName(name);

                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
//                        output.append("\n Phone number:" + phoneNumber);
                        contact.setPhone(phoneNumber);
                    }
                    phoneCursor.close();

                    // Add the contact to the ArrayList
//                    output.toString()
                    contactList.add(contact);
                }
            }
        }
        cursor.close();
        return contactList;
    }
}
