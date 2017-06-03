package com.abdel.dell.piideo.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.abdel.dell.piideo.R;
import com.abdel.dell.piideo.adapter.ContactAdapter;
import com.abdel.dell.piideo.app.Config;
import com.abdel.dell.piideo.app.MyApplication;
import com.abdel.dell.piideo.helper.ContactsHelper;
import com.abdel.dell.piideo.model.Contact;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abdel on 25/02/2017.
 */

public class ContactsFragment extends Fragment {

    private final String TAG = Fragment.class.getSimpleName();
    private ArrayList<Contact> contacts;
    private ListView mListView;
    private ContactAdapter contactAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_contacts, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mListView = (ListView) view.findViewById(R.id.listContact);
        super.onViewCreated(view, savedInstanceState);

        contacts = ContactsHelper.getContacts(getContext());

        JSONObject ctcJsObj = toJsonObj(contacts);
        verifyContacts(ctcJsObj);

    }

    private JSONObject toJsonObj(List<Contact> contacts) {
        JSONObject ctcJsObj = new JSONObject();

        JSONArray ctcJsnArr = new JSONArray();
        for (Contact c : contacts) {
            try {
                JSONObject jsnObj = new JSONObject();
                jsnObj.put("phone", c.getPhone());
                jsnObj.put("name", c.getName());
                jsnObj.put("exist", c.isExist());

                ctcJsnArr.put(jsnObj);

            } catch (JSONException ex) {
                Log.e("json exception", ex.toString());
            }
        }

        try {
            ctcJsObj.put("contacts", ctcJsnArr);
        } catch (JSONException ex) {
            Log.e("json exception", ex.toString());
        }

        return ctcJsObj;
    }

    /**
     * Method to verify the contacts that have the app installed
     *
     * @param listContacts phone list of contacts
     */
    private void verifyContacts(final JSONObject listContacts) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, Config.URL_VERIFY_CONTACTS, listContacts,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        System.out.println("****response: " + response.toString());
                        ArrayList<Contact> existContacts = new ArrayList<>();
                        try {
                            JSONArray contactsJsnArr = response.getJSONArray("contacts");
                            for (int i = 0; i < contactsJsnArr.length(); i++) {
                                boolean exist = contactsJsnArr.getJSONObject(i).getBoolean("exist");
                                if (exist) {
                                    String phone = contactsJsnArr.getJSONObject(i).getString("phone");
                                    for (Contact contact : contacts) {
                                        if (contact.getPhone().equals(phone)) {
//                                            contacts.remove(contact);
                                            existContacts.add(contact);
                                        }
                                    }
                                }
                            }
                            for (Contact c : contacts) {
                                System.out.println(c.toString());
                            }
                            contactAdapter = new ContactAdapter(getContext(), existContacts);
                            mListView.setAdapter(contactAdapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();

            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };


        int socketTimeout = 20000;//60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

}
