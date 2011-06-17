package sw805a.cardgame.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CheckBox;

public class CheckBoxListItem extends CheckBox {

	public CheckBoxListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheckBoxListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckBoxListItem(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
}
