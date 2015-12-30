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
  private static final int INVALID_POINTER_ID = -1;

  private Bitmap image;
  private Bitmap frame;
  private float scale = 1.0f;
  private Rect frameDestinationRect = new Rect();

  private int activePointerId = INVALID_POINTER_ID;
  private float activePointerX;
  private float activePointerY;
  private float positionX;
  private float positionY;

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
      frameDestinationRect.right = frame.getWidth();
      frameDestinationRect.bottom = frame.getHeight();
    }

    attrs.recycle();
    gestureDetector = new ScaleGestureDetector(context, this);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.save();
    canvas.translate(positionX, positionY);
    canvas.scale(scale, scale);
    canvas.drawBitmap(image, 0, 0, null);
    canvas.restore();

    canvas.drawBitmap(frame, null, frameDestinationRect, null);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    gestureDetector.onTouchEvent(event);

    final int action = event.getAction();
    switch(action & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        activePointerId = event.getPointerId(0);
        activePointerX = event.getX(activePointerId);
        activePointerY = event.getY(activePointerId);
        break;
      case MotionEvent.ACTION_UP:
        activePointerId = INVALID_POINTER_ID;
        break;
      case MotionEvent.ACTION_POINTER_UP: {
        // Extract the index of the pointer that left the touch sensor
        final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = event.getPointerId(pointerIndex);

        if (pointerId == activePointerId) { // This was our active pointer going up. Choose a new active pointer and adjust accordingly.
          final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
          activePointerX = event.getX(newPointerIndex);
          activePointerY = event.getY(newPointerIndex);
          activePointerId = event.getPointerId(newPointerIndex);
        }
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        final int pointerIndex = event.findPointerIndex(activePointerId);

        final float x = event.getX(pointerIndex);
        final float y = event.getY(pointerIndex);

        positionX += x - activePointerX;
        positionY += y - activePointerY;

        activePointerX = x;
        activePointerY = y;

        invalidate();
        return true;
      }
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
