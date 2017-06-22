package com.guaner.fileexplorer;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * @author sineom
 * @version 1.0
 * @time 2016/7/15 13:30
 * @updateAuthor ${Author}
 * @updataTIme 2016/7/15
 * @updataDes ${描述更新内容}
 */
public abstract class NormDialog extends Dialog {

    private View mContanView;

    public NormDialog(Context context) {
        super(context, R.style.self_dialog);
        setContentView(initView(context));

    }

    private View initView(Context ctx) {
        mContanView = View.inflate(ctx, getLayoutID(), null);
        return mContanView;
    }

    public abstract int getLayoutID();

    public View getContanView() {
        return mContanView;
    }

    public View getViewById(int viewId) {

        return mContanView.findViewById(viewId);
    }

    public void setViewOnClick(int viewId, View.OnClickListener onClickListener) {
        mContanView.findViewById(viewId).setOnClickListener(onClickListener);
    }


}
