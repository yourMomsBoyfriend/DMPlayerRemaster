package com.dmplayer.uicomponent.SwappingLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.dmplayer.R;
import com.dmplayer.models.ExternalProfileObject;

public class ExternalProfileLayout extends LinearLayout implements SwappingLayoutController {

    private ChildForSwapping startingLayout;
    private ProfileViewChild swappingLayout;
    private ChildForSwapping loadingLayout;

    private boolean isSwapped = false;

    public ExternalProfileLayout(Context context) {
        super(context);
    }

    public ExternalProfileLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExternalProfileLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExternalProfileLayout,
                0, 0);
        int startingLayoutRes;
        int swappingLayoutRes;
        int loadingLayoutRes;
        try {
            startingLayoutRes = a.getResourceId(R.styleable.ExternalProfileLayout_starting_layout, -1);
            swappingLayoutRes = a.getResourceId(R.styleable.ExternalProfileLayout_swapping_layout, -1);
            loadingLayoutRes = a.getResourceId(R.styleable.ExternalProfileLayout_loading_layout, -1);
        } finally {
            a.recycle();
        }

        startingLayout = new ChildForSwapping(getContext(), startingLayoutRes);
        loadingLayout = new ChildForSwapping(getContext(), loadingLayoutRes);
        swappingLayout = new ProfileViewChild(getContext(), swappingLayoutRes);

        if (!isSwapped)
            setFirstLayout();
        else
            setSecondLayout();
    }

    public void setSecondLayout() {
        removeAllViews();
        isSwapped = true;
        addView(swappingLayout);
    }

    public void setFirstLayout() {
        removeAllViews();
        isSwapped = false;
        addView(startingLayout);
    }

    private void setLoadingLayout() {
        removeAllViews();
        isSwapped = !isSwapped;
        addView(loadingLayout);
    }

    @Override
    public void onLogInStarted() {
        setLoadingLayout();
    }

    @Override
    public void onLogInFinished(ExternalProfileObject profile) {
        setSecondLayout();
        swappingLayout.setProfile(profile);
    }

    @Override
    public void onLoggedOut() {
        setFirstLayout();
    }
}