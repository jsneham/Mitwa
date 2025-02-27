package com.matriapp.mobile.dynamicprofile;

import android.content.Context;
import android.content.res.Resources;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.ApplicationData;
import com.matriapp.mobile.utility.Common;

import java.util.ArrayList;

/**
 * Created by lenovo on 2/23/2016.
 */
public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder> {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;
    private Common common;

    private Resources res;

    //use for in my profile display edit button & other user profile hide edit button
    private boolean isEditEnabled;

    //listeners
    private final ItemClickListener mItemClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.layout_section;
    //    private static final int VIEW_TYPE_ITEM = R.layout.layout_item; //TODO : change this
    private static final int VIEW_TYPE_ITEM = R.layout.layout_item_viewmyprofile; //TODO : change this

    public SectionedExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                          final LinearLayoutManager gridLayoutManager, ItemClickListener itemClickListener,
                                          SectionStateChangeListener sectionStateChangeListener, boolean isEditEnabled) {
        mContext = context;
        common = new Common(context);
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;
        res = context.getResources();

        this.isEditEnabled = isEditEnabled;


    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof ViewProfileSectionBean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false), viewType);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_ITEM:
                final ViewProfileFieldsBean item = (ViewProfileFieldsBean) mDataArrayList.get(position);

                holder.lblName.setText(item.getTitle());
