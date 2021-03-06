package sckdn.lisa.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import java.util.List;

import ceui.lisa.download.DownloadHolder;
import sckdn.lisa.R;
import sckdn.lisa.core.DownloadItem;
import sckdn.lisa.core.Manager;
import sckdn.lisa.databinding.RecyDownloadTaskBinding;
import sckdn.lisa.download.FileSizeUtil;
import sckdn.lisa.interfaces.Callback;
import sckdn.lisa.utils.GlideUtil;
import rxhttp.wrapper.entity.Progress;

//正在下载
public class DownloadingAdapter extends BaseAdapter<DownloadItem, RecyDownloadTaskBinding> {

    @Override
    public ViewHolder<RecyDownloadTaskBinding> getNormalItem(ViewGroup parent) {
        return new DownloadHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(mContext),
                        mLayoutID,
                        parent,
                        false
                )
        );
    }

    public DownloadingAdapter(List<DownloadItem> targetList, Context context) {
        super(targetList, context);
    }

    @Override
    public void initLayout() {
        mLayoutID = R.layout.recy_download_task;
    }

    @Override
    public void bindData(DownloadItem target, ViewHolder<RecyDownloadTaskBinding> bindView, int position) {
        bindView.baseBind.taskName.setText(target.getName());
        bindView.baseBind.progress.setTag(target.getUuid());
        if (!TextUtils.isEmpty(target.getShowUrl())) {
            Glide.with(mContext)
                    .load(GlideUtil.getUrl(target.getShowUrl()))
                    .into(bindView.baseBind.illustImage);
        }
        if (position == 0) {
            bindView.baseBind.progress.setProgress(Manager.get().getCurrentProgress());
            Manager.get().setCallback(new Callback<Progress>() {
                @Override
                public void doSomething(Progress t) {
                    if (Manager.get().getUuid().equals(bindView.baseBind.progress.getTag())) {
                        bindView.baseBind.progress.setProgress(t.getProgress());
                        bindView.baseBind.state.setText("正在下载");
                        bindView.baseBind.currentSize.setText(String.format("%s / %s",
                                FileSizeUtil.formatFileSize(t.getCurrentSize()),
                                FileSizeUtil.formatFileSize(t.getTotalSize())));
                    } else {
                        bindView.baseBind.progress.setProgress(0);
                        if (target.isProcessed()) {
                            bindView.baseBind.state.setText("已失败");
                        } else {
                            bindView.baseBind.state.setText("未开始");
                        }
                        bindView.baseBind.currentSize.setText(mContext.getString(R.string.string_115));
                    }
                }
            });
        } else {
            bindView.baseBind.progress.setProgress(0);
            bindView.baseBind.currentSize.setText(mContext.getString(R.string.string_115));
        }
        if (target.isProcessed()) {
            bindView.baseBind.state.setText("已失败");
        } else {
            bindView.baseBind.state.setText("未开始");
        }
        bindView.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manager.get().start(mContext);
            }
        });
    }
}
