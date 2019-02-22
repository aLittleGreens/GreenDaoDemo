package base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import yu.cai.greendaodemo.R;


/**
 * Created by Bruce
 * on 2017/6/18.
 */

public abstract class BaseDialog extends AppCompatDialog {

    View rootView;

    public BaseDialog(Context context) {
        this(context, R.style.dialog_full_screen);
    }

    private BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        rootView = LayoutInflater.from(getContext()).inflate(getLayoutRes(), null, false);
        setContentView(rootView);
        initView(rootView);
        LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        getData();
    }

    protected void getData() {

    }

    protected abstract int getLayoutRes();

     public abstract void initView(View rootView);
}
