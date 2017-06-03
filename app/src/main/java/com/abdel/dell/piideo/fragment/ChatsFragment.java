package com.abdel.dell.piideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdel.dell.piideo.R;
import com.abdel.dell.piideo.activity.PiideoActivity;

/**
 * Created by Abdel on 25/02/2017.
 */

public class ChatsFragment extends Fragment {
    private AppCompatButton toPiideoBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toPiideoBtn = (AppCompatButton) view.findViewById(R.id.toPiideoBtn);
        toPiideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PiideoActivity.class));
            }
        });
    }

}
