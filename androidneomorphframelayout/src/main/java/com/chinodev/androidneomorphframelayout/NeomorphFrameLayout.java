package com.chinodev.androidneomorphframelayout;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

public class NeomorphFrameLayout extends FrameLayout {
    //attributes
    private String SHAPE_TYPE;
    private String SHADOW_TYPE;
    private int CORNER_RADIUS;
    private int ELEVATION;
    private int HIGHLIGHT_COLOR;
    private int SHADOW_COLOR;
    private int BACKGROUND_COLOR;
    //private boolean CLICKABLE;

    //global variables
    private int SHAPE_PADDING = 0;
    //constants
    private final String SHAPE_TYPE_RECTANGLE = "1";
    private final String SHAPE_TYPE_CIRCLE = "2";
    private final String SHADOW_TYPE_OUTER = "1";
    private final String SHADOW_TYPE_INNER = "2";
    //global objects
    private Paint basePaint;
    private Paint paintShadow;
    private Paint paintHighLight;
    private Path basePath;
    private Path pathShadow;
    private Path pathHighlight;
    private RectF rectangle;


    public NeomorphFrameLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public NeomorphFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public NeomorphFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        int defaultElevation = (int) context.getResources().getDimension(R.dimen.neomorph_view_elevation);
        int defaultCornerRadius = (int) context.getResources().getDimension(R.dimen.neomorph_view_corner_radius);

        if (attrs != null) {
            //get attrs array
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NeomorphFrameLayout);
            //get all attributes
            SHAPE_TYPE = a.getString(R.styleable.NeomorphFrameLayout_neomorph_view_type);
            if (SHAPE_TYPE == null) {
                SHAPE_TYPE = SHAPE_TYPE_RECTANGLE;
            }

            SHADOW_TYPE = a.getString(R.styleable.NeomorphFrameLayout_neomorph_shadow_type);
            if (SHADOW_TYPE == null) {
                SHADOW_TYPE = SHADOW_TYPE_OUTER;
            }

            ELEVATION = a.getDimensionPixelSize(R.styleable.NeomorphFrameLayout_neomorph_elevation, defaultElevation);
            CORNER_RADIUS = a.getDimensionPixelSize(R.styleable.NeomorphFrameLayout_neomorph_corner_radius, defaultCornerRadius);
            BACKGROUND_COLOR = a.getColor(R.styleable.NeomorphFrameLayout_neomorph_background_color,
                    ContextCompat.getColor(context, R.color.neomorph_background_color));
            SHADOW_COLOR = a.getColor(R.styleable.NeomorphFrameLayout_neomorph_shadow_color,
                    ContextCompat.getColor(context, R.color.neomorph_shadow_color));
            HIGHLIGHT_COLOR = a.getColor(R.styleable.NeomorphFrameLayout_neomorph_highlight_color,
                    ContextCompat.getColor(context, R.color.neomorph_highlight_color));
            //CLICKABLE = a.getBoolean(R.styleable.NeoMorphFrameLayout_neomorph_clickable, false);

