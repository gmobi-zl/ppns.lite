package com.poponews.lite.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mocean.IAdItem;
import com.poponews.lite.util.SystemHelper;

/**
 * Created by zl on 2017/5/24.
 */

public class DetailAdView extends RelativeLayout {

    protected final static int ID_ICON = View.generateViewId();
    protected final static int ID_TITLE = View.generateViewId();
    protected final static int ID_CTA = View.generateViewId();
    protected final static int ID_IMAGE = View.generateViewId();
    protected final static int ID_RATE = View.generateViewId();
    protected final static int ID_BODY = View.generateViewId();
    protected final static int ID_SUBTITLE = View.generateViewId();
    protected final static int ID_LIKE = View.generateViewId();

    private boolean layoutStyleDefault = false;
    private Context mContext;
    private boolean isInitViews = false;

    public RelativeLayout getViews(){
        return this;
    }

    public DetailAdView(Context context) {
        super(context);
        mContext = context;
    }

    public int dip2px(float dpValue) {
        if (mContext == null)
            return (int)dpValue;

        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dip(float pxValue) {
        if (mContext == null)
            return (int)pxValue;
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public void refresh(IAdItem item) {
        if (item == null) return;

        if (item.has(IAdItem.HTML) || item.has(IAdItem.VIDEO)|| !item.has(IAdItem.IMAGE) || !item.has(IAdItem.TITLE)) {
            layoutStyleDefault = true;
            return;
        }

        layoutStyleDefault = false;
        RelativeLayout rootContainer = this;

        float sd = SystemHelper.getScreenDensity(mContext);
        int DP50 = Math.round(50 * sd);
        int DP5 = Math.round(5 * sd);

        boolean hasIcon = item.has(IAdItem.ICON);
        IAdItem.ISize sz = item.getImageSize(IAdItem.IMAGE);

        GMADTopRoundImageView ivAdImage = null;
        TextView tvAdName = null;
        RatingBar rbRate = null;
        ImageView ivAdIcon = null;

        if (isInitViews == false){
            int sw = SystemHelper.getScreenWidth(mContext);
            int sh = SystemHelper.getScreenHeight(mContext);
            //int iw = Math.round(preferredWidth * sd);
            //int ih = Math.round(preferredHeight * sd);
            boolean portrait = sh > sw;

            int ID_HSCREEN_VIEW = View.generateViewId();
            RelativeLayout layout = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams rlpc = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layout.setLayoutParams(rlpc);

            rootContainer.addView(layout);

            RelativeLayout rlContainer = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams rlHScreenCoLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            rlHScreenCoLp.addRule(RelativeLayout.CENTER_IN_PARENT, 1);;
            rlContainer.setLayoutParams(rlHScreenCoLp);
            rlContainer.setId(ID_HSCREEN_VIEW);
            layout.addView(rlContainer);

            int ID_AD_BOTTOM_VIEW = View.generateViewId();

            int height = sh;
            int width = sw;
            if (portrait == false){
                int halfHeight = (height * 2 / 3);
                int halfW = width * 2 / 3;
                int ivHeight = halfHeight * 3 / 4;

                int imgW = halfW;
                int imgH = sz.getHeight() * imgW / sz.getWidth();

                double bottomHeight = ((double)ivHeight * (0.382 / 0.618));
                if (imgH > sh - (int)bottomHeight){
                    imgH = sh - (int)bottomHeight - 10;
                }

                ShadowLayout boxContainer = new ShadowLayout(mContext);
                boxContainer.setCornerRadius(15);
                //boxContainer.addShadow(8);
                RelativeLayout.LayoutParams boxLp = new RelativeLayout.LayoutParams(halfW, LayoutParams.WRAP_CONTENT);
                boxLp.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                boxLp.setMargins(dip2px(8), dip2px(10), dip2px(8), dip2px(10));
                boxContainer.setLayoutParams(boxLp);
                rlContainer.addView(boxContainer);

                ivAdImage = new GMADTopRoundImageView(mContext);
                RelativeLayout.LayoutParams ivAdImageLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, imgH);
                ivAdImageLp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                ivAdImageLp.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);


                GradientDrawable gd = new GradientDrawable();
                float ivRad[] = {15, 15, 15, 15, 0, 0, 0, 0};
                gd.setCornerRadii(ivRad);
                gd.setColor(Color.parseColor("#00ffffff"));
                ivAdImage.setId(ID_IMAGE);
                ivAdImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivAdImage.setLayoutParams(ivAdImageLp);
                ivAdImage.setBackground(gd);
                boxContainer.addView(ivAdImage);

                RelativeLayout bottomContainer = new RelativeLayout(mContext);
                RelativeLayout.LayoutParams bottomLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)bottomHeight);
                bottomLp.addRule(RelativeLayout.BELOW, ID_IMAGE);
                bottomLp.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                bottomContainer.setLayoutParams(bottomLp);
                GradientDrawable bottomgd = new GradientDrawable();
                float bottomRad[] = {0, 0, 0, 0, 15, 15, 15, 15};
                bottomgd.setCornerRadii(bottomRad);
                bottomgd.setColor(Color.parseColor("#f8f8f8"));
                bottomContainer.setBackground(bottomgd);
                bottomContainer.setId(ID_AD_BOTTOM_VIEW);
                boxContainer.addView(bottomContainer);

                tvAdName = new TextView(mContext);
                RelativeLayout.LayoutParams tvAdNameLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                tvAdNameLp.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
                //tvAdNameLp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                tvAdNameLp.setMargins(dip2px(8), 0, dip2px(8), 0);
                tvAdName.setLayoutParams(tvAdNameLp);
                tvAdName.setId(ID_TITLE);
                tvAdName.setTextColor(Color.parseColor("#000000"));
                tvAdName.setMaxLines(2);
                tvAdName.setTextSize(16);
                bottomContainer.addView(tvAdName);

                rbRate = new RatingBar(mContext, null, android.R.attr.ratingBarStyleSmall);
                LayoutParams lpRate = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lpRate.addRule(RelativeLayout.BELOW, ID_TITLE);
                lpRate.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                rbRate.setLayoutParams(lpRate);
                rbRate.setNumStars(5);
                rbRate.setIsIndicator(true);
                rbRate.setStepSize(0.1f);
                rbRate.setRating(5f);
                rbRate.setId(ID_RATE);
                Drawable progress = rbRate.getProgressDrawable();
                LayerDrawable stars = (LayerDrawable) progress;
                stars.getDrawable(2).setColorFilter(0xFFFF9900, PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(0xFFE0E0E0, PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(0).setColorFilter(0xFFE0E0E0, PorterDuff.Mode.SRC_ATOP);
                bottomContainer.addView(rbRate);

                ivAdIcon = new ImageView(mContext);
                RelativeLayout.LayoutParams ivAdIconLp = new RelativeLayout.LayoutParams(dip2px(46), dip2px(46));
                ivAdIconLp.addRule(RelativeLayout.ALIGN_TOP, ID_AD_BOTTOM_VIEW);
                ivAdIconLp.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                ivAdIconLp.setMargins(0, -dip2px(23), 0, 0);
                ivAdIcon.setId(ID_ICON);
                ivAdIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                ivAdIcon.setLayoutParams(ivAdIconLp);
                boxContainer.addView(ivAdIcon);
            } else {
                int halfHeight = (height / 2);

                int imgW = sw - (dip2px(8) * 2);
                int imgH = sz.getHeight() * imgW / sz.getWidth();
                int ivHeight = halfHeight * 3 / 4;
                double bottomHeight = ((double)ivHeight * (0.382 / 0.618));

                if (imgH > sh - (int)bottomHeight){
                    imgH = sh - (int)bottomHeight - 10;
                }

                ShadowLayout boxContainer = new ShadowLayout(mContext);
                boxContainer.setCornerRadius(15);
                //boxContainer.addShadow(8);
                RelativeLayout.LayoutParams boxLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                boxLp.setMargins(dip2px(8), dip2px(10), dip2px(8), dip2px(10));
                boxContainer.setLayoutParams(boxLp);
                rlContainer.addView(boxContainer);

                ivAdImage = new GMADTopRoundImageView(mContext);
                RelativeLayout.LayoutParams ivAdImageLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, imgH);
                ivAdImageLp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                ivAdImageLp.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);

                GradientDrawable gd = new GradientDrawable();
                float ivRad[] = {15, 15, 15, 15, 0, 0, 0, 0};
                gd.setCornerRadii(ivRad);
                gd.setColor(Color.parseColor("#00ffffff"));
                ivAdImage.setId(ID_IMAGE);
                ivAdImage.setScaleType(ImageView.ScaleType.FIT_XY);
                ivAdImage.setLayoutParams(ivAdImageLp);
                ivAdImage.setBackground(gd);
                boxContainer.addView(ivAdImage);

                RelativeLayout bottomContainer = new RelativeLayout(mContext);
                RelativeLayout.LayoutParams bottomLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)bottomHeight);
                bottomLp.addRule(RelativeLayout.BELOW, ID_IMAGE);
                bottomLp.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                bottomContainer.setLayoutParams(bottomLp);
                GradientDrawable bottomgd = new GradientDrawable();
                float bottomRad[] = {0, 0, 0, 0, 15, 15, 15, 15};
                bottomgd.setCornerRadii(bottomRad);
                bottomgd.setColor(Color.parseColor("#f8f8f8"));
                bottomContainer.setBackground(bottomgd);
                bottomContainer.setId(ID_AD_BOTTOM_VIEW);
                boxContainer.addView(bottomContainer);

                tvAdName = new TextView(mContext);
                RelativeLayout.LayoutParams tvAdNameLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                tvAdNameLp.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
                //tvAdNameLp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                tvAdNameLp.setMargins(dip2px(8), 0, dip2px(8), 0);
                tvAdName.setLayoutParams(tvAdNameLp);
                tvAdName.setId(ID_TITLE);
                tvAdName.setTextColor(Color.parseColor("#000000"));
                tvAdName.setMaxLines(2);
                tvAdName.setTextSize(18);
                bottomContainer.addView(tvAdName);

                rbRate = new RatingBar(mContext, null, android.R.attr.ratingBarStyleSmall);
                LayoutParams lpRate = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lpRate.addRule(RelativeLayout.BELOW, ID_TITLE);
                lpRate.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                rbRate.setLayoutParams(lpRate);
                rbRate.setNumStars(5);
                rbRate.setIsIndicator(true);
                rbRate.setStepSize(0.1f);
                rbRate.setRating(5f);
                rbRate.setId(ID_RATE);
                Drawable progress = rbRate.getProgressDrawable();
                LayerDrawable stars = (LayerDrawable) progress;
                stars.getDrawable(2).setColorFilter(0xFFFF9900, PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(0xFFE0E0E0, PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(0).setColorFilter(0xFFE0E0E0, PorterDuff.Mode.SRC_ATOP);
                bottomContainer.addView(rbRate);

                ivAdIcon = new ImageView(mContext);
                RelativeLayout.LayoutParams ivAdIconLp = new RelativeLayout.LayoutParams(dip2px(46), dip2px(46));
                ivAdIconLp.addRule(RelativeLayout.ALIGN_TOP, ID_AD_BOTTOM_VIEW);
                ivAdIconLp.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                ivAdIconLp.setMargins(0, -dip2px(23), 0, 0);
                ivAdIcon.setId(ID_ICON);
                ivAdIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                ivAdIcon.setLayoutParams(ivAdIconLp);
                boxContainer.addView(ivAdIcon);
            }

            isInitViews = true;
        }

        if (isInitViews == true){
            int bindViewCount = countBindViews(item);
            if (bindViewCount > 0){
                String[] bindKeys = new String[bindViewCount];
                View[] bindViews = new View[bindViewCount];
                int index = 0;
                if (item.has(IAdItem.TITLE)){
                    bindKeys[index] = IAdItem.TITLE;
                    bindViews[index] = tvAdName;
                    index++;
                }
                if (item.has(IAdItem.IMAGE)){
                    bindKeys[index] = IAdItem.IMAGE;
                    bindViews[index] = ivAdImage;
                    index++;
                }
                if (item.has(IAdItem.RATE)){
                    bindKeys[index] = IAdItem.RATE;
                    bindViews[index] = rbRate;
                    index++;
                }
                if (item.has(IAdItem.ICON)){
                    bindKeys[index] = IAdItem.ICON;
                    bindViews[index] = ivAdIcon;
                    index++;
                }

                item.bind(bindKeys, bindViews);

            } else {
                item.bind(new String[]{IAdItem.IMAGE, IAdItem.TITLE, IAdItem.ICON},
                        new View[]{ivAdImage, tvAdName, ivAdIcon});
            }
        }
    }

    private int countBindViews(IAdItem item){
        int count = 0;
        if (item == null) return count;

        if (item.has(IAdItem.TITLE))
            count++;

        if (item.has(IAdItem.IMAGE))
            count++;

        if (item.has(IAdItem.RATE))
            count++;

        if (item.has(IAdItem.ICON))
            count++;

        return count;
    }

    /**
     * Top Round Corner Image view
     */
    public class GMADTopRoundImageView extends ImageView{

        private float[] rids = {15.0f,15.0f,15.0f,15.0f,0.0f,0.0f,0.0f,0.0f};
        private Path mPath;
        private RectF mRectF;

        public GMADTopRoundImageView(Context context) {
            super(context);
            mPath = new Path();
            mRectF = new RectF();
        }

        public GMADTopRoundImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public GMADTopRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            mPath = new Path();
            mRectF = new RectF();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mRectF.set(0,0,w,h);
            mPath.reset();
            mPath.addRoundRect(mRectF, rids, Path.Direction.CW);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int count = canvas.save();
            //canvas.clipPath(mPath, Region.Op.REPLACE);
            canvas.clipPath(mPath);
            super.onDraw(canvas);
            canvas.restoreToCount(count);
        }
    }
}
