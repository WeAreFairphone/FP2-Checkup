package com.fairphone.diagnostics.Module;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fairphone.diagnostics.R;

import java.util.List;

/**
 * Created by dirk on 16-10-15.
 */
public class ModuleListAdapter extends ArrayAdapter<Module> {

    public interface OnClickListener {
        void onClick(Module test);
    }

    OnClickListener mOnClickListener;

    public ModuleListAdapter(Context context, List<Module> tests, OnClickListener onClickListener) {
        super(context,
                R.layout.module_chooser_list_item, R.id.moduleName, tests);
        mOnClickListener = onClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        final Module module = getItem(position);
        if (module != null) {
            ((ImageView) view.findViewById(R.id.modulePicture)).setImageResource(module.getPictureResourceID());
            ((TextView) view.findViewById(R.id.moduleName)).setText(getContext().getString(module.getModuleNameID()));
            ((TextView) view.findViewById(R.id.moduleDescription)).setText(getContext().getString(module.getDescriptionId()));
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClick(module);
            }
        });

        return view;
    }


}
