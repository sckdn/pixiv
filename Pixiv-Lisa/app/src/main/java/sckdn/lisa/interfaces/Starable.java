package sckdn.lisa.interfaces;

/**
 * ε―ζΆθη
 */
public interface Starable {

    int getItemID();

    void setItemID(int id);

    boolean isItemStared();

    void setItemStared(boolean isLiked);
}
