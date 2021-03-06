package sckdn.lisa.fragments

import com.scwang.smartrefresh.layout.footer.FalsifyFooter
import com.scwang.smartrefresh.layout.header.FalsifyHeader
import sckdn.lisa.R
import sckdn.lisa.databinding.FragmentUserInfoBinding
import sckdn.lisa.interfaces.Display
import sckdn.lisa.model.UserDetailResponse
import sckdn.lisa.utils.Common
import sckdn.lisa.utils.Params

class FragmentUserInfo : BaseFragment<FragmentUserInfoBinding>(), Display<UserDetailResponse> {

    override fun initLayout() {
        mLayoutID = R.layout.fragment_user_info
    }

    public override fun initData() {
        baseBind.toolbar.setNavigationOnClickListener {
            mActivity.finish()
        }
        val user = mActivity.intent.getSerializableExtra(Params.CONTENT) as UserDetailResponse
        invoke(user)
    }

    override fun invoke(response: UserDetailResponse) {
        baseBind.mainPage.setHtml(Common.checkEmpty(response.profile.webpage))
        baseBind.twitter.setHtml(Common.checkEmpty(response.profile.twitter_url))
        baseBind.description.setHtml(Common.checkEmpty(response.user.comment))
        baseBind.pawoo.setHtml(Common.checkEmpty(response.profile.pawoo_url))
        baseBind.computer.text = Common.checkEmpty(response.workspace.pc)
        baseBind.monitor.text = Common.checkEmpty(response.workspace.monitor)
        baseBind.app.text = Common.checkEmpty(response.workspace.tool)
        baseBind.scanner.text = Common.checkEmpty(response.workspace.scanner)
        baseBind.drawBoard.text = Common.checkEmpty(response.workspace.tablet)
        baseBind.mouse.text = Common.checkEmpty(response.workspace.mouse)
        baseBind.printer.text = Common.checkEmpty(response.workspace.printer)
        baseBind.tableObjects.text = Common.checkEmpty(response.workspace.desktop)
        baseBind.likeMusic.text = Common.checkEmpty(response.workspace.music)
        baseBind.table.text = Common.checkEmpty(response.workspace.desk)
        baseBind.chair.text = Common.checkEmpty(response.workspace.chair)
        baseBind.otherText.text = Common.checkEmpty(response.workspace.comment)
    }

    override fun initView() {
        baseBind.refreshLayout.setRefreshHeader(FalsifyHeader(mContext))
        baseBind.refreshLayout.setRefreshFooter(FalsifyFooter(mContext))
    }
}
