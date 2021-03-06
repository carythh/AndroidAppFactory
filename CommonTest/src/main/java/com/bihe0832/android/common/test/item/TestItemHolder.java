package com.bihe0832.android.common.test.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bihe0832.android.common.test.R;
import com.bihe0832.android.lib.adapter.CardBaseHolder;
import com.bihe0832.android.lib.adapter.CardBaseModule;
import com.bihe0832.android.lib.text.TextFactoryUtils;

/**
 * @author hardyshi code@bihe0832.com
 *         Created on 2019-11-21.
 *         Description: Description
 */

public class TestItemHolder extends CardBaseHolder {

    public TextView mHeader;

    public TestItemHolder(View itemView, Context context) {
        super(itemView, context);
    }

    @Override
    public void initView() {
        mHeader = getView(R.id.test_title);
    }

    @Override
    public void initData(CardBaseModule item) {
        TestItemData data = (TestItemData) item;
        mHeader.setText(TextFactoryUtils.getSpannedTextByHtml(data.mContentText));
        if (null != data.mListener) {
            itemView.setOnClickListener(data.mListener);
        }
    }
}
