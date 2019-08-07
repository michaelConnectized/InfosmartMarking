package com.example.user.markingactivity.baseAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.beaconplusdemo.BeaconPlusManager;
import com.beaconplusdemo.Landmark;
import com.example.user.markingactivity.R;
import com.example.user.markingactivity.object.mDevice;

import java.util.ArrayList;

public class bleAdapter extends BaseAdapter {
    private Activity activity;
    private Context ctx;
    private ArrayList<mDevice> mDevs;
    private ArrayList<Integer> viewIDs;
    private int count;
    private ArrayList<Boolean> mDevCheckedIndex;
    final ProgressDialog progressDialog;
    private BeaconPlusManager beaconPlusManager;

    CheckBox chk_check;

    EditText et_r1;
    EditText et_r2;
    EditText et_r3;

    SharedPreferences sp;

    public bleAdapter(Activity activity, ArrayList<mDevice> mDevs) {
        this.activity = activity;
        this.ctx = activity;
        this.mDevs = mDevs;
        viewIDs = new ArrayList<Integer>();


        progressDialog = new ProgressDialog(activity);
        mDevCheckedIndex = new ArrayList<Boolean>();
    }
    @Override
    public int getCount() {
        return mDevs.size();
    }

    @Override
    public Object getItem(int i) {
        return mDevs.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.ble_dropdown_item, viewGroup, false);;
        }
        mDevice currentDevice = (mDevice)getItem(position);
//        TextView tv_uuid = view.findViewById(R.id.tv_uuid);
//        TextView tv_name = view.findViewById(R.id.tv_name);
//        TextView tv_address = view.findViewById(R.id.tv_address);
        TextView tv_major = view.findViewById(R.id.tv_major);
        TextView tv_minor = view.findViewById(R.id.tv_minor);
        TextView tv_rssi = view.findViewById(R.id.tv_rssi);
//        TextView tv_pwr = view.findViewById(R.id.tv_pwr);
        TextView tv_txpwr = view.findViewById(R.id.tv_txpwr);

        CheckBox chk_check = view.findViewById(R.id.chk_check);
        RelativeLayout relLay = view.findViewById(R.id.relLay);

//        tv_name.setText(currentDevice.getName());
//        tv_address.setText(currentDevice.getAddress());
        tv_major.setText(String.valueOf(currentDevice.getMajor()));
        tv_minor.setText(String.valueOf(currentDevice.getMinor()));
        tv_rssi.setText(String.valueOf(currentDevice.getRSSI()));
//        tv_pwr.setText(String.valueOf(currentDevice.getBatteryLevel()));
        tv_txpwr.setText(String.valueOf(currentDevice.getPower()));

        chk_check.setTag(position);
