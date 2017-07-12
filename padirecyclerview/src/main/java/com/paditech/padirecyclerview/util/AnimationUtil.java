package com.paditech.padirecyclerview.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.paditech.padirecyclerview.R;


/**
 * Created by Nha Nha on 7/3/2017.
 */

public class AnimationUtil {

    public static void goneViewScaleFade(final Context context, final View view) {
        try {
            if (view != null) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_fade_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.startAnimation(animation);
                view.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void visibleViewScaleFade(Context context, final View view) {
        try {
            view.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_fade_in);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
