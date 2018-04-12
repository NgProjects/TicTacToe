package tictactoe.angie.com.tictactoe;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class TicTacToeForeGroundView extends AppCompatImageView {

    private Drawable defaultForeGround;

    public TicTacToeForeGroundView(Context context) {
        this(context, null);
    }

    public TicTacToeForeGroundView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ForegroundImageView);
        Drawable foreGround = typedArray.getDrawable(0);
        if (foreGround != null) {
            setForeground(foreGround);
        }
        typedArray.recycle();
    }

    public void setForeground(Drawable drawable) {

        if (defaultForeGround == drawable) {
            return;
        }
        if (defaultForeGround != null) {
            defaultForeGround.setCallback(null);
            unscheduleDrawable(defaultForeGround);
        }

        defaultForeGround = drawable;

        if (drawable != null) {
            drawable.setCallback(this);
            if (drawable.isStateful()) {
                drawable.setState(getDrawableState());
            }
        }
        requestLayout();
        invalidate();
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == defaultForeGround;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (defaultForeGround != null) defaultForeGround.jumpToCurrentState();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (defaultForeGround != null && defaultForeGround.isStateful()) {
            defaultForeGround.setState(getDrawableState());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (defaultForeGround != null) {
            defaultForeGround.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (defaultForeGround != null) {
            defaultForeGround.setBounds(0, 0, w, h);
            invalidate();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        if (defaultForeGround != null) {
            defaultForeGround.draw(canvas);
        }
    }
}