//        if (tv_uuid!=null) {
//            tv_uuid.setText(currentDevice.getUUID().toString());
//        }

        return view;
    }

    public void refleshLayout(Context ctx, View view, boolean show) {
        ViewGroup mScrollView = (ViewGroup) view.getParent();
        int tmpId = view.getId();
        mScrollView.removeView(view);
        viewIDs.clear();
        count = 0;
        sp = PreferenceManager.getDefaultSharedPreferences(ctx);

        LinearLayout newLinearView = new LinearLayout(ctx);
        newLinearView.setOrientation(LinearLayout.VERTICAL);
        newLinearView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        newLinearView.setId(tmpId);
        for (mDevice mDev: mDevs) {
            View tmpView = LayoutInflater.from(ctx).inflate(R.layout.ble_dropdown_item, newLinearView, false);
            mDevice currentDevice = (mDevice)getItem(count);
//            TextView tv_uuid = tmpView.findViewById(R.id.tv_uuid);
//            TextView tv_name = tmpView.findViewById(R.id.tv_name);
//            TextView tv_address = tmpView.findViewById(R.id.tv_address);
            TextView tv_major = tmpView.findViewById(R.id.tv_major);
            TextView tv_minor = tmpView.findViewById(R.id.tv_minor);
//            TextView tv_pwr = tmpView.findViewById(R.id.tv_pwr);
            TextView tv_txpwr = tmpView.findViewById(R.id.tv_txpwr);
            TextView tv_rssi = tmpView.findViewById(R.id.tv_rssi);
            chk_check = tmpView.findViewById(R.id.chk_check);
            if (!show) {
                chk_check.setVisibility(View.GONE);
            } else {
                chk_check.setVisibility(View.VISIBLE);
            }

            //Button for show details
            Button btn_details1 = tmpView.findViewById(R.id.btn_details);
            Dialog detailsDialog1 = new Dialog(activity);
            detailsDialog1.setContentView(R.layout.dialog_ble_details);

            if (currentDevice.getUUID()!=null) {
                ((TextView)detailsDialog1.findViewById(R.id.tv_uuid)).setText(currentDevice.getUUID().toString());
            }

            ((TextView)detailsDialog1.findViewById(R.id.tv_name)).setText(currentDevice.getName());
            ((TextView)detailsDialog1.findViewById(R.id.tv_address)).setText(currentDevice.getAddress());
            ((TextView)detailsDialog1.findViewById(R.id.tv_major)).setText(String.valueOf(currentDevice.getMajor()));
            ((TextView)detailsDialog1.findViewById(R.id.tv_minor)).setText(String.valueOf(currentDevice.getMinor()));
            ((TextView)detailsDialog1.findViewById(R.id.tv_txpwr)).setText(String.valueOf(currentDevice.getPower()));;
            ((TextView)detailsDialog1.findViewById(R.id.tv_rssi)).setText(String.valueOf(currentDevice.getRSSI()));
            if (currentDevice.getBatteryLevel()!=999) {
                ((TextView)detailsDialog1.findViewById(R.id.tv_pwr)).setText(String.valueOf(currentDevice.getBatteryLevel()));
            } else {
                ((TextView)detailsDialog1.findViewById(R.id.tv_pwr)).setText("N/A");
            }

            detailsDialog1.setTitle(String.valueOf(currentDevice.getMajor())+"."+String.valueOf(currentDevice.getMinor()));
            btn_details1.setOnClickListener((v)->detailsDialog1.show());

//            tv_name.setText(currentDevice.getName());
//            tv_address.setText(currentDevice.getAddress());
            tv_rssi.setText(String.valueOf(currentDevice.getRSSI()));
            tv_major.setText(String.valueOf(currentDevice.getMajor()));
            tv_minor.setText(String.valueOf(currentDevice.getMinor()));
//            if (currentDevice.getBatteryLevel()!=999) {
//                tv_pwr.setText(String.valueOf(currentDevice.getBatteryLevel()));
//            } else {
//                tv_pwr.setText("N/A");
//            }

            tv_txpwr.setText(String.valueOf(currentDevice.getPower()));
//            if (currentDevice.getUUID()!=null) {
//                tv_uuid.setText(currentDevice.getUUID().toString());
//            }

            chk_check.setTag(count);
            chk_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        buttonView.setClickable(false);
                        if (isChecked) {
                            mDevCheckedIndex.set((int)buttonView.getTag(), true);
                        } else {
                            mDevCheckedIndex.set((int)buttonView.getTag(), false);
                        }
                         new Handler().postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 buttonView.setClickable(true);
                             }
                         }, 300);
                    }
            });

            //Button for edit tx pwr
            Button btn_edit_txPwr = tmpView.findViewById(R.id.btn_edit_txPwr);
            btn_edit_txPwr.setTag(R.id.tv_address);

            viewIDs.add(ViewCompat.generateViewId());
            tmpView.setId(viewIDs.get(count));

            btn_edit_txPwr.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ctx);
                    //dlgAlert.set

                    TableLayout.LayoutParams outerLayoutParam = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                    TableLayout outerLayout = new TableLayout(ctx);
                    outerLayout.setPadding(15, 15, 15, 15);
                    outerLayout.setLayoutParams(outerLayoutParam);

                    TableRow.LayoutParams tableRowParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    TableRow firstRow = new TableRow(ctx);
                    firstRow.setPadding(15, 15, 15, 15);
                    firstRow.setLayoutParams(tableRowParam);
                    TableRow secondRow = new TableRow(ctx);
                    secondRow.setPadding(15, 15, 15, 15);
                    secondRow.setLayoutParams(tableRowParam);
                    TableRow thirdRow = new TableRow(ctx);
                    thirdRow.setPadding(15, 15, 15, 15);
                    thirdRow.setLayoutParams(tableRowParam);

                    TableRow.LayoutParams txParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    TextView tv_r1 = new TextView(ctx);
                    tv_r1.setText("Address: ");
                    tv_r1.setLayoutParams(txParam);
                    TextView tv_r2 = new TextView(ctx);
                    tv_r2.setText("txPower: ");
                    tv_r2.setLayoutParams(txParam);
                    TextView tv_r3 = new TextView(ctx);
                    tv_r3.setText("Password: ");
                    tv_r3.setLayoutParams(txParam);



                    TableRow.LayoutParams etParam1 = new TableRow.LayoutParams((int)ctx.getResources().getDimension(R.dimen.edit_txPower_et), TableRow.LayoutParams.WRAP_CONTENT);
                    TableRow.LayoutParams etParam2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    et_r1 = new EditText(ctx);
                    et_r1.setLayoutParams(etParam2);
                    et_r1.setText(currentDevice.getAddress());
                    et_r1.setEnabled(false);
                    et_r2 = new EditText(ctx);
                    et_r2.setLayoutParams(etParam1);
                    et_r2.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
                    et_r2.setText(String.valueOf(currentDevice.getPower()));
                    et_r3 = new EditText(ctx);
                    et_r3.setLayoutParams(etParam2);
                    et_r3.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_r3.setText(sp.getString("beacon_password", ""));

                    firstRow.addView(tv_r1);
                    firstRow.addView(et_r1);
                    secondRow.addView(tv_r2);
                    secondRow.addView(et_r2);
                    thirdRow.addView(tv_r3);
                    thirdRow.addView(et_r3);

                    outerLayout.addView(firstRow);
                    outerLayout.addView(secondRow);
                    outerLayout.addView(thirdRow);

                    dlgAlert.setView(outerLayout);

                    dlgAlert.setTitle("Edit Tx Power");

                    dlgAlert.setPositiveButton("Confirm", (dialog, id) -> {
                        try {
                            //TODO EDIT TX POWER

                            Landmark lm = new Landmark(et_r1.getText().toString(), et_r2.getText().toString(), et_r3.getText().toString());
                            beaconPlusManager = BeaconPlusManager.EditTxPwrManager(activity, lm);
                            beaconPlusManager.startBeaconScan();

                            progressDialog.setMessage("Connecting to \n"+beaconPlusManager.getLandmark().getMacAddr()+"...");
                            progressDialog.setIndeterminate(true);

                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(false);
                            beaconPlusManager.setOnConnectionStatusChangedListener(status -> {
                                progressDialog.dismiss();

                                switch (status) {
                                    case CONNECT_FAIL:
                                        showConnectionFailedDialog();
                                        break;
                                    case CHANGE_SUCCESS:
                                        showChangedSuccessfulDialog();
                                        sp.edit().putString("beacon_password", beaconPlusManager.getLandmark().getPwd()).commit();

                                        ((TextView)tmpView.findViewById(R.id.tv_newTxpwr)).setText("->"+beaconPlusManager.getLandmark().getTxPwr());
                                        break;
                                    case PASSWORD_VALID_TIMEOUT:
                                        showPasswordValidTimeoutDialog();
                                        break;
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(ctx, "Sorry, there are some errors occurred.", Toast.LENGTH_LONG).show();
                        }
                    });

                    dlgAlert.setNegativeButton("Cancel", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                }
            });

            //Button for show details
            Button btn_details = tmpView.findViewById(R.id.btn_details);
            Dialog detailsDialog = new Dialog(activity);
            detailsDialog.setContentView(R.layout.dialog_ble_details);

            if (currentDevice.getUUID()!=null) {
                ((TextView)detailsDialog.findViewById(R.id.tv_uuid)).setText(currentDevice.getUUID().toString());
            }

            ((TextView)detailsDialog.findViewById(R.id.tv_name)).setText(currentDevice.getName());
            ((TextView)detailsDialog.findViewById(R.id.tv_address)).setText(currentDevice.getAddress());
            ((TextView)detailsDialog.findViewById(R.id.tv_major)).setText(String.valueOf(currentDevice.getMajor()));
            ((TextView)detailsDialog.findViewById(R.id.tv_minor)).setText(String.valueOf(currentDevice.getMinor()));
            ((TextView)detailsDialog.findViewById(R.id.tv_txpwr)).setText(String.valueOf(currentDevice.getPower()));;
            ((TextView)detailsDialog.findViewById(R.id.tv_rssi)).setText(String.valueOf(currentDevice.getRSSI()));
            if (currentDevice.getBatteryLevel()!=999) {
                ((TextView)detailsDialog.findViewById(R.id.tv_pwr)).setText(String.valueOf(currentDevice.getBatteryLevel()));
            } else {
                ((TextView)detailsDialog.findViewById(R.id.tv_pwr)).setText("N/A");
            }

            detailsDialog.setTitle(String.valueOf(currentDevice.getMajor())+"."+String.valueOf(currentDevice.getMinor()));
            btn_details.setOnClickListener((v)->detailsDialog.show());


            newLinearView.addView(tmpView);
            count++;
        }
        mScrollView.addView(newLinearView);
    }

    private void showPasswordValidTimeoutDialog() {
        AlertDialog.Builder localDialog = new AlertDialog.Builder(ctx);
        localDialog.setTitle("Password Validation Timeout");
        localDialog.setMessage("Would you like to connect again?");
        localDialog.setPositiveButton("Yes", (dialog, id) -> {
            progressDialog.show();
            beaconPlusManager.tryConnect();
            dialog.dismiss();
        });
        localDialog.setNegativeButton("No", (dialog, id) -> {
           dialog.dismiss();
        });
        localDialog.show();
    }

    private void showConnectionFailedDialog() {
        AlertDialog.Builder localDialog = new AlertDialog.Builder(ctx);
        localDialog.setTitle("Connection Failed!");
        localDialog.setMessage("Would you like to connect again?");
        localDialog.setPositiveButton("Yes", (dialog, id) -> {
            progressDialog.show();
            beaconPlusManager.tryConnect();
            dialog.dismiss();
        });
        localDialog.setNegativeButton("No", (dialog, id) -> {
            dialog.dismiss();
        });
        localDialog.show();
    }

    private void showChangedSuccessfulDialog() {
        try {
            AlertDialog.Builder localDialog = new AlertDialog.Builder(activity.getApplicationContext());
            localDialog.setMessage("Change Successful!");
            localDialog.setNeutralButton("Yes", (dialog, id) -> {
                dialog.dismiss();
            });
            localDialog.show();

        } catch (Exception e) {};
    }

    public ArrayList<Boolean> getmDevCheckedIndex() {
        return mDevCheckedIndex;
    }

    public void setmDevCheckedIndex(ArrayList<Boolean> mDevCheckedIndex) {
        this.mDevCheckedIndex = mDevCheckedIndex;
    }
}
