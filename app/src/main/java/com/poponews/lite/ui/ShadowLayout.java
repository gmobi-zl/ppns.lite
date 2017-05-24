package com.poponews.lite.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class ShadowLayout extends RelativeLayout {

    private int shadowColor = 0xAA000000;
    private float shadowRadius = 5;
    private float cornerRadius = 5;
    private float dx = 0;
    private float dy = 0;

    private boolean mInvalidateShadowOnSizeChanged = true;
    private boolean mForceInvalidateShadow = false;

    public ShadowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w > 0 && h > 0 && (getBackground() == null || mInvalidateShadowOnSizeChanged || mForceInvalidateShadow)) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mForceInvalidateShadow) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(right - left, bottom - top);
        }
    }

    public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
        mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
    }

    public void invalidateShadow() {
        mForceInvalidateShadow = true;
        requestLayout();
        invalidate();
    }

    private void initView(Context context, AttributeSet attrs) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        int xPadding = (int) (shadowRadius + Math.abs(dx));
        int yPadding = (int) (shadowRadius + Math.abs(dy));
        setPadding(xPadding, yPadding, xPadding, yPadding);
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {
    	/*
        Bitmap bitmap = createShadowBitmap(w, h, mCornerRadius, mShadowRadius, mDx, mDy, mShadowColor, Color.TRANSPARENT);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
        */
    }

    @Override
    public void draw(Canvas canvas) {
        int count = canvas.save();
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                w - shadowRadius,
                h - shadowRadius);

        if (dy > 0) {
            shadowRect.top += dy;
            shadowRect.bottom -= dy;
        } else if (dy < 0) {
            shadowRect.top += Math.abs(dy);
            shadowRect.bottom -= Math.abs(dy);
        }

        if (dx > 0) {
            shadowRect.left += dx;
            shadowRect.right -= dx;
        } else if (dx < 0) {
            shadowRect.left += Math.abs(dx);
            shadowRect.right -= Math.abs(dx);
        }

        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.TRANSPARENT);
        shadowPaint.setStyle(Paint.Style.FILL);

        if (!isInEditMode()) {
            shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        }

        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);

        Paint clientPaint = new Paint();
        clientPaint.setAntiAlias(true);
        clientPaint.setColor(Color.WHITE);
        clientPaint.setStyle(Paint.Style.FILL);
        clientPaint.setAlpha(255);
        RectF clientRect = new RectF(
        		shadowRect.left + cornerRadius,
        		shadowRect.top + cornerRadius,
        		shadowRect.right - cornerRadius,
        		shadowRect.bottom - cornerRadius);
        canvas.drawRoundRect(clientRect, cornerRadius, cornerRadius, clientPaint);

        final Path path = new Path();
        path.addRoundRect(clientRect, cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.clipPath(path, Region.Op.REPLACE);

        canvas.clipPath(path);
        super.draw(canvas);
        
        canvas.restoreToCount(count);
        
    }
    
    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius,
                                      float dx, float dy, int shadowColor, int fillColor) {

        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(output);

        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                shadowWidth - shadowRadius,
                shadowHeight - shadowRadius);

        if (dy > 0) {
            shadowRect.top += dy;
            shadowRect.bottom -= dy;
        } else if (dy < 0) {
            shadowRect.top += Math.abs(dy);
            shadowRect.bottom -= Math.abs(dy);
        }

        if (dx > 0) {
            shadowRect.left += dx;
            shadowRect.right -= dx;
        } else if (dx < 0) {
            shadowRect.left += Math.abs(dx);
            shadowRect.right -= Math.abs(dx);
        }

        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(fillColor);
        shadowPaint.setStyle(Paint.Style.FILL);

        if (!isInEditMode()) {
            shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        }

        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);

        Paint clientPaint = new Paint();
        clientPaint.setAntiAlias(true);
        clientPaint.setColor(Color.WHITE);
        clientPaint.setStyle(Paint.Style.FILL);
        clientPaint.setAlpha(255);
        /*
        canvas.drawRoundRect(new RectF(
        		shadowRect.left + cornerRadius,
        		shadowRect.top + cornerRadius,
        		shadowRect.right - cornerRadius,
        		shadowRect.bottom - cornerRadius), cornerRadius, cornerRadius, clientPaint);
        */
        return output;
    }

	public float getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(float mCornerRadius) {
		this.cornerRadius = mCornerRadius;
	}

	public int getShadowColor() {
		return shadowColor;
	}

	public void setShadowColor(int mShadowColor) {
		this.shadowColor = mShadowColor;
	}

	public float getShadowRadius() {
		return shadowRadius;
	}

	public void setShadowRadius(float mShadowRadius) {
		this.shadowRadius = mShadowRadius;
	}

	public void setShadowShift(float mDx, float mDy) {
		this.dx = mDx;
		this.dy = mDy;
	}

}