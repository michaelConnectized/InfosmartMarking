package com.example.user.markingactivity.baseAdapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.markingactivity.R;
import com.example.user.markingactivity.baseAdapter.bleAdapter;
import com.example.user.markingactivity.object.mDevice;

import java.util.ArrayList;

public class confirmAdapter extends bleAdapter {

    public confirmAdapter(Activity ctx, ArrayList<mDevice> mDevs) {
        super(ctx, mDevs);
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        mDevice currentDevice = (mDevice)getItem(position);
        View confirmView = super.getView(position, view, viewGroup);
        confirmView.findViewById(R.id.chk_check).setVisibility(View.GONE);
//        ((TextView)confirmView.findViewById(R.id.tv_uuid)).setText(currentDevice.getmUUID());

        Button btn_edit_txPwr = confirmView.findViewById(R.id.btn_edit_txPwr);
        btn_edit_txPwr.setVisibility(View.GONE);

        return confirmView;
    }
}