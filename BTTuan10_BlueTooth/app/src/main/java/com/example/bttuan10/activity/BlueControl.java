package com.example.bttuan10.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.bttuan10.R;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BlueControl extends AppCompatActivity {
    ImageButton btnTb1, btnTb2, btnDis;
    TextView txt1, txtMAC;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    Set<BluetoothDevice> pairedDevices1;
    String address = null;
    private ProgressDialog progress;
    int flaglamp1 = 0;
    int flaglamp2 = 0;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);
        setContentView(R.layout.activity_control);

        btnTb1 = findViewById(R.id.btnTb1);
        btnTb2 = findViewById(R.id.btnTb2);
        txt1 = findViewById(R.id.textV1);
        txtMAC = findViewById(R.id.textViewMAC);
        btnDis = findViewById(R.id.btnDisc);

        new ConnectBT().execute();

        btnTb1.setOnClickListener(v -> thietTb1());
        btnTb2.setOnClickListener(v -> thiettb2());
        btnDis.setOnClickListener(v -> Disconnect());
    }

    private void thietTb1() {
        if (btSocket != null) {
            try {
                if (flaglamp1 == 0) {
                    flaglamp1 = 1;
                    btnTb1.setBackgroundResource(R.drawable.tblon);
                    btSocket.getOutputStream().write("1".getBytes());
                    txt1.setText("Thiết bị số 1 đang bật");
                } else {
                    flaglamp1 = 0;
                    btnTb1.setBackgroundResource(R.drawable.tbloff);
                    btSocket.getOutputStream().write("A".getBytes());
                    txt1.setText("Thiết bị số 1 đang tắt");
                }
            } catch (IOException e) {
                msg("Lỗi khi gửi dữ liệu");
            }
        }
    }

    private void thiettb2() {
        if (btSocket != null) {
            try {
                if (flaglamp2 == 0) {
                    flaglamp2 = 1;
                    btnTb2.setBackgroundResource(R.drawable.tblon);
                    btSocket.getOutputStream().write("7".getBytes());
                    txt1.setText("Thiết bị số 2 đang bật");
                } else {
                    flaglamp2 = 0;
                    btnTb2.setBackgroundResource(R.drawable.tbloff);
                    btSocket.getOutputStream().write("G".getBytes());
                    txt1.setText("Thiết bị số 2 đang tắt");
                }
            } catch (IOException e) {
                msg("Lỗi khi gửi dữ liệu");
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(BlueControl.this, "Đang kết nối...", "Xin vui lòng đợi!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);

                    if (ActivityCompat.checkSelfPermission(BlueControl.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(BlueControl.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                        return null;
                    }

                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Kết nối thất bại! Kiểm tra thiết bị.");
                finish();
            } else {
                msg("Kết nối thành công.");
                isBtConnected = true;
                pairedDevicesList1();
            }
            progress.dismiss();
        }
    }

    private void pairedDevicesList1() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            return;
        }

        pairedDevices1 = myBluetooth.getBondedDevices();
        if (pairedDevices1.size() > 0) {
            for (BluetoothDevice bt : pairedDevices1) {
                txtMAC.setText(bt.getName() + " - " + bt.getAddress());
            }
        } else {
            msg("Không tìm thấy thiết bị kết nối.");
        }
    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        finish();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
