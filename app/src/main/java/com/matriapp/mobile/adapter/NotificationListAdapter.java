package com.matriapp.mobile.adapter;

import static com.matriapp.mobile.utility.Common.convertDpToPixels;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.CurrentPlanActivity;
import com.matriapp.mobile.activities.ExpressInterestActivity;
import com.matriapp.mobile.activities.LikeProfileActivity;
import com.matriapp.mobile.activities.ManagePhotosActivity;
import com.matriapp.mobile.activities.OtherUserProfileActivity;
import com.matriapp.mobile.activities.PhotoPasswordActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.activities.QuickMessageActivity;
import com.matriapp.mobile.activities.ShortlistedProfileActivity;
import com.matriapp.mobile.activities.UploadIdAndHoroscopeActivity;
import com.matriapp.mobile.activities.UploadVideoActivity;
import com.matriapp.mobile.activities.ViewMyProfileActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.fragments.InterestsFragment;
import com.matriapp.mobile.fragments.PhotoRequestFragment;
import com.matriapp.mobile.model.NotificationBean;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;
    private ArrayList<NotificationBean> arrayList = new ArrayList<>();
    private ItemListener myListener;
    private SessionManager session;

    public final int TYPE_DATA = 0;
    public final int TYPE_LOAD = 1;

    private OnLoadMoreListener loadMoreListener;
    private boolean isLoading = false, isMoreDataAvailable = true;

    public NotificationListAdapter(Context mContext, ArrayList<NotificationBean> arrayList) {
        if (mContext == null) return;
        this.mContext = mContext;
        this.arrayList = arrayList;
        session = new SessionManager(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        if (!arrayList.get(position).getId().equals(mContext.getString(R.string.str_loading))) {
            return TYPE_DATA;
        } else {
            return TYPE_LOAD;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_DATA) {
            return new ViewHolder(inflater.inflate(R.layout.cell_notification_list, viewGroup, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.cell_load, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == TYPE_DATA) {
            NotificationBean notificationBean = arrayList.get(position);

            ((ViewHolder) holder).lblTitle.setText(notificationBean.getTitle());
            ((ViewHolder) holder).lblDesc.setText(notificationBean.getMessage());
            ((ViewHolder) holder).lblTime.setText(Common.getDateStringFromDate(AppConstants.displayDateFormat, Common.getDateFromDateString(AppConstants.DateTimeFormat, notificationBean.getCreatedOn())));

            int placeHolder = 0;
            if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                placeHolder = R.drawable.female;
            } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                placeHolder = R.drawable.male;
            }

//            if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
//                placeHolder = R.drawable.male;
//            } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
//                placeHolder = R.drawable.female;
//            }
            Picasso.get().load(notificationBean.getImage())
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .resize(60, convertDpToPixels(60, MyApplication.getContext()))
                    .centerInside()
                    .into(((ViewHolder) holder).imgProfile);

            AppDebugLog.print("noti type : "+notificationBean.getNotificationType());
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView lblTitle,lblTime,lblDesc,btnViewProfile;
        private CircleImageView imgProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            lblTitle = itemView.findViewById(R.id.lblTitle);
            lblTime = itemView.findViewById(R.id.lblTime);
            lblDesc = itemView.findViewById(R.id.lblDesc);
            btnViewProfile = itemView.findViewById(R.id.btnViewProfile);

            itemView.setOnClickListener(this);
        }

        @Override public void onClick(View view) {
            if(arrayList.get(getAbsoluteAdapterPosition()) !=null) {
                NotificationBean notificationBean = arrayList.get(getAbsoluteAdapterPosition());
               // myListener.itemClicked(arrayList.get(getAbsoluteAdapterPosition()));
                Intent intent = null;
                AppDebugLog.print("image : "+notificationBean.getImage());
                AppDebugLog.print("noti type : "+notificationBean.getNotificationType());
                switch (Objects.requireNonNull(notificationBean.getNotificationType())) {
                    case "payment_received":
                        intent = new Intent(mContext, CurrentPlanActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case "plan_expired":
                        intent = new Intent(mContext, PlanListActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case "featured_profile":
                        intent = new Intent(mContext, ViewMyProfileActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case "profile_photo_approval":
                        intent = new Intent(mContext, ManagePhotosActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case "id_proof_photo_approval":
                        intent = new Intent(mContext, UploadIdAndHoroscopeActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case "video_approval":
                        intent = new Intent(mContext, UploadVideoActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case "viewed_contact_details":
                    case "viewed_profile":
                        intent = new Intent(mContext, OtherUserProfileActivity.class);
                        intent.putExtra("other_id",notificationBean.getSenderId());
                        mContext.startActivity(intent);
                        break;
                    case "add_shortlist":
                        intent = new Intent(mContext, ShortlistedProfileActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case "like_member":
                        intent = new Intent(mContext, LikeProfileActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case "interest_receive":
                        intent = new Intent(mContext, ExpressInterestActivity.class);
                        intent.putExtra("interest_tag", "receive");
                        mContext.startActivity(intent);
                        break;
                    case "reminder_interest_receive":
                    case "accepted_interest_receive":
                    case "rejected_interest_receive":
                        intent = new Intent(mContext, ExpressInterestActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case "message":
                        intent = new Intent(mContext, QuickMessageActivity.class);
                        mContext.startActivity(intent);
                        break;
                    case "photo_request":
                        intent = new Intent(mContext, PhotoPasswordActivity.class);
                        mContext.startActivity(intent);
                        break;
                    default:
                        AppDebugLog.print("Nothing");
                        break;
                }
            }
        }
    }

    public void setListener(ItemListener listener) {
        myListener = listener;
    }

    public interface ItemListener {
        void itemClicked(NotificationBean notificationBean);
    }

    static class LoadHolder extends RecyclerView.ViewHolder {
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
}
