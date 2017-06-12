package cn.sparta1029.sayi.components;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
public class TextViewWithImage extends TextView {
	public TextViewWithImage(Context context) {
		this(context, null);
	}
	public TextViewWithImage(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public TextViewWithImage(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// getCompoundDrawables() : Returns drawables for the left, top, right,
		// and bottom borders.
		Drawable[] drawables = getCompoundDrawables();
		// 得到drawableLeft设置的drawable对象
		Drawable leftDrawable = drawables[0];
		if (leftDrawable != null) {
			// 得到leftDrawable的宽度
			int leftDrawableWidth = leftDrawable.getIntrinsicWidth();
			// 得到drawable与text之间的间距
			int drawablePadding = getCompoundDrawablePadding();
			// 得到文本的宽度
			int textWidth = (int) getPaint().measureText(
					getText().toString().trim());
			int bodyWidth = leftDrawableWidth + drawablePadding + textWidth;
			canvas.save();
			canvas.translate((getWidth() - bodyWidth) / 2, 0);
		}
		super.onDraw(canvas);
	}
}
