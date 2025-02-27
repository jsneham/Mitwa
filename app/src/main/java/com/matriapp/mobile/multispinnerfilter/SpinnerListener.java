package com.matriapp.mobile.multispinnerfilter;

public interface SpinnerListener {

    void onItemsSelected(MultiSpinnerSearch singleSpinnerSearch);
    void onItemsSelected(SingleSpinnerSearch singleSpinnerSearch, KeyPairBoolData item);

}
