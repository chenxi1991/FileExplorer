package com.guaner.fileexplorer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.dragselectrecyclerview.DragSelectRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {
    public final String TAG = getClass().getName();
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.cancel_selected)
    TextView cancelSelected;
    @BindView(R.id.utils)
    LinearLayout utils;
    @BindView(R.id.delete)
    LinearLayout delete;
    @BindView(R.id.rename)
    LinearLayout rename;
    @BindView(R.id.close)
    ImageView close;

    private Animation right_in;
    private Animation right_out;
    private Animation left_in;
    private Animation left_out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        right_in = AnimationUtils.loadAnimation(this, R.anim.flipper_right_in);
        right_out = AnimationUtils.loadAnimation(this, R.anim.flipper_right_out);
        left_in = AnimationUtils.loadAnimation(this, R.anim.flipper_left_in);
        left_out = AnimationUtils.loadAnimation(this, R.anim.flipper_left_out);
//        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 000);
        addView(true, Constants.ROOTPATH);
        disableDeleteAndRenameBtn();
    }

    /**
     * 初始化删除和重命名按钮不可点击
     */
    private void disableDeleteAndRenameBtn() {
        delete.setEnabled(false);
        delete.setClickable(false);
        rename.setClickable(false);
        rename.setEnabled(false);
    }

    /**
     * 返回上级文件列表，实际就是移除当前显示的子级文件夹文件列表
     */
    @OnClick(R.id.back)
    public void onBackClicked() {
        removeView();
    }


    /**
     * 取消当前列表（即container中最上层的一个recyclerView）的选择模式
     */
    @OnClick(R.id.cancel_selected)
    public void onCancelSelectClicked() {
        setSelectedModeEnabled(false);
        toggleRenameEnable(false);
        toggleDeleteEnable(false);

    }

    /**
     * 删除选中的文件
     */
    @OnClick(R.id.delete)
    public void onDeleteClicked() {
        getShowingListAdapter().deleteSelectedFile();
    }

    /**
     * 重命名选中文件
     */
    @OnClick(R.id.rename)
    public void onRenameClicked() {
        getShowingListAdapter().renameSelectedFile();
    }

    /**
     * 退出
     */
    @OnClick(R.id.close)
    public void onViewClicked() {
        finish();
    }

    /**
     * 添加列表
     *
     * @param isFirstPage
     * @param filePath
     */
    public void addView(boolean isFirstPage, String filePath) {
        ItemsAdapter adapter = new ItemsAdapter(this, filePath);
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setBackgroundColor(getResources().getColor(android.R.color.white));
        recyclerView.setLayoutParams(new DragSelectRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        container.addView(recyclerView);
        if (!isFirstPage) {
            container.getChildAt(container.getChildCount() - 1).startAnimation(left_in);
            container.getChildAt(container.getChildCount() - 2).startAnimation(left_out);
        }
        toggleBackButton(container);
    }

    /**
     * 移除列表
     */
    public void removeView() {
        if (container.getChildCount() > 1) {
            container.getChildAt(container.getChildCount() - 1).startAnimation(right_out);
            container.getChildAt(container.getChildCount() - 2).startAnimation(right_in);
            container.removeViewAt(container.getChildCount() - 1);
        }
        toggleBackButton(container);
    }

    /**
     * 控制底部工具的显示隐藏
     *
     * @param show
     */
    public void toggleUtils(boolean show) {
        if (show) {
            utils.setVisibility(View.VISIBLE);
            cancelSelected.setVisibility(View.VISIBLE);
            back.setVisibility(View.GONE);
        } else {
            utils.setVisibility(View.GONE);
            cancelSelected.setVisibility(View.GONE);
            toggleBackButton(container);
        }
    }

    /**
     * 控制返回按钮的显示隐藏
     *
     * @param container
     */
    private void toggleBackButton(RelativeLayout container) {
        if (container.getChildCount() > 1)
            back.setVisibility(View.VISIBLE);
        else
            back.setVisibility(View.GONE);
    }

    /**
     * 控制重命名按钮能否点击
     *
     * @param enable
     */
    public void toggleRenameEnable(boolean enable) {
        rename.setClickable(enable);
        rename.setEnabled(enable);
        if (enable) {
            for (int i = 0; i < rename.getChildCount(); i++) {
                rename.getChildAt(i).setAlpha(1.0f);
            }
        } else {
            for (int i = 0; i < rename.getChildCount(); i++) {
                rename.getChildAt(i).setAlpha(0.3f);
            }
        }
    }

    /**
     * 控制删除按钮按钮能否点击
     *
     * @param enable
     */
    public void toggleDeleteEnable(boolean enable) {
        delete.setClickable(enable);
        delete.setEnabled(enable);
        if (enable) {
            for (int i = 0; i < delete.getChildCount(); i++) {
                delete.getChildAt(i).setAlpha(1.0f);
            }
        } else {
            for (int i = 0; i < delete.getChildCount(); i++) {
                delete.getChildAt(i).setAlpha(0.3f);
            }
        }
    }

    /**
     * 打开或关闭当前显示的列表的选中模式
     *
     * @param enabled
     */
    private void setSelectedModeEnabled(boolean enabled) {
        getShowingListAdapter().setSelectedModeEnabled(enabled);
    }

    /**
     * 获取当前显示的列表的adapter
     *
     * @return
     */
    private ItemsAdapter getShowingListAdapter() {
        return ((ItemsAdapter) (((RecyclerView) (container.getChildAt(container.getChildCount() - 1))).getAdapter()));
    }

}
