package com.matriapp.mobile.activities;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Response;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class PaymentWebView extends AppCompatActivity {

    String Total_amount, Method, Plan_id;
    private WebView web_payment;
    SessionManager session;
    Common common;
    ProgressDialog pd;
    private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

        session = new SessionManager(this);
        common = new Common(this);

        web_payment = (WebView) findViewById(R.id.web_payment);
        web_payment.setWebViewClient(new MyBrowser());

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("Total_amount")) {
                Total_amount = b.getString("Total_amount");
            }
            if (b.containsKey("Method")) {
                Method = b.getString("Method");
            }
            if (b.containsKey("Plan_id")) {
                Plan_id = b.getString("Plan_id");
            }
        }


        String url = AppConstants.payment_url + session.getLoginData(SessionManager.KEY_USER_ID) + "/" + Method + "/" + Plan_id + "/" + Total_amount;

        Log.d("weburl", url);
        web_payment.getSettings().setLoadsImagesAutomatically(true);
        web_payment.getSettings().setJavaScriptEnabled(true);
        web_payment.getSettings().setSupportMultipleWindows(true);
        web_payment.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        web_payment.getSettings().setUseWideViewPort(true);
        web_payment.getSettings().setUserAgentString(DESKTOP_USER_AGENT);
        web_payment.getSettings().setLoadWithOverviewMode(true);

        web_payment.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        web_payment.loadUrl(url);

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals(AppConstants.payment_fail)) {
                common.showToast("Payment Failed",web_payment);
                getCurrentPlan();
                finish();
            } else if (url.equals(AppConstants.payment_success)) {
                common.showToast("Payment Success. Thank you",web_payment);
                getCurrentPlan();

            }
            view.loadUrl(url);
            return true;
        }

    }

    private void getCurrentPlan() {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        common.makePostRequest(AppConstants.check_plan, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
//                    MyApplication.setPlan(object.getBoolean("is_show"));
                    if (object.has("plan_expired") && !object.isNull("plan_expired")) {
                        MyApplication.setPlan(common.isPlanExpired(object.getString("plan_expired")));
                    }
                    JSONObject obj = object.getJSONObject("data");
                    session.setUserData(SessionManager.KEY_PLAN_STATUS, checkWhetherIsExpired(obj.getString("plan_expired")));
                    Intent i = new Intent(PaymentWebView.this, CurrentPlanActivity.class);
                    i.putExtra("isFromSuccessPayment", true);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later),web_payment);
                }
                //pd.dismiss();
            }
        }, error -> {
            if (pd != null)
                pd.dismiss();
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),web_payment);
            }
        },web_payment);
    }

    private String checkWhetherIsExpired(String plan_expired) {
        String status= "";
        try {

            LocalDate expirationDate = LocalDate.parse(plan_expired, DateTimeFormatter.ISO_LOCAL_DATE);

            LocalDate currentDate = LocalDate.now();

            if (expirationDate.isBefore(currentDate)) {
                status="Expired";
    //            System.out.println("The plan has expired.");
            } else if (expirationDate.isEqual(currentDate)) {
                status="Expired";

    //            System.out.println("The plan expires today.");
            } else {
                status="Active";
                System.out.println("The plan is still valid.");
            }

            return status;
        } catch (Exception e) {
            e.printStackTrace();
            return status;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            showAlert();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        showAlert();
    }

    private void showAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to leave without making payment?");
        alert.setPositiveButton("Yes", (dialogInterface, i) -> finish());
        alert.setNegativeButton("No", null);
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentPlan();
    }

    private void getCurrentPlans() {

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        common.makePostRequest(AppConstants.check_plan, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    MyApplication.setPlan(object.getBoolean("is_show"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later),web_payment);
                }
                //pd.dismiss();
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),web_payment);
            }
        },web_payment);
    }
}