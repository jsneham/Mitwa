package com.matriapp.mobile.fragments.ProfileVerification;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.matriapp.mobile.R;
import com.matriapp.mobile.fragments.OTPRequestDialogWithFirebaseFragment;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class MobileVerificationFragment extends Fragment {

    private Common common;

    private SessionManager session;
    private Context context;
    private RelativeLayout loader,llView;
    private ImageView ivIconPrimary, ivIconSecondary, ivIconWhatsApp;
    private TextView tvDetailsPrimary, tvDetailsSecondary, tvDetailsWhatsApp,tvDescription,tvWhatsappDesc;
    private String mobile_verify_status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mobile_verification, container, false);

        context = getContext();
        session = new SessionManager(context);
        common = new Common(getActivity());

        llView = view.findViewById(R.id.llView);
        loader = view.findViewById(R.id.loader);
        ivIconWhatsApp = view.findViewById(R.id.ivIconWhatsApp);
        ivIconSecondary = view.findViewById(R.id.ivIconSecondary);
        ivIconPrimary = view.findViewById(R.id.ivIconPrimary);
        tvDetailsWhatsApp = view.findViewById(R.id.tvDetailsWhatsApp);
        tvDetailsSecondary = view.findViewById(R.id.tvDetailsSecondary);
        tvDetailsPrimary = view.findViewById(R.id.tvDetailsPrimary);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvWhatsappDesc = view.findViewById(R.id.tvWhatsappDesc);


        tvDetailsPrimary.setOnClickListener(v -> {
            if (mobile_verify_status.equalsIgnoreCase("No")) {
                OTPRequestDialogWithFirebaseFragment dialogFragment = OTPRequestDialogWithFirebaseFragment.newInstance();
                dialogFragment.show(getParentFragmentManager(), "OTP Dialog");
            }
        });

        tvDetailsSecondary.setOnClickListener(v -> {
            common.showToast(getString(R.string.verification_underProcess),llView);
        });

        tvDetailsWhatsApp.setOnClickListener(v -> {
            common.callWhatsApp(context);
        });
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyProfile();
    }

    private void getMyProfile() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");

                    mobile_verify_status = data.getString("mobile_verify_status");

                    setPrimaryContactNumber();
                    Log.d("TAG", "getMyProfile: " + data);

                }

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);
    }

    private void setPrimaryContactNumber() {
        if (mobile_verify_status.equalsIgnoreCase("Yes")) {
            tvDetailsPrimary.setText("Verified Successfully");
            tvDescription.setVisibility(View.GONE);
            tvDetailsPrimary.setTextColor(context.getColor(R.color.positive_green));
        } else {
            ivIconPrimary.setImageResource(R.drawable.profileverification_primaryphone);
            tvDetailsPrimary.setText("Verify Now");
        }
    }
}