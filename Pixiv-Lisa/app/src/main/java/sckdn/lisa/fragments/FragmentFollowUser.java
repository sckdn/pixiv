package sckdn.lisa.fragments;

import android.os.Bundle;

import sckdn.lisa.R;
import sckdn.lisa.adapters.BaseAdapter;
import sckdn.lisa.adapters.UAdapter;
import sckdn.lisa.repo.RemoteRepo;
import sckdn.lisa.databinding.FragmentBaseListBinding;
import sckdn.lisa.databinding.RecyUserPreviewBinding;
import sckdn.lisa.model.ListUser;
import sckdn.lisa.model.UserPreviewsBean;
import sckdn.lisa.repo.FollowUserRepo;
import sckdn.lisa.utils.Params;

public class FragmentFollowUser extends NetListFragment<FragmentBaseListBinding,
        ListUser, UserPreviewsBean> {

    private int userID;
    private String starType;
    private boolean showToolbar = false;

    public static FragmentFollowUser newInstance(int userID, String starType, boolean pShowToolbar) {
        Bundle args = new Bundle();
        args.putInt(Params.USER_ID, userID);
        args.putString(Params.STAR_TYPE, starType);
        args.putBoolean(Params.FLAG, pShowToolbar);
        FragmentFollowUser fragment = new FragmentFollowUser();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initBundle(Bundle bundle) {
        userID = bundle.getInt(Params.USER_ID);
        starType = bundle.getString(Params.STAR_TYPE);
        showToolbar = bundle.getBoolean(Params.FLAG);
    }

    @Override
    public RemoteRepo<ListUser> repository() {
        return new FollowUserRepo(userID, starType);
    }

    @Override
    public BaseAdapter<UserPreviewsBean, RecyUserPreviewBinding> adapter() {
        return new UAdapter(allItems, mContext);
    }

    @Override
    public boolean showToolbar() {
        return showToolbar;
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.string_232);
    }
}
