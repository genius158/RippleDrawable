package com.yan.rippledrawable;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author genius158
 */
public class RippleLayout extends ViewGroup implements View.OnLayoutChangeListener {
  private static final int DEFAULT_COLOR = Color.parseColor("#24000000");

  private View child;
  private Drawable rippleDrawable;
  private int rippleColor;
  private int rippleMaskId;

  public RippleLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RippleLayout);
    rippleColor = ta.getColor(R.styleable.RippleLayout_rippleColor, DEFAULT_COLOR);
    rippleMaskId = ta.getResourceId(R.styleable.RippleLayout_rippleMask, -1);
    ta.recycle();
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    if (getChildCount() > 1) {
      throw new IllegalStateException("can only hold one child");
    }

    child = getChildAt(0);
    if (child == null) {
      throw new IllegalStateException("need a child to dell logic");
    }
  }

  @Override public void setClickable(boolean clickable) {
    super.setClickable(false);
  }

  @Override public void setOnClickListener(final OnClickListener onClickListener) {
    child.setOnClickListener(onClickListener);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    child.addOnLayoutChangeListener(this);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    child.removeOnLayoutChangeListener(this);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
    setMeasuredDimension(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(), MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.EXACTLY));
  }

  @Override protected boolean checkLayoutParams(LayoutParams p) {
    return p instanceof MarginLayoutParams;
  }

  @Override protected LayoutParams generateDefaultLayoutParams() {
    return new MarginLayoutParams(-1, -1);
  }

  @Override protected LayoutParams generateLayoutParams(LayoutParams p) {
    return new MarginLayoutParams(p);
  }

  @Override public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new MarginLayoutParams(getContext(), attrs);
  }

  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    child.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
  }

  @Override
  public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
      int oldTop, int oldRight, int oldBottom) {

    boolean bgChanged = rippleDrawable != child.getBackground()
        || child.getBackground() == null && rippleDrawable == null;

    if (bgChanged) {
      Drawable drawableBG = child.getBackground();
      Drawable drawableMask =
          rippleMaskId == -1 ? drawableBG : ContextCompat.getDrawable(getContext(), rippleMaskId);
      rippleDrawable = getRippleDrawable(drawableBG, drawableMask, rippleColor);
      ViewCompat.setBackground(child, rippleDrawable);
    }
  }

  public static Drawable getRippleDrawable(Drawable drawable) {
    return getRippleDrawable(drawable, DEFAULT_COLOR);
  }

  public static Drawable getRippleDrawable(Drawable drawable, int color) {
    return getRippleDrawable(drawable, drawable, color);
  }

  public static Drawable getRippleDrawable(Drawable drawable, Drawable mask, int color) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      return new RippleDrawable(ColorStateList.valueOf(color), drawable, mask);
    }

    if (drawable == null
        || drawable instanceof StateListDrawable
        || drawable instanceof ShapeDrawable
        || drawable instanceof GradientDrawable
        || drawable instanceof NinePatchDrawable
        || drawable instanceof BitmapDrawable) {
      return new DrawableWithCoverTint(drawable, color);
    }

    return new DrawableWithCover(drawable, color);
  }
}
