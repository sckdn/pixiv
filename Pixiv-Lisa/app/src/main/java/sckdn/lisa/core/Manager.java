package sckdn.lisa.core;


import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import sckdn.lisa.activities.Lisa;
import sckdn.lisa.database.AppDatabase;
import sckdn.lisa.database.DownloadEntity;
import sckdn.lisa.database.DownloadingEntity;
import sckdn.lisa.helper.Android10DownloadFactory22;
import sckdn.lisa.interfaces.Callback;
import sckdn.lisa.model.Holder;
import sckdn.lisa.utils.Common;
import sckdn.lisa.utils.Local;
import sckdn.lisa.utils.Params;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import rxhttp.RxHttp;
import rxhttp.wrapper.callback.UriFactory;
import rxhttp.wrapper.entity.Progress;

public class Manager {

    private List<DownloadItem> content = new ArrayList<>();
    private Disposable handle = null;
    private long nonius;

    private boolean isRunning = false;

    private Manager() {
        currentIllustID = 0;
        nonius = 0L;
    }

    public void restore(Context context) {
        List<DownloadingEntity> downloadingEntities = AppDatabase.getAppDatabase(context).downloadDao().getAllDownloading();
        if (!Common.isEmpty(downloadingEntities)) {
            Common.showLog("downloadingEntities " + downloadingEntities.size());
            if (content != null) {
                content = new ArrayList<>();
            }
            for (DownloadingEntity entity : downloadingEntities) {
                DownloadItem downloadItem = Lisa.sGson.fromJson(entity.getTaskGson(), DownloadItem.class);
                content.add(downloadItem);
            }
            Common.showToast("下载记录恢复成功");
        }
    }

    public static Manager get() {
        return Manager.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final Manager INSTANCE = new Manager();
    }

    public void addTask(DownloadItem bean, Context context) {
        synchronized (this) {
            if (content == null) {
                content = new ArrayList<>();
            }
            boolean isTaskExist = false;
            for (DownloadItem item : content) {
                if (item.isSame(bean)) {
                    isTaskExist = true;
                }
            }
            if (!isTaskExist) {
                safeAdd(bean);
            }
            start(context);
        }
    }

    private void safeAdd(DownloadItem item) {
        Common.showLog("Manager safeAdd " + item.getUuid());
        content.add(item);
        DownloadingEntity entity = new DownloadingEntity();
        entity.setFileName(item.getName());
        entity.setUuid(item.getUuid());
        entity.setTaskGson(Lisa.sGson.toJson(item));
        AppDatabase.getAppDatabase(Lisa.getContext()).downloadDao().insertDownloading(entity);
    }

    private void safeDelete(DownloadItem item) {
        safeDelete(item, true);
    }

    private void safeDelete(DownloadItem item, boolean isDownloadSuccess) {
        content.remove(item);
        if (isDownloadSuccess) {
            DownloadingEntity entity = new DownloadingEntity();
            entity.setFileName(item.getName());
            entity.setUuid(item.getUuid());
            entity.setTaskGson(Lisa.sGson.toJson(item));
            AppDatabase.getAppDatabase(Lisa.getContext()).downloadDao().deleteDownloading(entity);
            Common.showToast(item.getName() + "已下载完成");
        } else {
            item.setProcessed(true);
            //加到队列尾部
            content.add(item);
        }
    }

    public void addTasks(List<DownloadItem> list, Context context) {
        if (!Common.isEmpty(list)) {
            for (DownloadItem item : list) {
                addTask(item, context);
            }
        }
    }

    public void start(Context context) {
        if (!Common.isEmpty(content)) {
            for (DownloadItem item : content) {
                item.setProcessed(false);
            }
        }
        if (isRunning) {
            Common.showLog("Manager 正在下载中，不用多次start");
            return;
        }
        loop(context);
    }

    private void loop(Context context) {
        if (Common.isEmpty(content)) {
            isRunning = false;
            Common.showLog("Manager 已经全部下载完成");
            return;
        }
        isRunning = true;
        DownloadItem item = getFirstOne();
        if (item != null) {
            downloadOne(context, item);
        } else {
            stop();
        }
    }

    private DownloadItem getFirstOne() {
        for (int i = 0; i < content.size(); i++) {
            if (!content.get(i).isProcessed()) {
                return content.get(i);
            }
        }
        return null;
    }

