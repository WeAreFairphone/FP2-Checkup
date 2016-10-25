package com.fairphone.checkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.fairphone.checkup.Module.BatteryModule;
import com.fairphone.checkup.Module.CameraModule;
import com.fairphone.checkup.Module.DisplayModule;
import com.fairphone.checkup.Module.Module;
import com.fairphone.checkup.Module.ModuleListAdapter;
import com.fairphone.checkup.Module.ReceiverModule;
import com.fairphone.checkup.Module.SpeakerModule;
import com.fairphone.checkup.Module.TransceiverModule;

import java.util.ArrayList;

public class ModuleChooser extends AppCompatActivity implements ModuleListAdapter.OnClickListener {
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_chooser);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mListView = (ListView) findViewById(R.id.listView);
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
