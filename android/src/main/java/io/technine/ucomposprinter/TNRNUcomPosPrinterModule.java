package io.technine.ucomposprinter;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.widget.ImageView;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDevice;
import android.app.PendingIntent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.File;


import hk.ucom.printer.UcomPrinterManager;
import hk.ucom.printer.UcomUsbDevice;
import hk.ucom.printer.connection.ResultReceiver;

import static hk.ucom.printer.UcomPrinterManager.PrinterModel.PU808USE;

public class TNRNUcomPosPrinterModule extends ReactContextBaseJavaModule implements ResultReceiver {
    public static final String VERSION = "1.0.6";

    public static final String TAG = "TNRNPrinterModule";

    final int boldSize = 2;

    final int normalSize = 0;

    private int fullWidth = 40;

    private final ReactApplicationContext reactContext;

    private UcomPrinterManager mPrinterManager;

    public TNRNUcomPosPrinterModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.initPrinterManager(reactContext);
    }

    @Override
    public String getName() {
        return "TNRNUcomPosPrinter";
    }

    @ReactMethod
    public void getVersion(Callback callback) {
        callback.invoke("Version: " + VERSION);
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    @ReactMethod
    public void show(String message) {
        Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @ReactMethod
    public void getText(Callback callback) {
        callback.invoke("hihihi 222 from module");
    }

    @Override
    public void onConnectionFinished(int status) {
        Log.d(TAG, "onConnectionFinished :: status >> " + status);

//        // if error due to lacking permission to the printer, raise dialog for user
//        if (ucomManager.getPrinterStatus() == Result.ERROR_USB_PERMISSION_NOT_GRANTED) {
//            ucomManager.requestUsbPermission(mActivity);
//            return;
//        }

//        // the status is either Result.SUCCESS or Result.ERROR
//        if (status == Result.SUCCESS) {
//            mConnectionStatus.setText(R.string.connection_status_successful);
//            mActivity.getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, new GeneralFragment()).commit();
//        } else
//            mConnectionStatus.setText(R.string.connection_status_not_successful);

        if (status == Result.SUCCESS) {
            Log.d(TAG, "onConnectionFinished() :: connect success");
        }

        if (status == Result.ERROR) {
            Log.d(TAG, "onConnectionFinished() :: connect failed");
        }

        if (mPrinterManager.getPrinterStatus() == Result.ERROR_USB_PERMISSION_NOT_GRANTED) {
            mPrinterManager.requestUsbPermission(this.reactContext);
            return;
        }

        Log.d(TAG, "onConnectionFinished() :: isConnected >> " + mPrinterManager.isConnected());
    }

    private void initPrinterManager(Context context) {
        Log.d(TAG, "init()");

        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        String DeviceName = "";
        mPrinterManager = new UcomPrinterManager(PU808USE);

        context.registerReceiver(usbPermissionReceiver, new IntentFilter(mPrinterManager.ACTION_USB_PERMISSION));

        Iterator it = deviceList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me2 = (Map.Entry) it.next();
            String productName = deviceList.get(me2.getKey()).getProductName();
            String address = deviceList.get(me2.getKey()).getDeviceName();
            if (productName.contains("Receipt Printer")) {
                DeviceName = address;
            }
            mUsbManager.requestPermission(deviceList.get(me2.getKey()), PendingIntent.getBroadcast(context, 0, new Intent(mPrinterManager.ACTION_USB_PERMISSION), 0));

        }

        mPrinterManager.setManualSocketClose(true);
        mPrinterManager.registerResultReceiver(this);
        mPrinterManager.setUsbConnection(context, DeviceName);
        mPrinterManager.executeCommand(false);
    }

    private BroadcastReceiver usbPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UcomPrinterManager.ACTION_USB_PERMISSION)) {
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    // usb permission granted
                    mPrinterManager.executeCommand(false);
                } else {
                    // usb permission request denied
                }
            }
        }
    };

    @ReactMethod
    public void print() {
        Log.d(TAG, "print()");
        if (!mPrinterManager.isConnected()) {
            Log.d(TAG, "printer not connected");
            return;
        }
        String message = "some text 中文";
        int fontStyleMask = 0;
        mPrinterManager.printText(message);
        mPrinterManager.printText(message);
        mPrinterManager.printText(message, fontStyleMask);
        mPrinterManager.writeln(2);
        mPrinterManager.executeCommand(false);
    }

    @ReactMethod
    public void printHeader(String mes) {
        if (!mPrinterManager.isConnected()) {
            Log.d(TAG, "printer not connected");
            return;
        }
        String message = mes;
        mPrinterManager.setBoldFont(boldSize);
        mPrinterManager.printText(message, UcomPrinterManager.Align.CENTER);
        mPrinterManager.writeln(2);

    }

    @ReactMethod
    public void printFooter(String mes) {
        if (!mPrinterManager.isConnected()) {
            Log.d(TAG, "printer not connected");
            return;
        }
        String message = mes;
        mPrinterManager.setBoldFont(boldSize);
        mPrinterManager.printText(message, UcomPrinterManager.Align.CENTER);
        mPrinterManager.writeln(8);
        mPrinterManager.executeCommand(true);
    }

    @ReactMethod
    public void printContent(String mes) {
        if (!mPrinterManager.isConnected()) {
            Log.d(TAG, "printer not connected");
            return;
        }
        String[] allString = mes.split(" ");
        ArrayList<String> message = new ArrayList<>();
        int strLimit = 0;
        ArrayList<String> tmpString = new ArrayList<>();
        for (int y=0; y < allString.length; y++) {
          strLimit += allString[y].length();
          if (strLimit > 45) {
              String listString = "";
              for (String s : tmpString)
              {
                  listString += s + " ";
              }
              message.add(listString);
              strLimit = 0;
              tmpString.clear();
              continue;
          }
          tmpString.add(allString[y]);
        }
        mPrinterManager.setBoldFont(normalSize);
        for (int x=0; x < message.size(); x++) {
            mPrinterManager.printText(createSpacing(4));
            mPrinterManager.printText(message.get(x));
        }
        mPrinterManager.writeln(2);
    }

    @ReactMethod
    public void printQRCoder(String mes) {
        if (!mPrinterManager.isConnected()) {
            Log.d(TAG, "printer not connected");
            return;
        }
        String message = mes;
        mPrinterManager.setBoldFont(normalSize);
        mPrinterManager.printQRCode(mes, UcomPrinterManager.Align.CENTER);
        mPrinterManager.writeln(2);
    }

    @ReactMethod
    public void printNewLine(int num) {
        mPrinterManager.writeln(num);
    }

    private String createSpacing(int num) {
        return new String(new char[num]).replace('\0', ' ');
    }

    @ReactMethod
    public void printLogo(String path) {
        if (!mPrinterManager.isConnected()) {
            Log.d(TAG, "printer not connected");
            return;
        }
        File imgFile = new  File(path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            mPrinterManager.printBitmap(myBitmap, UcomPrinterManager.Align.CENTER, false);
        }
    }
}