    private void downloadOne(Context context, DownloadItem bean) {
        UriFactory factory = null;
        if (Lisa.sSettings.getDownloadWay() == 0 ) {
            factory = new Android10DownloadFactory22(context, bean);
        }
        currentIllustID = bean.getIllust().getId();
        Common.showLog("Manager 下载单个 当前进度" + nonius);
        uuid = bean.getUuid();

        UriFactory finalFactory = factory;
        handle = RxHttp.get(bean.getUrl())
                .addHeader(Params.MAP_KEY, Params.IMAGE_REFERER)
                .setRangeHeader(nonius, true)
                .asDownload(factory, AndroidSchedulers.mainThread(), new Consumer<Progress>() {
                    @Override
                    public void accept(Progress progress) {
                        nonius = progress.getCurrentSize();
                        currentProgress = progress.getProgress();
                        Common.showLog("currentProgress " + currentProgress);
                        try {
                            if (mCallback != null) {
                                mCallback.doSomething(progress);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }) //指定主线程回调
                .doFinally(new Action() {
                    @Override
                    public void run() throws Throwable {
                        //下载完成，处理相关逻辑
                        currentIllustID = 0;
                        currentProgress = 0;
                        nonius = 0L;
                        loop(context);
                        Common.showLog("doFinally ");
                    }
                })
                .subscribe(s -> {//s为String类型，这里为文件存储路径
                    Common.showLog("downloadOne " + s);

                    //通知 DOWNLOAD_ING 下载完成
                    {
                        Intent intent = new Intent(Params.DOWNLOAD_ING);
                        Holder holder = new Holder();
                        holder.setCode(Params.DOWNLOAD_SUCCESS);
                        holder.setIndex(0);
                        holder.setDownloadItem(bean);
                        intent.putExtra(Params.CONTENT, holder);
                        LocalBroadcastManager.getInstance(Lisa.getContext()).sendBroadcast(intent);
                    }

                    //通知 DOWNLOAD_FINISH 下载完成
                    {
                        DownloadEntity downloadEntity = new DownloadEntity();
                        downloadEntity.setIllustGson(Lisa.sGson.toJson(bean.getIllust()));
                        downloadEntity.setFileName(bean.getName());
                        downloadEntity.setDownloadTime(System.currentTimeMillis());
                       {
                            downloadEntity.setFilePath(((Android10DownloadFactory22) finalFactory).getFileUri().toString());
                        }
                        AppDatabase.getAppDatabase(Lisa.getContext()).downloadDao().insert(downloadEntity);
                        //通知FragmentDownloadFinish 添加这一项
                        Intent intent = new Intent(Params.DOWNLOAD_FINISH);
                        intent.putExtra(Params.CONTENT, downloadEntity);
                        LocalBroadcastManager.getInstance(Lisa.getContext()).sendBroadcast(intent);
                    }
                    safeDelete(bean);
                }, throwable -> {
                    //下载失败，处理相关逻辑
                    throwable.printStackTrace();
                    Common.showLog("下载失败，原因：" + throwable.toString());
                    safeDelete(bean, false);
                    {
                        //通知 DOWNLOAD_ING 有一项下载失败
                        Intent intent = new Intent(Params.DOWNLOAD_ING);
                        Holder holder = new Holder();
                        holder.setCode(Params.DOWNLOAD_FAILED);
                        holder.setIndex(0);
                        holder.setDownloadItem(bean);
                        intent.putExtra(Params.CONTENT, holder);
                        LocalBroadcastManager.getInstance(Lisa.getContext()).sendBroadcast(intent);
                    }
                });
    }

    private int currentProgress;

    public int getCurrentProgress() {
        return currentProgress;
    }

    private String uuid;
    private int currentIllustID;

    public int getCurrentIllustID() {
        return currentIllustID;
    }

    public String getUuid() {
        return uuid;
    }

    private Callback<Progress> mCallback;

    public Callback<Progress> getCallback() {
        return mCallback;
    }

    public void setCallback(Callback<Progress> callback) {
        mCallback = callback;
    }

    public List<DownloadItem> getContent() {
        return content;
    }

    public void stop() {
        isRunning = false;
        if (handle != null) {
            handle.dispose();
        }
        Common.showLog("已经停止");
        Lisa.sSettings.setCurrentProgress(nonius);
        Local.setSettings(Lisa.sSettings);
    }

    public void clear() {
        stop();
        AppDatabase.getAppDatabase(Lisa.getContext()).downloadDao().deleteAllDownloading();
        content.clear();
    }
}
