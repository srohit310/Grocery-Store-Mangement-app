package com.stancorp.grocerystorev1.DisplayItems;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;
import com.stancorp.grocerystorev1.R;

import java.util.Scanner;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanSkuCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void handleResult(Result result) {
        Intent data = new Intent();
        data.putExtra("SKUCODE",result.getText().toString());
        setResult(RESULT_OK,data);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}