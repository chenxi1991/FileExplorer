package com.guaner.fileexplorer;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenxi on 2017/6/21.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private MainActivity mActivity;
    private List<File> list;
    private File folderFile;
    private Boolean selectedModeEnabled = false;
    //已选中的文件
    private List<File> selectedItems = new ArrayList<>();

    public ItemsAdapter(Context context, String folderPath) {
        this.mActivity = (MainActivity) context;
        this.folderFile = new File(folderPath);
        this.list = Utils.getChildFiles(folderFile);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //根据当前是否是选择模式控制选择按钮的显示隐藏
        if (selectedModeEnabled) {
            holder.checkTag.setVisibility(View.VISIBLE);
            //防止recyclerView循环使用item导致的选中状态错乱
            if (selectedItems.contains(list.get(position))) {
                holder.checkTag.setChecked(true);
            } else {
                holder.checkTag.setChecked(false);
            }
        } else {
            holder.checkTag.setVisibility(View.GONE);
        }

        //根据File类型控制UI显示
        holder.itemName.setText(list.get(position).getName());
        if (list.get(position).isDirectory()) {
            //文件夹
            holder.itemImg.setImageResource(R.drawable.folder);
        } else if (list.get(position).getName().toUpperCase().endsWith(".IWB")) {
            //IWB文件
            holder.itemImg.setImageResource(R.drawable.iwb);
        } else if (list.get(position).getName().toUpperCase().endsWith(".UBM")) {
            //UBM文件
            holder.itemImg.setImageResource(R.drawable.ubm);
        } else {
            MediaFile.MediaFileType type = MediaFile.getFileType(list.get(position).getAbsolutePath());
            if (type != null) {
                if (MediaFile.isVideoFileType(type.fileType)) {
                    //视频
                    holder.itemImg.setImageBitmap(Utils.getVideoThumbnail(list.get(position).getAbsolutePath(), 128, 128, MediaStore.Images.Thumbnails.MICRO_KIND));
                } else if (MediaFile.isImageFileType(type.fileType)) {
                    //图片
                    holder.itemImg.setImageBitmap(Utils.getImageThumbnail(list.get(position).getAbsolutePath(), 128, 128));
                }
            } else {
                //其他文件类型
                holder.itemImg.setImageResource(R.drawable.other);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedModeEnabled) {
                    //非选择模式执行正常的点击事件
                    if (list.get(position).isDirectory()) {
                        //文件夹
                        mActivity.addView(false, list.get(position).getAbsolutePath());
                    } else if (list.get(position).getName().toUpperCase().endsWith(".IWB")) {
                        //IWB文件
                        //TODO
                    } else if (list.get(position).getName().toUpperCase().endsWith(".UBM")) {
                        //UBM文件
                        //TODO
                    } else {
                        MediaFile.MediaFileType type = MediaFile.getFileType(list.get(position).getAbsolutePath());
                        if (type != null) {
                            if (MediaFile.isVideoFileType(type.fileType)) {
                                //视频
                                //TODO
                            } else if (MediaFile.isImageFileType(type.fileType)) {
                                //图片
                                //TODO
                            }
                        } else {
                            //其他文件类型
                            Toast.makeText(mActivity, "对不起，不支持该文件类型", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    //选择模式时将点击事件分发给RatioBtn
                    holder.checkTag.performClick();
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //若已是选择模式则不再执行
                if (!selectedModeEnabled)
                    setSelectedModeEnabled(true);
                return false;
            }
        });
        holder.checkTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行选中和取消选中，包括刷新选中状态和已选列表的控制
                if (selectedItems.contains(list.get(position))) {
                    selectedItems.remove(list.get(position));
                    holder.checkTag.setChecked(false);
                } else {
                    selectedItems.add(list.get(position));
                    holder.checkTag.setChecked(true);
                }
                //根据已选中item的数量控制重命名按钮是否可点击
                mActivity.toggleRenameEnable((selectedItems.size() == 1));
                mActivity.toggleDeleteEnable(selectedItems.size() > 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_img)
        ImageView itemImg;
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.check_tag)
        RadioButton checkTag;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    /**
     * 重新加载当前文件夹的文件列表
     */
    public void reloadFromDisk() {
        list.clear();
        list.addAll(Utils.getChildFiles(folderFile));
        selectedItems.clear();
        selectedModeEnabled = false;
        mActivity.toggleUtils(false);
        mActivity.toggleDeleteEnable(false);
        mActivity.toggleRenameEnable(false);
        notifyDataSetChanged();
    }

    /**
     * 选择模式的开关方法
     *
     * @param enabled
     */
    public void setSelectedModeEnabled(boolean enabled) {
        mActivity.toggleUtils(enabled);
        selectedItems.clear();
        selectedModeEnabled = enabled;
        notifyDataSetChanged();
    }

    /**
     * 删除选中的文件
     */
    public void deleteSelectedFile() {
        final NormDialog deleteDialog = new NormDialog(mActivity) {
            @Override
            public int getLayoutID() {
                return R.layout.dialog_delete;
            }
        };
        deleteDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (File f : selectedItems) {
                    if (f.isDirectory()) {
                        Utils.deleteDir(f);
                    } else {
                        Utils.deleteFile(f);
                    }
                }
                reloadFromDisk();
                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();
    }

    public void renameSelectedFile() {
        final File selectedFile = selectedItems.get(0);
        final NormDialog renameDialog = new NormDialog(mActivity) {
            @Override
            public int getLayoutID() {
                return R.layout.dialog_rename;
            }
        };
        final EditText et = (EditText) renameDialog.findViewById(R.id.et);
        et.setText(selectedFile.getName());
        et.setSelection(selectedFile.getName().length());
        renameDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDialog.dismiss();
            }
        });
        renameDialog.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = Utils.renameFile(selectedFile, et.getText().toString());
                if (success) {
                    renameDialog.dismiss();
                    reloadFromDisk();
                }
            }
        });
        renameDialog.show();
    }

}
