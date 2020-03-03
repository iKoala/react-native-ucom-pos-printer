package io.technine.ucomposprinter;

import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import hk.ucom.printer.UcomPrinterManager;
import hk.ucom.printer.connection.ResultReceiver;

import static hk.ucom.printer.UcomPrinterManager.PrinterModel.PU808USE;

public class TNRNUcomPosPrinterModule extends ReactContextBaseJavaModule implements ResultReceiver {

    public static final String VERSION = "1.0.6";

    public static final String TAG = "TNRNPrinterModule";

    private final ReactApplicationContext reactContext;

    private UcomPrinterManager mPrinterManager;

    public TNRNUcomPosPrinterModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        this.initPrinterManager();
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

        Log.d(TAG, "onConnectionFinished() :: isConnected >> " + mPrinterManager.isConnected());
    }

    private void initPrinterManager() {
        Log.d(TAG, "init()");
        String printerAddress = "192.168.1.87";
        String printerPort = "9100";
        mPrinterManager = new UcomPrinterManager(PU808USE);
        mPrinterManager.setManualSocketClose(true);
        mPrinterManager.registerResultReceiver(this);
        mPrinterManager.setEthernetConnection(printerAddress, Integer.parseInt(printerPort));
        mPrinterManager.executeCommand(false);
    }

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
}