//                holder.ivIcon.setImageResource(item.getImage());
                AppDebugLog.print("resp : " +  item.getTitle());


                switch (item.getTitle()) {
                    case "Professional Additional Info":
                        holder.lblName.setText("Professional More Info");
                    case "Address":
                        holder.lblName.setText("Ancestral Origin");
                        break;
                    case "Phone":
                        holder.lblName.setText("Alternate Phone");
                        break;
                    case "Time To Call":
                        holder.lblName.setText("Matchmaker's Phone");
//                        holder.lblName.setVisibility(View.GONE);
                        break;
                    case "Residence":
                        holder.lblName.setText("Residence Type");
                        break;
                    case "Employee in":
                        holder.lblName.setText("Work Sector");
                        break;
                    case "Looking For":
                        holder.lblName.setText("Marital Status");
                        break;
                    case "Expectations":
                        holder.lblName.setText("Looking For (Desired Qualities)");
                        break;
                    case "Languages Known":
                        holder.lblName.setText("Spoken Language(s)");
                        break;
                    case "Status Children":
                        holder.lblName.setText("Children Living With");
                        break;
                    case "Hobby":
                        holder.lblName.setText("Hobbies & Interest(s)");
                        break;
                    case "Created By":
                        holder.lblName.setText("Profile Created By");
                        break;
                    case "Referenced By":
                        holder.lblName.setText("Matchmaker's Name");
                        break;
                    case "No Of Brothers":
                        holder.lblName.setText("No. of Brother(s)");
                        break;
                    case "No Of Married Brother":
                        holder.lblName.setText("Married Brother(s)");
                        break;
                    case "No Of Sisters":
                        holder.lblName.setText("No. of Sister(s)");
                        break;
                    case "No Of Married Sister":
                        holder.lblName.setText("Married Sister(s)");
                        break;
                    case "Employee In":
                        holder.lblName.setText("Work Sector");
                        break;
                    case "Father Name":
                        holder.lblName.setText("Father's Name");
                        break;

                    case "Mother Name":
                        holder.lblName.setText("Mother's Name");
                        break;

                    case "Father Occupation":
                        holder.lblName.setText("Father's Occupation");
                        break;

                    case "Mother Occupation":
                        holder.lblName.setText("Mother's Occupation");
                        break;


                    case "Designation":
                        holder.lblName.setVisibility(View.GONE);
//                        holder.lblName.setText("Work Designation");
                        break;

                    case "Gothra":
                        holder.lblName.setText("Gothra(m)");
                        break;

                    case "Birthdate":
                        holder.lblName.setText("Date of Birth");
                        break;

                    case "Horoscope":
                        holder.lblName.setText("Horoscope Belief");
                        break;
                    case "Challenged/Health Information":
                        holder.lblName.setText("Health/Challenged");
                        break;
                    case "Subcaste":
                        holder.lblName.setText("Sub-Caste");
                        break;
                    default:
                        holder.lblName.setText(item.getTitle());
                        break;
                }


                if (isEditEnabled && item.getTitle().equalsIgnoreCase("Weight")) {
                    String weight;
                    if (checkField(item.getValue()).equals("N/A")) {
                        weight = "N/A";
                    } else {
                        weight = item.getValue().replace("Kg", "Kgs");
                    }
                    holder.lblValue.setText(weight);
                }
                else if (isEditEnabled && item.getTitle().equalsIgnoreCase("Height")) {
                    if (!checkField(item.getValue()).equals("N/A")) {
                        holder.lblValue.setText(item.getValue());
                    } else
                        holder.lblValue.setText("N/A");
                }
                else if (item.getTitle().equalsIgnoreCase("Height Preference")) {
                    if (!checkField(item.getValue()).equals("N/A")) {
                        if (item.getValue().split("to").length == 2) {
                            String from = item.getValue().split("to")[0].trim();
                            String to = item.getValue().split("to")[1].trim();
                            String fHeight = "", tHeight = "";
                            if (!checkField(from).equals("N/A")) {
//                                fHeight = common.calculateHeight(from);
                                fHeight = from;
                            }
                            if (!checkField(to).equals("N/A")) {
//                                tHeight = common.calculateHeight(to);
                                tHeight = to;
                            }
                            holder.lblValue.setText(fHeight + " to " + tHeight);
                        } else {
                            holder.lblValue.setText("N/A");
                        }
                    } else {
                        holder.lblValue.setText("N/A");
                    }
                }
                else if (isEditEnabled && item.getTitle().equalsIgnoreCase("Star") && item.getId().equalsIgnoreCase("star")) {
                    if (!checkField(item.getValue()).equals("N/A")) {
                        holder.lblValue.setText(ApplicationData.myProfileStarStr);
                    } else {
                        holder.lblValue.setText("N/A");
                    }
                }
                else if (isEditEnabled && item.getTitle().equalsIgnoreCase("Moonsign")) {
                    if (!checkField(item.getValue()).equals("N/A")) {
                        holder.lblValue.setText(ApplicationData.myProfileMoonSignStr);
                    } else {
                        holder.lblValue.setText("N/A");
                    }
                }
                else if (isEditEnabled && item.getId().equals("no_of_brothers")) {
                    if (item.getValue().equals("")|| item.getValue().equals("0")) {
                        holder.lblValue.setText("0");

                    }
                    else {
                        holder.lblValue.setText(item.getValue());
                    }
                }

                else if (isEditEnabled && item.getId().equals("no_of_sisters")) {
                    if (item.getValue().equals("")|| item.getValue().equals("0")) {
                        holder.lblValue.setText("0");
                    }
                    else{
                        holder.lblValue.setText(item.getValue());
                    }
                }
                else {

                    try {
                        // add space after comma (a, b, c)
                        if (item.getId() != null) {
                            if (!(item.getId().equals("part_income") || item.getId().equals("income"))) {
                                setspaceaftercomma(item, holder);
                            } else {
                                holder.lblValue.setText(checkField(item.getValue()));
                            }
                        } else setspaceaftercomma(item, holder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



//                    if (item.getId().equals("time_to_call")) {
//                        holder.lblValue.setVisibility(View.GONE);
//
//                    }
//                    if (item.getTitle().equals("Designation") || item.getTitle().equals("Partner Complexion") || item.getTitle().equals("Residence") || item.getTitle().equals("Star") || item.getId().equals("part_bodytype")) {
//                        holder.lblValue.setVisibility(View.GONE);
//                        holder.lblName.setVisibility(View.GONE);
//
//                    }

                    // holder.lblValue.setText(checkField(item.getValue()));
                }

                holder.view.setOnClickListener(v -> mItemClickListener.itemClicked(item));
                break;

            case VIEW_TYPE_SECTION:
                final ViewProfileSectionBean viewProfileSectionBean = (ViewProfileSectionBean) mDataArrayList.get(position);
                holder.lblSection.setText(viewProfileSectionBean.getName().replace("Education & Occupation Information", "Education & Occupation"));
                switch (viewProfileSectionBean.id) {
//                    case "basic_info":
//                        addBasicInfoImage(viewProfileSectionBean.getViewProfileFieldList());
//                        break;
//                    case "religion_info":
//                        addReligionInfoImage(viewProfileSectionBean.getViewProfileFieldList());
//                        break;
                    case "about_me_and_hobby":
                        holder.lblSection.setText("About & More");
//                        addAboutInfoImage(viewProfileSectionBean.getViewProfileFieldList());
                        break;
//                    case "edu_Occup":
//                        addEducationInfoImage(viewProfileSectionBean.getViewProfileFieldList());
//                        break;
//                    case "life_style_info":
//                        addLifestyleInfoImage(viewProfileSectionBean.getViewProfileFieldList());
//                        break;
                    case "location_info":
                        holder.lblSection.setText("Contact Details");
//                        addLocationInfoImage(viewProfileSectionBean.getViewProfileFieldList());
                        break;
//                    case "family_info":
//                        addFamilyInfoImage(viewProfileSectionBean.getViewProfileFieldList());
//                        break;
//
//                    case "photo_info":
//                        addPhotoInfoImage(viewProfileSectionBean.getViewProfileFieldList());
//                        break;
//
//                    case "basic_partner_info":
//                        addBasicPartnerInfoImage(viewProfileSectionBean.getViewProfileFieldList());
//                        break;
//
//                    case "religion_partner_info":
//                        addReligionPartnerInfoImage(viewProfileSectionBean.getViewProfileFieldList());
//                        break;
//
//                    case "location_partner_info":
//                        addLocationPartnerInfoImage(viewProfileSectionBean.getViewProfileFieldList());
//                        break;
//
//                    case "edu_occup_partner_info":
//                        addEducationInfoImage(viewProfileSectionBean.getViewProfileFieldList());
//                        break;

                }


                AppDebugLog.print("Section : " + viewProfileSectionBean.getId());
                AppDebugLog.print("Section : " + viewProfileSectionBean.getName());
                holder.lblSection.setOnClickListener(v -> {

                    mItemClickListener.lastSectionExpand(viewProfileSectionBean);

                    if (!isEditEnabled && viewProfileSectionBean.getId().equals("contact_info")) {
                        if (viewProfileSectionBean.isContactVisible) {
                            mSectionStateChangeListener.onSectionStateChanged(viewProfileSectionBean, !viewProfileSectionBean.isExpanded);
                        } else {
                            mItemClickListener.viewContact(viewProfileSectionBean);
                        }
                    } else if (position > 0) {
                        mSectionStateChangeListener.onSectionStateChanged(viewProfileSectionBean, !viewProfileSectionBean.isExpanded);
                    }


                });
                holder.imgSection.setOnClickListener(v -> {
                    if (!isEditEnabled && viewProfileSectionBean.getId().equals("contact_info")) {
                        if (viewProfileSectionBean.isContactVisible) {
                            mSectionStateChangeListener.onSectionStateChanged(viewProfileSectionBean, !viewProfileSectionBean.isExpanded);
                        } else {
                            mItemClickListener.viewContact(viewProfileSectionBean);
                        }
                    } else if (position > 0) {
                        mSectionStateChangeListener.onSectionStateChanged(viewProfileSectionBean, !viewProfileSectionBean.isExpanded);
                    }
                });

                if (isEditEnabled) {
                    holder.btnEdit.setVisibility(View.VISIBLE);
                    holder.btnEdit.setOnClickListener(v -> {
                        mItemClickListener.itemClicked(viewProfileSectionBean);
                    });
                } else {
                    holder.btnEdit.setVisibility(View.GONE);
                }

                if (viewProfileSectionBean.getId() != null && viewProfileSectionBean.getId().length() > 0) {
                    try {
                        holder.imgSection.setImageDrawable(res.getDrawable(res.getIdentifier(viewProfileSectionBean.getId().toLowerCase(), "drawable", mContext.getPackageName())));
                    } catch (Exception e) {
                        holder.imgSection.setImageResource(R.drawable.basic_info);
                    }
                } else {
                    holder.imgSection.setImageResource(R.drawable.basic_info);
                }


                break;
        }
    }

    private void setspaceaftercomma(ViewProfileFieldsBean item, ViewHolder holder) {

        String[] valArr = item.getValue().split(",");
        if (valArr.length > 2) {
            holder.lblValue.setText(item.getValue().replaceAll("[,.!?;:]", "$0 ").replaceAll("\\s+", " "));
        } else {
            holder.lblValue.setText(checkField(item.getValue()));
        }
    }

    private String checkField(String val) {
        if (val.equals("") || val.equals("null")) {
            return "N/A";
        }
        return val;
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSection(position))
            return VIEW_TYPE_SECTION;
        else return VIEW_TYPE_ITEM;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        //common
        View view;
        int viewType;

        //for section
        TextView lblSection;
        ImageView btnEdit, imgSection, ivIcon;
        View bottomDivider;
        LinearLayout llRow;

        //for item
        TextView lblName, lblValue;
        String maritalStatus="";

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                ivIcon = view.findViewById(R.id.ivIcon);
                lblName = view.findViewById(R.id.lblName);
                lblValue = view.findViewById(R.id.lblValue);
                bottomDivider = view.findViewById(R.id.bottomDivider);
                llRow = view.findViewById(R.id.llRow);
            } else {
                lblSection = view.findViewById(R.id.lblSection);
                btnEdit = view.findViewById(R.id.btnEdit);
                imgSection = view.findViewById(R.id.imgSection);
            }
        }
    }
}
