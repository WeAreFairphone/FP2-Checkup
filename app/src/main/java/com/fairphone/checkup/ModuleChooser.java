package com.fairphone.checkup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.fairphone.checkup.modules.BatteryModule;
import com.fairphone.checkup.modules.CameraModule;
import com.fairphone.checkup.modules.DisplayModule;
import com.fairphone.checkup.modules.Module;
import com.fairphone.checkup.modules.ModuleListAdapter;
import com.fairphone.checkup.modules.ReceiverModule;
import com.fairphone.checkup.modules.SpeakerModule;
import com.fairphone.checkup.modules.TransceiverModule;

import java.util.ArrayList;

public class ModuleChooser extends Activity implements ModuleListAdapter.OnClickListener {
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_chooser);
        mListView = (ListView) findViewById(R.id.list_view);
        ArrayList<Module> tests = new ArrayList<>();
        tests.add(new DisplayModule());
        tests.add(new CameraModule());
        tests.add(new BatteryModule());
        tests.add(new TransceiverModule());
        tests.add(new ReceiverModule());
        tests.add(new SpeakerModule());
        mListView.setAdapter(new ModuleListAdapter(this, tests, this));
    }

    @Override
    public void onClick(Module test) {
        Intent intent = new Intent(this, TestChooser.class);
        intent.putExtra("Module", test);
        startActivity(intent);
    }
}
