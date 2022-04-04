package com.example.saveimage_showgallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STROAGE = 1001;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        // storage 접근 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        }
    }

    public void onClickSave(View view) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss"); // 올해년도몇월며칠_몇시몇분몇초 형식으로 포맷하겠다.

        if(imageView == null) { // 이미지뷰에 아무것도 없으면 return
            Log.i("jeongmin", "이미지뷰에 아무것도 없습니다.");
            return;
        }
        Log.i("jeongmin", "이미지뷰에 뭐가 있긴 합니다.");

        saveImage(((BitmapDrawable) imageView.getDrawable()).getBitmap(), simpleDateFormat.format(new Date()));
    }

    public boolean saveImage(Bitmap bitmap, String saveImageName) {

        String saveDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ "/image";
        
        Log.i("jeongmin", "경로 : " + saveDir);

        File file = new File(saveDir);
        if (!file.exists()) {
            file.mkdir();
            Log.i("jeongmin", "폴더가 없습니다.");
        }
        Log.i("jeongmin", "폴더가 있습니다.");

        String fileName = saveImageName + ".png";
        File tempFile = new File(saveDir, fileName);
        FileOutputStream output = null;

        Log.i("jeongmin", "try-catch 문 진입 전");
        try {
            if (tempFile.createNewFile()) {
                output = new FileOutputStream(tempFile);
                // 이미지 줄이기
                Bitmap newBitmap = bitmap.createScaledBitmap(bitmap, 200, 200, true);
                // 이미지 압축. 압축된 파일은 output stream 에 저장. 2번째 인자는 압축률인데 100으로 해도 많이 깨짐..
                newBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                Log.i("jeongmin", "if 문 속 : 저장은 함");
                Toast.makeText(getApplicationContext(), "이미지를 저장했습니다.\n이미지 이름 : " + fileName, Toast.LENGTH_SHORT).show();
                // 이미지 스캐닝 해서 갤러리에서 보이게 해 주는 코드
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));
            } else {
                // 같은 이름의 파일 존재
                Log.i("jeongmin", "else 문 속 : 같은 이름의 파일 존재:"+fileName);

                return false;
            }
            Log.i("jeongmin", "try 문 안의 if 문도 else 문도 진입하지 않음");
        } catch (FileNotFoundException e) {
            Log.i("jeongmin", "FileNotFoundException 에러 : 파일을 찾을 수 없음" + e);
            return false;

        } catch (IOException e) {
            Log.i("jeongmin", "IOException 에러 : " + e);
            e.printStackTrace();
            return false;

        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i("jeongmin", "try-catch 문 후의 끝");
        return true;
    }
}