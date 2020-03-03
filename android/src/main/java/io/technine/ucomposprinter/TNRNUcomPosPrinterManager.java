package io.technine.ucomposprinter;

import android.util.Log;

import com.facebook.react.bridge.ReactMethod;

import hk.ucom.printer.UcomPrinterManager;
import hk.ucom.printer.connection.ResultReceiver;

import static hk.ucom.printer.UcomPrinterManager.PrinterModel.PU808USE;

public class TNRNUcomPosPrinterManager implements ResultReceiver {

    public static final String TAG = "TNRNPrinterManager";

    private UcomPrinterManager mPrinterManager;

    public TNRNUcomPosPrinterManager() {
        Log.d(TAG, "TNRNUcomPosPrinterManager constructor");
    }

    @Override
    public void onConnectionFinished(int status) {
        Log.d(TAG, "onConnectionFinished :: status >> " + status);
//        /*
//         * interface function to perform action when receives result from
//         * printer connection
//         */
//        if (mProgressDialog != null)
//            mProgressDialog.dismiss();
//
//        // for detail of the status code, use getPrinterStatus()
//        Utils.showMessage(mActivity, Result.getMessage(ucomManager.getPrinterStatus()));
//
//        // if error due to lacking permission to the printer, raise dialog for user
//        if (ucomManager.getPrinterStatus() == Result.ERROR_USB_PERMISSION_NOT_GRANTED) {
//            ucomManager.requestUsbPermission(mActivity);
//            return;
//        }
//
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
        Log.d(TAG, mPrinterManager.getPrinterModel().toString());
    }

    public void init() {
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
    public String getPrinterModel() {
        return mPrinterManager.getPrinterModel().toString();
    }

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
