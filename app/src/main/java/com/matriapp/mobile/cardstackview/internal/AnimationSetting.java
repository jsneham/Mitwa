package com.matriapp.mobile.cardstackview.internal;

import android.view.animation.Interpolator;

import com.matriapp.mobile.cardstackview.Direction;

public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
