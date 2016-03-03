package com.antonionicolaspina.framedimageview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public final class FramedImageView extends View implements ScaleGestureDetector.OnScaleGestureListener, RotationGestureDetector.OnRotationGestureListener {
  private static final int INVALID_POINTER_ID = -1;

  private ScaleGestureDetector gestureDetector;
  private RotationGestureDetector rotateDetector;

  private Bitmap image;
  private Bitmap frame;
  private float minScale = 0.25f;
  private float maxScale = 5f;
  private float scale = 1.0f;
  private float relativeScale = 1.0f;
  private Rect frameDestinationRect = new Rect();
  private float rotation = 0f;

  private int activePointerId = INVALID_POINTER_ID;
  private float activePointerX;
  private float activePointerY;
  private PointF position = new PointF();
  private PointF rotationCenter = new PointF();

  private int viewWidth;
  private int viewHeight;

  private boolean panEnabled;
  private boolean rotationEnabled;

  /******************
   ** Constructors **
   ******************/
  public FramedImageView(Context context) {
    super(context);
    init(context, null);
  }

  public FramedImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public FramedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public FramedImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  protected void init(Context context, AttributeSet attributes) {
    if (null != attributes) {
      TypedArray attrs = context.getTheme().obtainStyledAttributes(attributes, R.styleable.FramedImageView, 0, 0);
      int imageResId = attrs.getResourceId(R.styleable.FramedImageView_fiv_image, View.NO_ID);
      if (View.NO_ID != imageResId) {
        setImage(imageResId);
      }

      int frameResId = attrs.getResourceId(R.styleable.FramedImageView_fiv_frame, View.NO_ID);
      if (View.NO_ID != frameResId) {
        setFrame(frameResId);
      }

      minScale = attrs.getFloat(R.styleable.FramedImageView_fiv_minScale, minScale);
      maxScale = attrs.getFloat(R.styleable.FramedImageView_fiv_maxScale, maxScale);
      panEnabled = attrs.getBoolean(R.styleable.FramedImageView_fiv_panEnabled, panEnabled);
      rotationEnabled = attrs.getBoolean(R.styleable.FramedImageView_fiv_rotationEnabled, rotationEnabled);
      attrs.recycle();
    }

    gestureDetector = new ScaleGestureDetector(context, this);
    rotateDetector  = new RotationGestureDetector(this);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (null != image) {
      canvas.save();

      if (panEnabled) {
        canvas.translate(position.x, position.y);
      }

      canvas.scale(scale, scale);

      if (rotationEnabled) {
        canvas.rotate(-rotation, activePointerX, activePointerY);
      }

      canvas.drawBitmap(image, 0, 0, null);
      canvas.restore();
    }

    if (null != frame) {
      canvas.drawBitmap(frame, null, frameDestinationRect, null);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    gestureDetector.onTouchEvent(event);
    rotateDetector.onTouchEvent(event);
    super.onTouchEvent(event);

    final int action = event.getAction();
    switch(action & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        activePointerId = event.getPointerId(0);
        activePointerX = event.getX(activePointerId);
        activePointerY = event.getY(activePointerId);
        return true;
      case MotionEvent.ACTION_UP:
        activePointerId = INVALID_POINTER_ID;
        return true;
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
        return true;
      }
      case MotionEvent.ACTION_MOVE: {
        final int pointerIndex = event.findPointerIndex(activePointerId);

        final float x = event.getX(pointerIndex);
        final float y = event.getY(pointerIndex);

        if (panEnabled) {
          position.x += x - activePointerX;
          position.y += y - activePointerY;
          invalidate();
        }

        activePointerX = x;
        activePointerY = y;

        return true;
      }
    }
    return false;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    viewWidth  = MeasureSpec.getSize(widthMeasureSpec);
    viewHeight = MeasureSpec.getSize(heightMeasureSpec);

    if (null != frame) {
      final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
      final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

      final float frameWidth  = frame.getWidth();
      final float frameHeight = frame.getHeight();

      if (MeasureSpec.UNSPECIFIED == widthMode) {
        viewWidth = (int) frameWidth;
      }
      if (MeasureSpec.UNSPECIFIED == heightMode) {
        viewHeight = (int) frameHeight;
      }

      final float scale = Math.min(viewWidth/frameWidth, viewHeight/frameHeight);
      viewWidth  = (int) (scale*frameWidth);
      viewHeight = (int) (scale*frameHeight);
    }

    resetPositions();

    frameDestinationRect.right  = viewWidth;
    frameDestinationRect.bottom = viewHeight;
    setMeasuredDimension(viewWidth, viewHeight);
  }

  @Override
  public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
    float scaleFactor = scaleGestureDetector.getScaleFactor();
    float relativeFactor = scale/relativeScale;
    relativeScale = Math.max(minScale, Math.min(relativeScale*scaleFactor, maxScale));
    scale         = relativeFactor*relativeScale;
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

  @Override
  public void OnRotation(RotationGestureDetector rotationDetector) {
    if (rotationEnabled) {
      rotation = rotationDetector.getAngle();
      rotationCenter.x = activePointerX;
      rotationCenter.y = activePointerY;
      invalidate();
    }
  }

  /******************
   ***** Public ****
   ******************/

  /**
   * Set a drawable as the touch-controlled image.
   * @param resourceId Resource identificator.
   */
  public void setImage(int resourceId) {
    setImage(BitmapFactory.decodeResource(getResources(), resourceId));
  }

  /**
   * Set a drawable as the overlayed frame over the image.
   * @param resourceId Resource identificator.
   */
  public void setFrame(int resourceId) {
    setFrame(BitmapFactory.decodeResource(getResources(), resourceId));
  }

  /**
   * Set a bitmap as the touch-controlled image.
   * @param bitmap Bitmap.
   */
  public void setImage(Bitmap bitmap) {
    image = bitmap;
    resetPositions();
    invalidate();
  }

  /**
   * Set a bitmap as the overlayed frame over the image.
   * @param bitmap Bitmap.
   */
  public void setFrame(Bitmap bitmap) {
    frame = bitmap;
    requestLayout();
  }

  /**
   * Reset image position and scale.
   */
  public void resetPositions() {
    if (null != image) {
      final float imageWidth  = image.getWidth();
      final float imageHeight = image.getHeight();
      scale = Math.min(viewWidth/imageWidth, viewHeight/imageHeight);
      relativeScale = 1f;
      position.x = (viewWidth - imageWidth*scale)/2f;
      position.y = (viewHeight - imageHeight*scale)/2f;
    }
  }

  /**
   * Get relative scale between image and frame.
   * @return scale as a per-one value.
   */
  public float getScale() {
    return relativeScale;
  }

  /**
   * Return offset position between the image and the frame.
   * @return Pointf containing x and y offsets, as a per-one value. Eg. 0=top-left, 1=bottom-right.
   */
  public PointF getOffset() {
    return new PointF(position.x/viewWidth, position.y/viewHeight);
  }

  /**
   * Return image rotation.
   * @return rotation in degrees.
   */
  public float getRotation() {
    return -rotation;
  }


  /**
   * Return rotation center position.
   * @return Pointf containing x and y offsets, as a per-one value. Eg. 0=top-left, 1=bottom-right.
   */
  public PointF getRotationCenter() {
    return new PointF(rotationCenter.x/viewWidth, rotationCenter.y/viewHeight);
  }
}
