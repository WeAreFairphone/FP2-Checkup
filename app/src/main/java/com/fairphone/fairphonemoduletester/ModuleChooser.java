package com.fairphone.fairphonemoduletester;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.fairphone.fairphonemoduletester.Module.CameraModule;
import com.fairphone.fairphonemoduletester.Module.DisplayModule;
import com.fairphone.fairphonemoduletester.Module.Module;
import com.fairphone.fairphonemoduletester.Module.ModuleListAdapter;
import com.fairphone.fairphonemoduletester.Module.ReceiverModule;
import com.fairphone.fairphonemoduletester.Module.SpeakerModule;
import com.fairphone.fairphonemoduletester.Module.TransceiverModule;

import java.util.ArrayList;

public class ModuleChooser extends Activity implements ModuleListAdapter.OnClickListener {
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_chooser);
        super.onCreate(savedInstanceState);
        mListView = (ListView) findViewById(R.id.listView);
        ArrayList<Module> tests = new ArrayList<>();
        tests.add(new DisplayModule());
        tests.add(new TransceiverModule());
        tests.add(new CameraModule());
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
