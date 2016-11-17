package com.fairphone.checkup.tests;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fairphone.checkup.R;

import java.util.List;

public class TestListAdapter extends ArrayAdapter<Test.Details> {
    public interface OnClickListener {
        void onClick(Test.Details item);
    }

    OnClickListener mOnClickListener;

    public TestListAdapter(Context context, List<Test.Details> tests, OnClickListener onClickListener) {
        super(context,
                R.layout.list_item_test, R.id.list_item_title, tests);
        mOnClickListener = onClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        final Test.Details item = getItem(position);
        if (item != null) {
            ((TextView) view.findViewById(R.id.list_item_title)).setText(item.getTitle(getContext()));
            ((TextView) view.findViewById(R.id.list_item_summary)).setText(item.getSummary(getContext()));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClick(item);
            }
        });

        return view;
    }
}
