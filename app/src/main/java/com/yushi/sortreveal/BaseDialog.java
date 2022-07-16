package com.yushi.sortreveal;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;


public abstract class BaseDialog extends Dialog  {
    protected Context context;


    public BaseDialog(Context context) {
        super(context);
        this.context = context;
        setContentView(getLayoutRes());
        initDialog();
    }

    protected abstract int getLayoutRes();


    protected void installContent() {
        initView();
        initStyle();
        initListen();
    }


    protected abstract void initListen();

    protected abstract void initStyle();

    protected abstract void initView();

    protected abstract int getDialogWidth();

    protected abstract int getDialogHeight();

    protected abstract int getGravity();





    private void initDialog() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = getDialogWidth() == 0 ? WindowManager.LayoutParams.WRAP_CONTENT : getDialogWidth();
            lp.height = getDialogHeight() == 0 ? WindowManager.LayoutParams.WRAP_CONTENT : getDialogHeight();
            lp.dimAmount = 0.55f;
            window.setGravity(getGravity() == 0 ? Gravity.CENTER : getGravity());
            window.setAttributes(lp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

}
