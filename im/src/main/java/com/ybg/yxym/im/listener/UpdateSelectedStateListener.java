package com.ybg.yxym.im.listener;

import com.ybg.yxym.im.entity.FileType;

public interface UpdateSelectedStateListener {

    void onSelected(String path, long fileSize, FileType type);

    void onUnselected(String path, long fileSize, FileType type);

}
