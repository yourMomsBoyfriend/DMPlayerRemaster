package com.dmplayer.externalaccount;

import android.view.View;

public interface ExternalAccountViewInternalCallbacks {
        View.OnClickListener onLogInListener();
        View.OnClickListener onRefreshListener();
        View.OnClickListener onLogOutListener();
}