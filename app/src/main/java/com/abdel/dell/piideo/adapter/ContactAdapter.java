package com.abdel.dell.piideo.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abdel.dell.piideo.R;
import com.abdel.dell.piideo.model.Contact;

import java.util.ArrayList;

/**
 * Created by DELL on 14/05/2017.
 */

public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(@NonNull Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contact contact = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_contact_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        TextView tvPhone = (TextView) convertView.findViewById(R.id.phone);
        // Populate the data into the template view using the data object
        tvName.setText(contact.getName());
        tvPhone.setText(contact.getPhone());
        // Return the completed view to render on screen
        return convertView;
    }
}