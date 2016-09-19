package com.fairphone.diagnostics.tests;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fairphone.diagnostics.R;

import java.util.List;

/**
 * Created by dirk on 16-10-15.
 */
public class TestListAdapter extends ArrayAdapter<Test> {
    public interface OnClickListener {
        void onClick(Test test);
    }

    OnClickListener mOnClickListener;

    public TestListAdapter(Context context, List<Test> tests, OnClickListener onClickListener) {
        super(context,
                R.layout.test_chooser_list_item, R.id.listItemTestTitle, tests);
        mOnClickListener = onClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        final Test test = getItem(position);
        if (test != null) {
            ((TextView) view.findViewById(R.id.listItemTestTitle)).setText(getContext().getString(test.getTestTitleID()));
            ((TextView) view.findViewById(R.id.listItemTestDescription)).setText(getContext().getString(test.getTestDescriptionID()));
            TextView textview = (TextView) view.findViewById(R.id.test_list_test_result);
            if (test.hasRun()) {
                textview.setVisibility(View.VISIBLE);
                if (test.didPass()) {
                    textview.setText(getContext().getString(R.string.test_passed));
                    textview.setTextColor(0xff00aa00);
                } else {
                    textview.setText(getContext().getString(R.string.test_failed));
                    textview.setTextColor(0xffaa0000);
                }
            } else {
                textview.setVisibility(View.INVISIBLE);
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClick(test);
            }
        });

        return view;
    }


}
