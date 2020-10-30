package com.bestapk.petukvai.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.bestapk.petukvai.R;
import com.bestapk.petukvai.helper.ApiConfig;
import com.bestapk.petukvai.helper.Constant;
import com.bestapk.petukvai.helper.DatabaseHelper;
import com.bestapk.petukvai.helper.Session;
import com.bestapk.petukvai.helper.VolleyCallback;
import com.bestapk.petukvai.model.Slot;

@SuppressLint("SetTextI18n")
public class CheckoutActivity extends AppCompatActivity implements OnMapReadyCallback, PaymentResultListener {
    private ArrayList<Slot> timeSlots = new ArrayList<>();
    private String TAG = CheckoutActivity.class.getSimpleName();
    public Toolbar toolbar;
    public TextView tvTaxPercent, tvTaxAmt, tvDelivery, tvPayment, tvLocation, tvAlert, tvWltBalance, tvCity;
    public TextView tvName, tvTotal, tvDeliveryCharge, tvSubTotal, tvCurrent, tvWallet, tvPromoCode, tvPCAmount;
    public TextView tvPlaceOrder, tvConfirmOrder, tvPreTotal, tvOrderNotes;
    TextView totalPayInBkash, totalPayInRocket, totalPayInNagad;
    TextView bkashCharge, rocketCharge, nagadCharge;
    LinearLayout lytPayOption, lytTax, lytOrderList, lytWallet, lytCLocation, paymentLyt, deliveryLyt;
    LinearLayout lytBkashPay, lytNagadPay, lytRocketPay, dayLyt;
    Button btnApply;
    EditText edtPromoCode, edtBkashMob, edtBkashTxid, edtRocketMob, edtRocketTxid, edtNagadMob, edtNagadTxid;
    public ProgressBar prgLoading;
    Session session;
    JSONArray qtyList, variantIdList, nameList;
    DatabaseHelper databaseHelper;
    double total, subtotal;
    String deliveryCharge = "0";
    SupportMapFragment mapFragment;
    CheckBox chWallet, chHome, chWork;
    public RadioButton rToday, rTomorrow;

    String deliveryTime = "", deliveryDay = "", pCode = "", paymentMethod = "", label = "", appliedCode = "";
    RadioButton rbCod, rbBkashPay, rbNagadPay, rbRocketPay;
    ProgressDialog mProgressDialog;
    RelativeLayout walletLyt, mainLayout;
    Map<String, String> razorParams;
    public String razorPayId;
    double usedBalance = 0;
    RecyclerView recyclerView;
    ArrayList<Slot> slotList;
    SlotAdapter adapter;
    ProgressBar pBar;
    public boolean isApplied;
    double taxAmt = 0.0;
    double dCharge = 0.0, pCodeDiscount = 0.0;

