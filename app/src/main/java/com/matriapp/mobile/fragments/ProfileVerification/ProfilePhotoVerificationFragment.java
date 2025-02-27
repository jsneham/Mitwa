package com.matriapp.mobile.fragments.ProfileVerification;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class ProfilePhotoVerificationFragment extends Fragment {

    private Common common;
    private ProgressDialog pd;

    private SessionManager session;
    private Context context;
    private RelativeLayout loader,llView;
    private TextView btnNeedHelp, tvDescription;
    private String id_proof_approve, id_proof;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_photo_verification, container, false);

        context = getContext();
        session = new SessionManager(context);
        common = new Common(getActivity());

        llView = view.findViewById(R.id.llView);
        btnNeedHelp = view.findViewById(R.id.btnNeedHelp);
        tvDescription = view.findViewById(R.id.tvDescription);

        btnNeedHelp.setOnClickListener(v -> {
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

                    id_proof_approve = data.getString("id_proof_approve");
                    id_proof = data.getString("id_proof");

                    setIdProofPhoto();
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

    private void setIdProofPhoto() {
        if (id_proof_approve.equalsIgnoreCase("UNAPPROVED") && isValidImage(id_proof)) {
            tvDescription.setText("Will be verified with Govt. ID");
            tvDescription.setTextColor(context.getColor(R.color.negative_red));
        }
        else if (id_proof_approve.equalsIgnoreCase("APPROVED") && isValidImage(id_proof)) {
            tvDescription.setText("Verified Successfully with Govt. ID");
            tvDescription.setTextColor(context.getColor(R.color.positive_green));
        }

    }

    private boolean isValidImage(String url) {
        return !url.equals("") && !url.equals("null");
    }

}