            a.recycle();
        } else {
            SHAPE_TYPE = "rectangle";
            ELEVATION = defaultElevation;
            CORNER_RADIUS = defaultCornerRadius;
            BACKGROUND_COLOR = ContextCompat.getColor(context, R.color.neomorph_background_color);
            SHADOW_COLOR = ContextCompat.getColor(context, R.color.neomorph_shadow_color);
            HIGHLIGHT_COLOR = ContextCompat.getColor(context, R.color.neomorph_highlight_color);
        }

        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintHighLight = new Paint(Paint.ANTI_ALIAS_FLAG);

        basePaint.setColor(BACKGROUND_COLOR);
        //paint for shadow
        paintShadow.setColor(BACKGROUND_COLOR);
        paintShadow.setShadowLayer(ELEVATION, ELEVATION, ELEVATION, SHADOW_COLOR);
        //paint for highlight
        paintHighLight.setColor(BACKGROUND_COLOR);
        paintHighLight.setShadowLayer(ELEVATION, -ELEVATION, -ELEVATION, HIGHLIGHT_COLOR);


        basePath = new Path();
        pathHighlight = new Path();
        pathShadow = new Path();

        //TODO: make SHAPE_PADDING dynamic
        SHAPE_PADDING = ELEVATION * 2;

        //setOnTouchListener(onTouchListener);
        setWillNotDraw(false);
        //should be SW accelerated, since HW doesn't support paint.setShadowLayer();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectangle = new RectF(SHAPE_PADDING, SHAPE_PADDING, this.getWidth() - SHAPE_PADDING, this.getHeight() - SHAPE_PADDING);
        resetPath(w, h);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPadding(SHAPE_PADDING, SHAPE_PADDING, SHAPE_PADDING, SHAPE_PADDING);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (SHADOW_TYPE) {
            case SHADOW_TYPE_INNER:
                canvas.clipPath(basePath);
                break;
            default:
            case SHADOW_TYPE_OUTER:
                break;
        }
        paintShadow.setAlpha(155);
        paintHighLight.setAlpha(155);
        canvas.drawPath(basePath, basePaint);
        canvas.drawPath(pathShadow, paintShadow);
        canvas.drawPath(pathHighlight, paintHighLight);

        /*switch (SHAPE_TYPE) {
            case SHAPE_TYPE_CIRCLE:
                int radius = (this.getWidth() / 2) - SHAPE_PADDING;
                canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, radius, paintShadow);
                canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, radius, paintHighLight);
                break;
            default:
            case SHAPE_TYPE_RECTANGLE:

                canvas.drawRoundRect(
                        new RectF(SHAPE_PADDING, SHAPE_PADDING, this.getWidth() - SHAPE_PADDING, this.getHeight() - SHAPE_PADDING),
                        CORNER_RADIUS,
                        CORNER_RADIUS,
                        paintShadow);
                canvas.drawRoundRect(
                        new RectF(SHAPE_PADDING, SHAPE_PADDING, this.getWidth() - SHAPE_PADDING, this.getHeight() - SHAPE_PADDING),
                        CORNER_RADIUS,
                        CORNER_RADIUS,
                        paintHighLight);
                break;
        }*/

        //rectangle = new RectF(SHAPE_PADDING, SHAPE_PADDING, this.getWidth() - SHAPE_PADDING, this.getHeight() - SHAPE_PADDING);
    }

    private void resetPath(int w, int h) {
        basePath.reset();
        pathHighlight.reset();
        pathShadow.reset();

        switch (SHAPE_TYPE) {
            case SHAPE_TYPE_CIRCLE:
                //get max suitable diameter, which is the smallest dimension
                int maxDiameter = this.getWidth() < this.getHeight() ? this.getWidth() : this.getHeight();
                int radius = (maxDiameter / 2) - SHAPE_PADDING;
                basePath.addCircle(w / 2, h / 2, radius, Path.Direction.CW);
                pathHighlight.addCircle(w / 2, h / 2, radius, Path.Direction.CW);
                pathShadow.addCircle(w / 2, h / 2, radius, Path.Direction.CW);
                break;
            default:
            case SHAPE_TYPE_RECTANGLE:
                basePath.addRoundRect(rectangle, CORNER_RADIUS, CORNER_RADIUS, Path.Direction.CW);
                pathHighlight.addRoundRect(rectangle, CORNER_RADIUS, CORNER_RADIUS, Path.Direction.CW);
                pathShadow.addRoundRect(rectangle, CORNER_RADIUS, CORNER_RADIUS, Path.Direction.CW);
                break;
        }

        if (SHADOW_TYPE.equals(SHADOW_TYPE_INNER)) {
            if (!pathHighlight.isInverseFillType()) {
                pathHighlight.toggleInverseFillType();
            }
            if (!pathShadow.isInverseFillType()) {
                pathShadow.toggleInverseFillType();
            }
        }

        basePath.close();
        pathHighlight.close();
        pathShadow.close();
    }
}
