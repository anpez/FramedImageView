package com.antonionicolaspina.framedimageview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public final class FramedImageView extends View implements ScaleGestureDetector.OnScaleGestureListener {
  private static final String TAG = FramedImageView.class.getSimpleName();

  private Bitmap image;
  private Bitmap frame;
  private float scale = 1.0f;

  public FramedImageView(Context context) {
    super(context);
    init(context, null);
  }

  public FramedImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, context.getTheme().obtainStyledAttributes(attrs, R.styleable.FramedImageView, 0, 0));
  }

  public FramedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, context.getTheme().obtainStyledAttributes(attrs, R.styleable.FramedImageView, 0, 0));
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public FramedImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, context.getTheme().obtainStyledAttributes(attrs, R.styleable.FramedImageView, 0, 0));
  }

  ScaleGestureDetector gestureDetector;
  protected void init(Context context, TypedArray attrs) {
    int imageResId = attrs.getResourceId(R.styleable.FramedImageView_image, View.NO_ID);
    if (View.NO_ID != imageResId) {
      image = BitmapFactory.decodeResource(context.getResources(), imageResId);
    }
    int frameResId = attrs.getResourceId(R.styleable.FramedImageView_frame, View.NO_ID);
    if (View.NO_ID != frameResId) {
      frame = BitmapFactory.decodeResource(context.getResources(), frameResId);
    }

    attrs.recycle();
    gestureDetector = new ScaleGestureDetector(context, this);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.save();
    canvas.scale(scale, scale);
    canvas.drawBitmap(image, null, new Rect(0, 0, image.getWidth(), image.getHeight()), null);
    canvas.restore();

    canvas.drawBitmap(frame, null, new Rect(0, 0, frame.getWidth(), frame.getHeight()), null);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    gestureDetector.onTouchEvent(event);
    switch(event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        return true;
    }
    return true;
  }

  @Override
  public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
    float scale = scaleGestureDetector.getScaleFactor();
    this.scale *= scale;
    invalidate();
    return true;
  }

  @Override
  public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
    return true;
  }

  @Override
  public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
  }
}
