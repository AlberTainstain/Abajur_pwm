package com.example.javabluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class ListaDispositivos extends ListActivity {


    private BluetoothAdapter adpBT2 = null;

    static String AdressM = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

                adpBT2 = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> dispositivospareados = adpBT2.getBondedDevices();

        if(dispositivospareados.size() > 0){
            for(BluetoothDevice dispositivo : dispositivospareados){
                String nomeBt = dispositivo.getName();
                String macBt = dispositivo.getAddress();
                ArrayBluetooth.add(nomeBt + "\n" + macBt);
            }
            setListAdapter(ArrayBluetooth);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String GeralInfo = ((TextView) v).getText().toString();

       // Toast.makeText(getApplicationContext(), "Info():" + GeralInfo, Toast.LENGTH_LONG).show();

        String MacAdress = GeralInfo.substring(GeralInfo.length() - 17);

        // Toast.makeText(getApplicationContext(), "Info():" + MacAdress, Toast.LENGTH_LONG).show();

        Intent ReturnMac = new Intent();
        ReturnMac.putExtra(AdressM, MacAdress);
        setResult(RESULT_OK, ReturnMac);
        finish();
    }
}
