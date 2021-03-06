package sckdn.lisa.adapters

import android.content.Context
import android.text.TextUtils
import com.bumptech.glide.Glide
import sckdn.lisa.R
import sckdn.lisa.databinding.RecyMangaSeriesBinding
import sckdn.lisa.model.MangaSeriesItem
import sckdn.lisa.utils.GlideUtil

class MangaSeriesAdapter(
    targetList: MutableList<MangaSeriesItem>,
    context: Context
) : BaseAdapter<MangaSeriesItem, RecyMangaSeriesBinding>(targetList, context) {

    override fun initLayout() {
        mLayoutID = R.layout.recy_manga_series
    }

    override fun bindData(
        target: MangaSeriesItem,
        bindView: ViewHolder<RecyMangaSeriesBinding>,
        position: Int
    ) {
        bindView.baseBind.seriesTitle.text = "#%s".format(target.title)
        bindView.baseBind.seriesSize.text = "共%d话".format(target.series_work_count)
        if (!TextUtils.isEmpty(target.cover_image_urls.medium)) {
            Glide.with(mContext)
                .load(GlideUtil.getUrl(target.cover_image_urls.medium))
                .into(bindView.baseBind.imageView)
        }
        bindView.itemView.setOnClickListener {
            mOnItemClickListener.onItemClick(it, position, 0)
        }
    }
}
