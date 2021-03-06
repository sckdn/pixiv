package sckdn.lisa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import sckdn.lisa.R;
import sckdn.lisa.databinding.ActivityFragmentBinding;
import sckdn.lisa.fragments.FragmentCollection;
import sckdn.lisa.fragments.FragmentColors;
import sckdn.lisa.fragments.FragmentComment;
import sckdn.lisa.fragments.FragmentDoing;
import sckdn.lisa.fragments.FragmentDownload;
import sckdn.lisa.fragments.FragmentEditAccount;
import sckdn.lisa.fragments.FragmentEditFile;
import sckdn.lisa.fragments.FragmentFollowUser;
import sckdn.lisa.fragments.FragmentHistory;
import sckdn.lisa.fragments.FragmentImageDetail;
import sckdn.lisa.fragments.FragmentLikeIllust;
import sckdn.lisa.fragments.FragmentListSimpleUser;
import sckdn.lisa.fragments.FragmentLocalUsers;
import sckdn.lisa.fragments.FragmentLogin;
import sckdn.lisa.fragments.FragmentMangaSeries;
import sckdn.lisa.fragments.FragmentMangaSeriesDetail;
import sckdn.lisa.fragments.FragmentRecmdIllust;
import sckdn.lisa.fragments.FragmentRecmdUser;
import sckdn.lisa.fragments.FragmentRelatedIllust;
import sckdn.lisa.fragments.FragmentSearch;
import sckdn.lisa.fragments.FragmentSearchUser;
import sckdn.lisa.fragments.FragmentSettings;
import sckdn.lisa.fragments.FragmentUserIllust;
import sckdn.lisa.fragments.FragmentUserInfo;
import sckdn.lisa.fragments.FragmentUserManga;
import sckdn.lisa.fragments.FragmentViewPager;
import sckdn.lisa.fragments.FragmentWalkThrough;
import sckdn.lisa.fragments.FragmentWebView;
import sckdn.lisa.fragments.FragmentWorkSpace;
import sckdn.lisa.helper.BackHandlerHelper;
import sckdn.lisa.model.IllustsBean;
import sckdn.lisa.utils.Params;

public class TemplateActivity extends BaseActivity<ActivityFragmentBinding> implements ColorPickerDialogListener {

    public static final String EXTRA_FRAGMENT = "dataType";
    public static final String EXTRA_KEYWORD = "keyword";
    protected Fragment childFragment;
    private String dataType;

    @Override
    protected void initBundle(Bundle bundle) {
        dataType = bundle.getString(EXTRA_FRAGMENT);
    }

    protected Fragment createNewFragment() {
        Intent intent = getIntent();
        if (!TextUtils.isEmpty(dataType)) {
            switch (dataType) {
                case "????????????":
                    return new FragmentLogin();
                case "????????????": {
                    int id = intent.getIntExtra(Params.ILLUST_ID, 0);
                    String title = intent.getStringExtra(Params.ILLUST_TITLE);
                    return FragmentRelatedIllust.newInstance(id, title);
                }
                case "????????????":
                    return new FragmentHistory();
                case "????????????": {
                    String url = intent.getStringExtra(Params.URL);
                    String title = intent.getStringExtra(Params.TITLE);
                    Boolean preferPreserve = intent.getBooleanExtra(Params.PREFER_PRESERVE, false);
                    return FragmentWebView.newInstance(title, url, preferPreserve);
                }
                case "??????":
                    return new FragmentSettings();
                case "????????????":
                    return new FragmentRecmdUser();
                case "????????????": {
                    String keyword = intent.getStringExtra(EXTRA_KEYWORD);
                    return FragmentSearchUser.newInstance(keyword);
                }
                case "????????????": {
                    int id = intent.getIntExtra(Params.ILLUST_ID, 0);
                    String title = intent.getStringExtra(Params.ILLUST_TITLE);
                    return FragmentComment.newInstance(id, title);
                }
                case "????????????":
                    return new FragmentLocalUsers();
                case "??????":
                    return new FragmentWalkThrough();
                case "????????????":
                    return FragmentFollowUser.newInstance(
                            getIntent().getIntExtra(Params.USER_ID, 0),
                            Params.TYPE_PUBLUC, true);
                case "??????":
                    return new FragmentSearch();
                case "????????????":
                    return new FragmentUserInfo();
                case "???????????????????????????":
                    return FragmentListSimpleUser.newInstance((IllustsBean) intent.getSerializableExtra(Params.CONTENT));
                case "????????????":
                    return FragmentUserIllust.newInstance(intent.getIntExtra(Params.USER_ID, 0),
                            true);
                case "????????????":
                    return FragmentUserManga.newInstance(intent.getIntExtra(Params.USER_ID, 0),
                            true);
                case "??????/????????????":
                    return FragmentLikeIllust.newInstance(intent.getIntExtra(Params.USER_ID, 0),
                            Params.TYPE_PUBLUC, true);
                case "????????????":
                    return new FragmentDownload();
                case "????????????":
                    return FragmentRecmdIllust.newInstance("??????");
                case "????????????":
                    return FragmentImageDetail.newInstance(intent.getStringExtra(Params.URL));
                case "????????????":
                    return new FragmentEditAccount();
                case "??????????????????":
                    return new FragmentEditFile();
                case "??????????????????":
                    return FragmentViewPager.newInstance(Params.VIEW_PAGER_MUTED);
                case "??????????????????":
                    return FragmentMangaSeries.newInstance(intent.getIntExtra(Params.USER_ID, 0));
                case "??????????????????":
                    return FragmentMangaSeriesDetail.newInstance(intent.getIntExtra(Params.ID, 0));
                case "??????????????????":
                    return new FragmentWorkSpace();
                case "????????????":
                    return new FragmentDoing();
                case "??????????????????":
                    return FragmentCollection.newInstance(0);
                case "????????????":
                    return FragmentCollection.newInstance(2);
                case "????????????":
                    return new FragmentColors();
                default:
                    return new Fragment();
            }
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (childFragment instanceof FragmentWebView) {
            return ((FragmentWebView) childFragment).getAgentWeb().handleKeyEvent(keyCode, event) ||
                    super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createNewFragment();
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
                childFragment = fragment;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (childFragment != null) {
            childFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean hideStatusBar() {
        if ("????????????".equals(dataType)) {
            return false;
        } else {
            return getIntent().getBooleanExtra("hideStatusBar", true);
        }
    }

    @Override
    public void onColorSelected(int dialogId, int color) {

    }


    @Override
    public void onDialogDismissed(int dialogId) {

    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }
}
