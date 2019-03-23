package com.yan.rippledrawable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.view.animation.DecelerateInterpolator;

class RippleDrawableWrap extends AbstractDrawableWithCover {
  private final int DURING_ALPHA = 350;
  private final Paint paint;
  private int paintAlpha;
  private final PointF touchPoint = new PointF();
  private final RippleAnim rippleAnim;

  RippleDrawableWrap(Drawable original, Drawable mask, int color) {
    super(original, mask);
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(color);
    paint.setStyle(Paint.Style.FILL);
    paint.setColorFilter(new PorterDuffColorFilter(
        Color.argb(185, Color.red(color), Color.green(color), Color.blue(color)),
        PorterDuff.Mode.SRC_IN));
    paintAlpha = paint.getAlpha();

    rippleAnim = new RippleAnim();
  }

  @Override public void draw(@NonNull Canvas canvas) {
    super.draw(canvas);
    if (rippleAnim.rippleAnim != null) {
      canvas.drawCircle(touchPoint.x, touchPoint.y, rippleAnim.radius, paint);
    }
  }

  private float getMaxRadius(boolean withPoint) {
    float h = bounds.width();
    float v = bounds.height();
    if (withPoint) {
      h = Math.max(touchPoint.x, bounds.width() - touchPoint.x);
      v = Math.max(touchPoint.y, bounds.height() - touchPoint.y);
    }
    return (float) Math.sqrt(Math.pow(h, 2) + Math.pow(v, 2));
  }

  @Override public boolean setVisible(boolean visible, boolean restart) {
    if (!visible) {
      rippleAnim.end();
      rippleAnim.rippleAnim = null;
      paint.setShader(null);
    }
    return super.setVisible(visible, restart);
  }

  @Override void onStateChange(boolean enabled, boolean pressed, boolean focused, boolean hovered) {
    boolean isCoverShow = enabled && (pressed || focused || hovered);
    if (enabled && pressed) {
      rippleAnim.tapDown();
    }
    if (isCoverShow) {
      loadShader();
    } else {
      rippleAnim.tapUp();
    }
    if (rippleAnim.rippleAnim != null) {
      invalidateSelf();
    }
  }

  private void loadShader() {
    if (paint.getShader() != null) {
      return;
    }
    Drawable drawable = getMask();
    Bitmap coverBitmap =
        Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ALPHA_8);
    Shader shader = new BitmapShader(coverBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    Canvas canvas = new Canvas(coverBitmap);
    if (drawable == null) {
      drawable = new ShapeDrawable();
      drawable.setBounds(bounds);
    }
    drawable.draw(canvas);
    paint.setShader(shader);
  }

  @Override public void setHotspot(float x, float y) {
    super.setHotspot(x, y);
    x = Math.max(x, 0);
    x = Math.min(x, bounds.width());
    y = Math.max(y, 0);
    y = Math.min(y, bounds.height());
    touchPoint.set(x, y);
  }

  class RippleAnim {
    private ValueAnimator rippleAnim;
    private float radius = 0;

    private ValueAnimator getAnim(int during, ValueAnimator.AnimatorUpdateListener updateListener) {
      ValueAnimator rippleAnim = ValueAnimator.ofInt(0, during);
      rippleAnim.setDuration(during);
      rippleAnim.addUpdateListener(updateListener);
      return rippleAnim;
    }

    void tapDown() {
      final float maxRadius = getMaxRadius(false);
      final float touchRadius = getMaxRadius(true);
      final float startRadius = Math.min(touchRadius / 4, 50);
      final int startAlpha = paintAlpha;
      cancel();

      final int enterDuring = (int) (maxRadius * (maxRadius / touchRadius));

      rippleAnim = getAnim(enterDuring, new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator animation) {
          int value = (int) animation.getAnimatedValue();
          if (value <= DURING_ALPHA) {
            float offset = value / (float) DURING_ALPHA;
            paint.setAlpha((int) (offset * startAlpha));
          } else if (paint.getAlpha() != startAlpha) {
            paint.setAlpha(startAlpha);
          }

          float offset = value / (float) enterDuring;
          radius = offset * (maxRadius - startRadius) + startRadius;

          invalidateSelf();
        }
      });
      rippleAnim.setInterpolator(new DecelerateInterpolator(0.8F));
      rippleAnim.start();
    }

    void tapUp() {
      if (rippleAnim == null) {
        return;
      }
      cancel();

      final float maxRadius = getMaxRadius(true);
      final float startRadius = Math.max(maxRadius / 3, radius);
      final float startAlpha = paintAlpha;

      rippleAnim = getAnim(DURING_ALPHA, new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator animation) {
          int value = (int) animation.getAnimatedValue();
          float offset = value / (float) DURING_ALPHA;
          radius = offset * (maxRadius - startRadius) + startRadius;
          radius = Math.min(radius, maxRadius);
          paint.setAlpha((int) ((1 - offset) * startAlpha));
          invalidateSelf();
        }
      });
      rippleAnim.setInterpolator(new DecelerateInterpolator());
      rippleAnim.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          rippleAnim = null;
        }
      });
      rippleAnim.start();
    }

    void cancel() {
      if (rippleAnim != null && rippleAnim.isRunning()) {
        rippleAnim.cancel();
      }
    }

    void end() {
      if (rippleAnim != null && rippleAnim.isRunning()) {
        rippleAnim.end();
      }
    }
  }
}