    String transectionId = "";
    String transectionMobile = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mainLayout = findViewById(R.id.mainLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        paymentModelClass = new PaymentModelClass(CheckoutActivity.this);
        databaseHelper = new DatabaseHelper(CheckoutActivity.this);
        session = new Session(CheckoutActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        pBar = findViewById(R.id.pBar);
        lytTax = findViewById(R.id.lytTax);
        tvTaxAmt = findViewById(R.id.tvTaxAmt);
        tvTaxPercent = findViewById(R.id.tvTaxPercent);
        dayLyt = findViewById(R.id.dayLyt);
        rbCod = findViewById(R.id.rbcod);
        rbBkashPay = findViewById(R.id.rbBkashPay);
        rbNagadPay = findViewById(R.id.rbNagadPay);
        rbRocketPay = findViewById(R.id.rbRocketPay);
        tvDelivery = findViewById(R.id.tvDelivery);
        tvPayment = findViewById(R.id.tvPayment);
        tvPCAmount = findViewById(R.id.tvPCAmount);
        tvPromoCode = findViewById(R.id.tvPromoCode);
        tvAlert = findViewById(R.id.tvAlert);
        edtPromoCode = findViewById(R.id.edtPromoCode);
        lytBkashPay = findViewById(R.id.lytBkashPay);
        lytRocketPay = findViewById(R.id.lytRocketPay);
        lytNagadPay = findViewById(R.id.lytNagadPay);

        totalPayInBkash = findViewById(R.id.totalPayInBkash);
        bkashCharge = findViewById(R.id.bkashCharge);

        totalPayInRocket = findViewById(R.id.totalPayInRocket);
        rocketCharge = findViewById(R.id.rocketCharge);

        totalPayInNagad = findViewById(R.id.totalPayInNagad);
        nagadCharge = findViewById(R.id.nagadCharge);

        chWallet = findViewById(R.id.chWallet);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotal = findViewById(R.id.tvTotal);
        tvName = findViewById(R.id.tvName);
        tvCity = findViewById(R.id.tvCity);
        tvCurrent = findViewById(R.id.tvCurrent);
        lytPayOption = findViewById(R.id.lytPayOption);
        lytOrderList = findViewById(R.id.lytOrderList);
        lytCLocation = findViewById(R.id.lytCLocation);
        lytWallet = findViewById(R.id.lytWallet);
        walletLyt = findViewById(R.id.walletLyt);
        paymentLyt = findViewById(R.id.paymentLyt);

        edtBkashMob = findViewById(R.id.edtBkashMob);
        edtBkashTxid = findViewById(R.id.edtBkashTxid);
        edtRocketMob = findViewById(R.id.edtRocketMob);
        edtRocketTxid = findViewById(R.id.edtRocketTxid);
        edtNagadMob = findViewById(R.id.edtNagadMob);
        edtNagadTxid = findViewById(R.id.edtNagadTxid);

        deliveryLyt = findViewById(R.id.deliveryLyt);
        tvWallet = findViewById(R.id.tvWallet);
        prgLoading = findViewById(R.id.prgLoading);
        tvPlaceOrder = findViewById(R.id.tvPlaceOrder);
        tvConfirmOrder = findViewById(R.id.tvConfirmOrder);
        tvOrderNotes = findViewById(R.id.tvOrderNotes);
        lytWallet.setVisibility(View.GONE);


        rToday = findViewById(R.id.rToday);
        rTomorrow = findViewById(R.id.rTomorrow);
        tvWltBalance = findViewById(R.id.tvWltBalance);
        tvPreTotal = findViewById(R.id.tvPreTotal);
        btnApply = findViewById(R.id.btnApply);
        tvCurrent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_address, 0, 0, 0);
        tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
        tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process_gray, 0, 0, 0);
        tvConfirmOrder.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_confirm, 0);
        tvPlaceOrder.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_process, 0);
        tvPreTotal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info, 0, 0, 0);
        ApiConfig.getWalletBalance(CheckoutActivity.this, session);
        GetTimeSlots();
        try {
            qtyList = new JSONArray(session.getData(Session.KEY_Orderqty));
            variantIdList = new JSONArray(session.getData(Session.KEY_Ordervid));
            nameList = new JSONArray(session.getData(Session.KEY_Ordername));

            for (int i = 0; i < nameList.length(); i++) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setWeightSum(4f);
                String[] name = nameList.getString(i).split("==");
                TextView tv1 = new TextView(this);
                tv1.setText(name[1] + " (" + CartActivity.cartNames.get("" + i) + ")");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1.5f;
                tv1.setLayoutParams(lp);
                tv1.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                linearLayout.addView(tv1);

                TextView tv2 = new TextView(this);
                tv2.setText(qtyList.getString(i));
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.weight = 0.7f;
                tv2.setLayoutParams(lp1);
                tv2.setGravity(Gravity.CENTER);
                linearLayout.addView(tv2);

                TextView tv3 = new TextView(this);
                tv3.setText(Constant.SETTING_CURRENCY_SYMBOL + name[2]);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2.weight = 0.8f;
                tv3.setLayoutParams(lp2);
                tv3.setGravity(Gravity.CENTER);
                linearLayout.addView(tv3);

                TextView tv4 = new TextView(this);
                tv4.setText(Constant.SETTING_CURRENCY_SYMBOL + name[3]);
                LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp3.weight = 1f;
                tv4.setLayoutParams(lp3);
                tv4.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                linearLayout.addView(tv4);
                lytOrderList.addView(linearLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SetDataTotal();
        chWallet.setTag("false");
        getWalletBalance();
        tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);

        if (Constant.WALLET_BALANCE == 0) {
            chWallet.setEnabled(false);
            walletLyt.setEnabled(false);
        }
        chWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chWallet.getTag().equals("false")) {
                    chWallet.setChecked(true);
                    lytWallet.setVisibility(View.VISIBLE);

                    if (Constant.WALLET_BALANCE >= subtotal) {
                        usedBalance = subtotal;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.SETTING_CURRENCY_SYMBOL + (Constant.WALLET_BALANCE - usedBalance));
                        paymentMethod = "wallet";
                        lytPayOption.setVisibility(View.GONE);
                    } else {
                        usedBalance = Constant.WALLET_BALANCE;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.SETTING_CURRENCY_SYMBOL + "0.0");
                        lytPayOption.setVisibility(View.VISIBLE);
                    }
                    subtotal = (subtotal - usedBalance);
                    tvWallet.setText("-" + Constant.SETTING_CURRENCY_SYMBOL + usedBalance);
                    tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
                    chWallet.setTag("true");

                } else {
                    walletUncheck();
                }

            }
        });
        PromoCodeCheck();
        setPaymentMethod();
    }

    public void walletUncheck() {
        lytPayOption.setVisibility(View.VISIBLE);
        tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
        lytWallet.setVisibility(View.GONE);
        chWallet.setChecked(false);
        chWallet.setTag("false");
        SetDataTotal();
    }

    public void setPaymentMethod() {
        rbCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rbCod.setChecked(true);
                rbBkashPay.setChecked(false);
                lytBkashPay.setVisibility(View.GONE);
                rbRocketPay.setChecked(false);
                lytRocketPay.setVisibility(View.GONE);
                rbNagadPay.setChecked(false);
                lytNagadPay.setVisibility(View.GONE);

                paymentMethod = rbCod.getTag().toString();

            }
        });
        final double bCharge = Math.ceil((subtotal * 1.85) / 100.0);
        final double rCharge = Math.ceil((subtotal * 1.80) / 100.0);
        final double nCharge = Math.ceil((subtotal * 1.45) / 100.0);

        rbBkashPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbBkashPay.setChecked(true);
                lytBkashPay.setVisibility(View.VISIBLE);
                rbCod.setChecked(false);
                rbRocketPay.setChecked(false);
                lytRocketPay.setVisibility(View.GONE);
                rbNagadPay.setChecked(false);
                lytNagadPay.setVisibility(View.GONE);

                totalPayInBkash.setText("Total: "+String.valueOf(subtotal) +" Taka");
                bkashCharge.setText("Bkash charge: "+String.valueOf(bCharge) +" Taka");
                totalPayInBkash.setTextColor(getResources().getColor(R.color.black));
                bkashCharge.setTextColor(getResources().getColor(R.color.black));

                paymentMethod = rbBkashPay.getTag().toString();

            }
        });
        rbRocketPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbRocketPay.setChecked(true);
                lytRocketPay.setVisibility(View.VISIBLE);
                rbCod.setChecked(false);
                rbBkashPay.setChecked(false);
                lytBkashPay.setVisibility(View.GONE);
                rbNagadPay.setChecked(false);
                lytNagadPay.setVisibility(View.GONE);

                totalPayInRocket.setText("Total: "+String.valueOf(subtotal) +" Taka");
                rocketCharge.setText("Rocket charge: "+String.valueOf(rCharge) +" Taka");
                totalPayInRocket.setTextColor(getResources().getColor(R.color.black));
                rocketCharge.setTextColor(getResources().getColor(R.color.black));

                paymentMethod = rbRocketPay.getTag().toString();
                Checkout.preload(getApplicationContext());
            }
        });

        rbNagadPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbNagadPay.setChecked(true);
                lytNagadPay.setVisibility(View.VISIBLE);
                rbCod.setChecked(false);
                rbBkashPay.setChecked(false);
                lytBkashPay.setVisibility(View.GONE);
                rbRocketPay.setChecked(false);
                lytRocketPay.setVisibility(View.GONE);

                totalPayInNagad.setText("Total: "+String.valueOf(subtotal) +" Taka");
                nagadCharge.setText("Nagad charge: "+String.valueOf(nCharge) +" Taka");
                totalPayInNagad.setTextColor(getResources().getColor(R.color.black));
                nagadCharge.setTextColor(getResources().getColor(R.color.black));

                paymentMethod = rbNagadPay.getTag().toString();
            }
        });

    }

    private String getTime() {
        String delegate = "HH:mm a";
        Date currentTime = Calendar.getInstance().getTime();
        String time = (String)DateFormat.format(delegate, currentTime);
        System.out.println("======>getTime: "+time);
        return time;
    }


    public void SetDataTotal() {
        dCharge = 0.0;
        if(session.isUserLoggedIn()) {
            dCharge = Double.parseDouble(session.getData(Session.KEY_AREA_DELIVERY_CHARGE));
        }
        total = databaseHelper.getTotalCartAmt(session);

        tvTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(total));
        subtotal = total;
        if (total < Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY) {
            tvDeliveryCharge.setText(Constant.SETTING_CURRENCY_SYMBOL + dCharge);
            subtotal = subtotal + dCharge;
        } else {
            tvDeliveryCharge.setText("Free");
        }
        taxAmt = ((Constant.SETTING_TAX * total) / 100);
        if (pCode.isEmpty()) {
            subtotal = (subtotal + taxAmt);
        } else
            subtotal = (subtotal + taxAmt - pCodeDiscount);
        tvTaxPercent.setText("Tax(" + Constant.SETTING_TAX + "%)");
        tvTaxAmt.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(taxAmt));
        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
    }

    public void OnBtnClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfirmOrder:
                tvPayment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_3));
                tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
                tvDelivery.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
                tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                tvConfirmOrder.setVisibility(View.GONE);
                tvPlaceOrder.setVisibility(View.VISIBLE);
                paymentLyt.setVisibility(View.VISIBLE);
                deliveryLyt.setVisibility(View.GONE);

                break;
            case R.id.tvPlaceOrder:
                PlaceOrderProcess();

                break;
            case R.id.imgedit:
                startActivity(new Intent(CheckoutActivity.this, ProfileActivity.class));
                break;
            case R.id.tvUpdate:
                if (ApiConfig.isGPSEnable(CheckoutActivity.this))
                    startActivity(new Intent(CheckoutActivity.this, MapActivity.class));
                else
                    ApiConfig.displayLocationSettingsRequest(CheckoutActivity.this);
                break;
            default:
                break;
        }
    }


    public void PlaceOrderProcess() {
        transectionId = "";
        transectionMobile = "";
        if (deliveryDay.length() == 0) {
            Toast.makeText(CheckoutActivity.this, getString(R.string.select_delivery_day), Toast.LENGTH_SHORT).show();
            return;
        } else if (deliveryTime.length() == 0) {
            Toast.makeText(CheckoutActivity.this, getString(R.string.select_delivery_time), Toast.LENGTH_SHORT).show();
            return;
        } else if (paymentMethod.isEmpty()) {
            Toast.makeText(CheckoutActivity.this, getString(R.string.select_payment_method), Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!paymentMethod.isEmpty() && paymentMethod.equals(getString(R.string.bkash_pay))) {
            if(!getPayMentInfo(edtBkashMob, edtBkashTxid, getString(R.string.bkash_pay))) return;
            transectionMobile = edtBkashMob.getText().toString();
            transectionId = edtBkashTxid.getText().toString();
        }
        else if(!paymentMethod.isEmpty() && paymentMethod.equals(getString(R.string.rocket_pay))) {
            if(!getPayMentInfo(edtRocketMob, edtRocketTxid, getString(R.string.rocket_pay))) return;
            transectionMobile = edtRocketMob.getText().toString();
            transectionId = edtRocketTxid.getText().toString();
        }
        else if(!paymentMethod.isEmpty() && paymentMethod.equals(getString(R.string.nagad_pay))) {
            if(!getPayMentInfo(edtNagadMob, edtNagadTxid, getString(R.string.nagad_pay))) return;
            transectionMobile = edtNagadMob.getText().toString();
            transectionId = edtNagadTxid.getText().toString();
        }

        dCharge = Double.parseDouble(session.getData(Session.KEY_AREA_DELIVERY_CHARGE));
        if(total >= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY) {
            dCharge = 0.0;
        }

        final Map<String, String> sendparams = new HashMap<String, String>();
        sendparams.put(Constant.PLACE_ORDER, Constant.GetVal);
        sendparams.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        sendparams.put(Constant.TAX_PERCENT, String.valueOf(Constant.SETTING_TAX));
        sendparams.put(Constant.TAX_AMOUNT, DatabaseHelper.decimalformatData.format(taxAmt));
        sendparams.put(Constant.TOTAL, DatabaseHelper.decimalformatData.format(total));
        sendparams.put(Constant.FINAL_TOTAL, DatabaseHelper.decimalformatData.format(subtotal));
        sendparams.put(Constant.PRODUCT_VARIANT_ID, String.valueOf(variantIdList));
        sendparams.put(Constant.QUANTITY, String.valueOf(qtyList));
        sendparams.put(Constant.MOBILE, session.getData(Session.KEY_MOBILE));
        sendparams.put(Constant.DELIVERY_CHARGE, String.valueOf(dCharge));
        sendparams.put(Constant.DELIVERY_TIME, (deliveryDay + " - " + deliveryTime));
        sendparams.put(Constant.KEY_WALLET_USED, chWallet.getTag().toString());
        sendparams.put(Constant.KEY_WALLET_BALANCE, String.valueOf(usedBalance));
        sendparams.put(Constant.PAYMENT_METHOD, paymentMethod);
        sendparams.put(Constant.ORDER_NOTES, tvOrderNotes.getText().toString());
        sendparams.put(Constant.PAYER_ACC_NO, transectionMobile);
        sendparams.put(Constant.PAYMENT_TXID, transectionId);

        final String address = session.getData(Session.KEY_ADDRESS) + ", " + session.getData(Session.KEY_SUB_AREA) + ", "
                + session.getData(Session.KEY_AREA) + ", " + session.getData(Session.KEY_CITY);
        if (!pCode.isEmpty()) {
            sendparams.put(Constant.PROMO_CODE, pCode);
            sendparams.put(Constant.PROMO_DISCOUNT, String.valueOf(pCodeDiscount));
        }
        sendparams.put(Constant.ADDRESS, address);
        sendparams.put(Constant.LONGITUDE, session.getCoordinates(Session.KEY_LONGITUDE));
        sendparams.put(Constant.LATITUDE, session.getCoordinates(Session.KEY_LATITUDE));
        sendparams.put(Constant.EMAIL, session.getData(Session.KEY_EMAIL));
        System.out.println("=====params " + sendparams.toString());


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_order_confirm, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final AlertDialog dialog = alertDialog.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvCancel, tvConfirm, tvItemTotal, tvTaxPercent1, tvTaxAmt1, tvDeliveryCharge1, tvTotal1, tvPromoCode1, tvPCAmount1, tvWallet1, tvFinalTotal1;
        LinearLayout lytPromo, lytWallet;

        lytPromo = dialogView.findViewById(R.id.lytPromo);
        lytWallet = dialogView.findViewById(R.id.lytWallet);
        tvItemTotal = dialogView.findViewById(R.id.tvItemTotal);
        tvTaxPercent1 = dialogView.findViewById(R.id.tvTaxPercent);
        tvTaxAmt1 = dialogView.findViewById(R.id.tvTaxAmt);
        tvDeliveryCharge1 = dialogView.findViewById(R.id.tvDeliveryCharge);
        tvTotal1 = dialogView.findViewById(R.id.tvTotal);
        tvPromoCode1 = dialogView.findViewById(R.id.tvPromoCode);
        tvPCAmount1 = dialogView.findViewById(R.id.tvPCAmount);
        tvWallet1 = dialogView.findViewById(R.id.tvWallet);
        tvFinalTotal1 = dialogView.findViewById(R.id.tvFinalTotal);
        tvCancel = dialogView.findViewById(R.id.tvCancel);
        tvConfirm = dialogView.findViewById(R.id.tvConfirm);
        String orderMessage = "";
        if (!pCode.isEmpty())
            lytPromo.setVisibility(View.VISIBLE);
        else
            lytPromo.setVisibility(View.GONE);

        if (chWallet.getTag().toString().equals("true"))
            lytWallet.setVisibility(View.VISIBLE);
        else
            lytWallet.setVisibility(View.GONE);

        if(tvDeliveryCharge.getText().toString().equalsIgnoreCase("free")){
            dCharge = 0.0;
        }

        double totalAfterTax = (total + dCharge + taxAmt);
        tvItemTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + total);
        tvDeliveryCharge1.setText(tvDeliveryCharge.getText().toString());
        tvTaxPercent1.setText(getString(R.string.tax) + "(" + Constant.SETTING_TAX + "%) :");
        tvTaxAmt1.setText(tvTaxAmt.getText().toString());
        tvTotal1.setText(Constant.SETTING_CURRENCY_SYMBOL + totalAfterTax);
        tvPCAmount1.setText(tvPCAmount.getText().toString());
        tvWallet1.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + usedBalance);
        tvFinalTotal1.setText(Constant.SETTING_CURRENCY_SYMBOL + subtotal);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!paymentMethod.isEmpty()) {
                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            if (result) {
                                try {
                                    String obj = response.toString();

                                    System.out.println("=======> Place order resObject: " + obj);

                                    int sIndex = obj.indexOf("{");
                                    int eIndex = obj.indexOf("}");
                                    String subString = obj.substring(sIndex,eIndex+1);

                                    JSONObject object = new JSONObject(subString);

                                    if (!object.getBoolean(Constant.ERROR)) {
//                                        AddTransaction(object.getString(Constant.ORDER_ID), paymentMethod, transectionId, "Success", getString(R.string.order_success), sendparams);
                                        startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class));
//                                        finish();
                                        if (chWallet.getTag().toString().equals("true")) {
                                            ApiConfig.getWalletBalance(CheckoutActivity.this, session);
                                        }
                                        dialog.dismiss();

                                    } else {
                                        Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //  System.out.println("========order=======" + response);
                        }
                    }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, sendparams, true);
                    dialog.dismiss();
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean getPayMentInfo(EditText editMob, EditText editTxid, String paymentType) {
        if(editMob.getText().toString().equals("")) {
            Toast.makeText(CheckoutActivity.this, "Empty " + paymentType + " Mobile No.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(editTxid.getText().toString().equals("")) {
            Toast.makeText(CheckoutActivity.this, "Empty " + paymentType + " TxID", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void CreateOrderId() {

        showProgressDialog(getString(R.string.loading));
        Map<String, String> params = new HashMap<>();
        String[] amount = String.valueOf(subtotal * 100).split("\\.");
        params.put("amount", "" + amount[0]);
        System.out.println("====params " + params.toString());
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {

                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            startPayment(object.getString("id"), object.getString("amount"));
                            hideProgressDialog();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.Get_RazorPay_OrderId, params, false);

    }

    public void startPayment(String orderId, String payAmount) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
        checkout.setImage(R.mipmap.ic_launcher_new);

        try {
            JSONObject options = new JSONObject();
            options.put(Constant.NAME, session.getData(Session.KEY_NAME));
            options.put(Constant.ORDER_ID, orderId);
            options.put(Constant.CURRENCY, "INR");
            options.put(Constant.AMOUNT, payAmount);

            JSONObject preFill = new JSONObject();
            preFill.put(Constant.EMAIL, session.getData(Session.KEY_EMAIL));
            preFill.put(Constant.CONTACT, session.getData(Session.KEY_MOBILE));
            options.put("prefill", preFill);
            checkout.open(CheckoutActivity.this, options);
        } catch (Exception e) {
            Log.d(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            razorPayId = razorpayPaymentID;
            PlaceOrder(paymentMethod, razorPayId, true, razorParams, "Success");


        } catch (Exception e) {
            Log.d(TAG, "onPaymentSuccess  ", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "onPaymentError  ", e);
        }
    }

    public void PlaceOrder(final String paymentType, final String txnid, boolean issuccess, final Map<String, String> sendparams, final String status) {
        showProgressDialog(getString(R.string.processing));
        if (issuccess) {
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {

                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean(Constant.ERROR)) {
//                                AddTransaction("", paymentType, txnid, status, getString(R.string.order_success), sendparams);
                                startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();

                            }
                            hideProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, sendparams, false);
        } else {

//            AddTransaction("", getString(R.string.rocket_pay), txnid, status, getString(R.string.order_failed), sendparams);
        }
    }

    public void AddTransaction(String orderId, String paymentType, String txnid, String status, String message, Map<String, String> sendparams) {
        Map<String, String> transparams = new HashMap<>();
        transparams.put(Constant.Add_TRANSACTION, Constant.GetVal);
        transparams.put(Constant.NAME, session.getData(Session.KEY_NAME));
        transparams.put(Constant.ORDER_ID, orderId);
        transparams.put(Constant.TYPE, paymentType);
        transparams.put(Constant.MOBILE, transectionMobile);
        transparams.put(Constant.TRANS_ID, txnid);
        transparams.put(Constant.AMOUNT, sendparams.get(Constant.FINAL_TOTAL));
        transparams.put(Constant.STATUS, status);
        transparams.put(Constant.MESSAGE, message);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        transparams.put(Constant.TXTN_DATE, df.format(c));

        System.out.println("========> TransParam: "+transparams);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        System.out.println("========> AddTransaction-response: "+response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
//                            startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class));
//                            finish();

//                            if (status.equals("Failed"))
//                                finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, transparams, false);
    }

    public void RefreshPromoCode(View view) {
        if (isApplied) {

            btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_4));
            btnApply.setText(getString(R.string.apply));
            edtPromoCode.setText("");
            tvPromoCode.setVisibility(View.GONE);
            tvPCAmount.setVisibility(View.GONE);
            isApplied = false;
            appliedCode = "";
            pCode = "";
            SetDataTotal();

        }
    }

    public void PromoCodeCheck() {
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String promoCode = edtPromoCode.getText().toString().trim();
                if (promoCode.isEmpty()) {
                    tvAlert.setVisibility(View.VISIBLE);
                    tvAlert.setText(getString(R.string.enter_promo_code));
                } else if (isApplied && promoCode.equals(appliedCode)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.promo_code_already_applied), Toast.LENGTH_SHORT).show();
                } else {
                    if (isApplied && !promoCode.equals(appliedCode)) {
                        SetDataTotal();
                    }
                    tvAlert.setVisibility(View.GONE);
                    btnApply.setVisibility(View.INVISIBLE);
                    pBar.setVisibility(View.VISIBLE);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Constant.VALIDATE_PROMO_CODE, Constant.GetVal);
                    params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
                    params.put(Constant.PROMO_CODE, promoCode);
                    params.put(Constant.TOTAL, String.valueOf(total));

                    deliveryCharge = session.getData(Session.KEY_AREA_DELIVERY_CHARGE);

                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            if (result) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    //   System.out.println("===res " + response);
                                    if (!object.getBoolean(Constant.ERROR)) {
                                        pCode = object.getString(Constant.PROMO_CODE);
                                        tvPromoCode.setText(getString(R.string.promo_code) + "(" + pCode + ")");
                                        btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
                                        btnApply.setText(getString(R.string.applied));
                                        isApplied = true;
                                        appliedCode = edtPromoCode.getText().toString();
                                        tvPCAmount.setVisibility(View.VISIBLE);
                                        tvPromoCode.setVisibility(View.VISIBLE);
                                        dCharge = tvDeliveryCharge.getText().toString().equalsIgnoreCase("free") ? 0.0 : Double.parseDouble(deliveryCharge);
                                        subtotal = (Double.parseDouble(object.getString(Constant.DISCOUNTED_AMOUNT)) + taxAmt + dCharge);
                                        pCodeDiscount = (Double.parseDouble(object.getString(Constant.DISCOUNT)));
                                        tvPCAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + pCodeDiscount);
                                        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + subtotal);
                                    } else {
                                        btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_4));
                                        btnApply.setText(getString(R.string.apply));
                                        tvAlert.setVisibility(View.VISIBLE);
                                        tvAlert.setText(object.getString("message"));
                                    }
                                    pBar.setVisibility(View.GONE);
                                    btnApply.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, CheckoutActivity.this, Constant.PROMO_CODE_CHECK_URL, params, false);

                }
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap mMap = googleMap;
        mMap.clear();
        LatLng latLng = new LatLng(Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE)), Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE)));
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.current_location)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
        tvName.setText(session.getData(Session.KEY_NAME));
        tvCurrent.setText(getString(R.string.current_location) + " : " + ApiConfig.getAddress(Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE)), Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE)), CheckoutActivity.this));
        String address = session.getData(Session.KEY_ADDRESS) + ",<br>"
                + session.getData(Session.KEY_SUB_AREA) + ", " + session.getData(Session.KEY_AREA)
                + ", " + session.getData(Session.KEY_CITY)
                + "<br><b>" + getString(R.string.mobile_) + session.getData(Session.KEY_MOBILE);
        tvCity.setText(Html.fromHtml(address));
    }

    @Override
    public void onBackPressed() {

        if (paymentLyt.getVisibility() == View.VISIBLE) {
            walletUncheck();
            tvPayment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process_gray, 0, 0, 0);
            tvDelivery.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_3));
            tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
            tvConfirmOrder.setVisibility(View.VISIBLE);
            tvPlaceOrder.setVisibility(View.GONE);
            paymentLyt.setVisibility(View.GONE);
            deliveryLyt.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();
    }

    public void getWalletBalance() {

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_USER_DATA, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("=================*setting " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            Constant.WALLET_BALANCE = Double.parseDouble(object.getString(Constant.KEY_BALANCE));
                            if (DrawerActivity.tvWallet != null) {
                                DrawerActivity.tvWallet.setText(getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                            }
                            tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.USER_DATA_URL, params, false);

    }

    public void GetTimeSlots() {
        slotList = new ArrayList<>();
        slotList.add(new Slot("27","যে কোন সময়","00:00:12"));
        slotList.add(new Slot("26","সকাল ৬ টা থেকে ৯ টা","08:30:00"));
        slotList.add(new Slot("22","সকাল ৯ টা থেকে ১২ টা","11:30:00"));
        slotList.add(new Slot("21","দুপুর ২ টা থেকে সন্ধ্যা ৬  টা","17:30:00"));
        slotList.add(new Slot("20","সন্ধ্যা ৬ টা থেকে রাত ৯ টা","20:30:00"));
        dayLyt.setVisibility(View.VISIBLE);

        adapter = new SlotAdapter(slotList);
        recyclerView.setAdapter(adapter);

//        Map<String, String> params = new HashMap<String, String>();
//        params.put(Constant.GET_TIME_SLOT, Constant.GetVal);
//
//        ApiConfig.RequestToVolley(new VolleyCallback() {
//            @Override
//            public void onSuccess(boolean result, String response) {
//                if (result) {
//                    try {
//                        JSONObject object = new JSONObject(response);
//                        slotList = new ArrayList<>();
//                        if (!object.getBoolean(Constant.ERROR)) {
//                            dayLyt.setVisibility(View.VISIBLE);
//                            JSONArray jsonArray = object.getJSONArray(Constant.TIME_SLOTS);
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject object1 = jsonArray.getJSONObject(i);
//                                slotList.add(new Slot(object1.getString(Constant.ID), object1.getString(Constant.TITLE), object1.getString(Constant.LAST_ORDER_TIME)));
//
//                            }
//                            adapter = new SlotAdapter(slotList);
//                            recyclerView.setAdapter(adapter);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, CheckoutActivity.this, Constant.SETTING_URL, params, false);

    }


    public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.ViewHolder> {
        public ArrayList<Slot> categorylist;
        int selectedPosition = 0;

        public SlotAdapter(ArrayList<Slot> categorylist) {
            this.categorylist = categorylist;

        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_time_slot, parent, false);
            return new ViewHolder(view);
        }

        @NonNull
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            //holder.setIsRecyclable(false);
            final Slot model = categorylist.get(position);
            holder.rdBtn.setText(model.getTitle());
            holder.rdBtn.setTag(position);
            holder.rdBtn.setChecked(position == selectedPosition);
            if (deliveryDay.equals(getString(R.string.other_day))) {
                model.setSlotAvailable(true);
                // deliveryTime = model.getTitle();
            }
            if (model.isSlotAvailable()) {
                holder.rdBtn.setClickable(true);
                holder.rdBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            } else {
                holder.rdBtn.setChecked(false);
                holder.rdBtn.setClickable(false);
                holder.rdBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            }
            if (getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()) > 0) {
                rToday.setClickable(false);
                rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            } else {
                rToday.setClickable(true);
                rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            }
            System.out.println("======time slote valdation " + getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()));
            rToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()) > 0) {
                        rToday.setClickable(false);
                        rToday.setChecked(false);
                        rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));

                    } else {
                        rToday.setChecked(true);
                        rTomorrow.setChecked(false);
                        deliveryDay = getString(R.string.today);
                        for (Slot s : slotList) {
                            if (getTime().compareTo(s.getLastOrderTime()) > 0) {
                                s.setSlotAvailable(false);
                            } else
                                s.setSlotAvailable(true);
                        }
                        notifyDataSetChanged();
                    }
                }
            });

            rTomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deliveryDay = getString(R.string.other_day);
                    rToday.setChecked(false);
                    rTomorrow.setChecked(true);
                    notifyDataSetChanged();
                }

            });
            holder.rdBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryTime = model.getTitle();
                    selectedPosition = (Integer) v.getTag();
                    notifyDataSetChanged();
                }
            });

            if (holder.rdBtn.isChecked()) {
                deliveryTime = model.getTitle();
            }
        }

        @Override
        public int getItemCount() {
            return categorylist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RadioButton rdBtn;

            public ViewHolder(View itemView) {
                super(itemView);
                rdBtn = itemView.findViewById(R.id.rdBtn);

            }

        }
    }


}
