package com.example.javabluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button LampButton, OnOffButton, btnConnect;
    SeekBar PWMseekBar;

    Boolean connection = false;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONEXAO = 2;

    ConnectedThread connectedThread;

    BluetoothAdapter adpBT = null;
    BluetoothDevice BTdevice = null;
    BluetoothSocket BTSocket = null;

    UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static String MAC = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LampButton = (Button)findViewById(R.id.LampButton);
        btnConnect = (Button)findViewById(R.id.btnConnect);

        PWMseekBar = (SeekBar)findViewById(R.id.seekBar);



        adpBT = BluetoothAdapter.getDefaultAdapter();

        if(adpBT == null){
            Toast.makeText(getApplicationContext(),"Dispositívo sem Bluetooth!", Toast.LENGTH_LONG).show();
        } else if(!adpBT.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connection == true){
                    //desconectar
                    try{
                        BTSocket.close();
                        connection = false;
                        btnConnect.setText("Connect");

                        Toast.makeText(getApplicationContext(), "Bluetooth desconectado.", Toast.LENGTH_LONG).show();
                    } catch (IOException erro){
                        Toast.makeText(getApplicationContext(), "DEU RUIM: " + erro, Toast.LENGTH_LONG).show();
                    }
                }else{
                    //conectar
                Intent AbreLista = new Intent(MainActivity.this, ListaDispositivos.class);
                startActivityForResult(AbreLista, REQUEST_CONEXAO);
                }
            }
        });

        LampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connection){
                    connectedThread.write("a1");

                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não conectado.", Toast.LENGTH_LONG).show();
                }

            }
        });

        PWMseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(connection){
                    connectedThread.write("pwm" + seekBar.getProgress());

                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não conectado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth ON", Toast.LENGTH_LONG).show();
                }
                 else{
                    Toast.makeText(getApplicationContext(), "Bluetooth not ON", Toast.LENGTH_LONG).show();
                }
            case REQUEST_CONEXAO:
                if(resultCode == Activity.RESULT_OK){
                    MAC = data.getExtras().getString(ListaDispositivos.AdressM);

                    //Toast.makeText(getApplicationContext(), "CONECTOU: " + MAC, Toast.LENGTH_LONG).show();

                    BTdevice = adpBT.getRemoteDevice(MAC);

                    try {
                        BTSocket = BTdevice.createRfcommSocketToServiceRecord(BT_UUID);

                        BTSocket.connect();

                        connection = true;

                        connectedThread = new ConnectedThread(BTSocket);
                        connectedThread.start();

                        btnConnect.setText("Disconnect");

                        Toast.makeText(getApplicationContext(), "CONECTOU: " + MAC, Toast.LENGTH_LONG).show();
                    } catch (IOException erro) {

                        connection = false;
                        Toast.makeText(getApplicationContext(), "DEU RUIM: " + erro, Toast.LENGTH_LONG).show(); //RETIRAR EM VERSAO FINAL
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "DEU RUIM, NAO CONECTOU!1!", Toast.LENGTH_LONG).show();
                }
             break;
                            }
    }

    private class ConnectedThread extends Thread {
       // private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
         //   mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            /* while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }*/
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String dadosenviar) {
            byte[] msgBuffer = dadosenviar.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }

    }
}
