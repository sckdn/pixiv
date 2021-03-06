package sckdn.lisa.repo;

import android.content.Context;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import sckdn.lisa.view.MyDeliveryHeader;

public abstract class LocalRepo<T> extends BaseRepo {

    public abstract T first();

    public abstract T next();

    @Override
    public boolean hasNext() {
        return false;
    }

    public boolean enableRefresh() {
        return true;
    }

    @Override
    public RefreshHeader getHeader(Context context) {
        return new MyDeliveryHeader(context);
    }
}
