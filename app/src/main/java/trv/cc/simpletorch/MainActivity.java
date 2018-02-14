package trv.cc.simpletorch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private CameraManager manager;
    private Camera camera;
    private TextView mainText;
    private ImageButton switchBtn;
    private boolean isOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mainText = (TextView) findViewById(R.id.main_text);
        switchBtn = (ImageButton) findViewById(R.id.torch_btn);

        mainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "你的手机没有闪光灯!\n  启用屏幕手电模式!", Toast.LENGTH_SHORT).show();
            switchBtn.setVisibility(View.INVISIBLE);
            screenLight();
        } else {
            switchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOpen) {
                        openFlash();
                        switchBtn.setBackgroundResource(R.drawable.button_on);
                    } else {
                        closeFlash();
                        switchBtn.setBackgroundResource(R.drawable.button_off);
                    }
                    isOpen = !isOpen;
                }
            });
        }
    }

    private void screenLight() {
        Window localWindow = this.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        params.screenBrightness = 1.0f;
        localWindow.setAttributes(params);
    }

    @SuppressLint("NewApi")
    private void openFlash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
                if (manager != null) {
                    manager.setTorchMode("0", true);
                }
            } else {
                camera = Camera.open();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void closeFlash() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (manager == null) {
                    return;
                }
                manager.setTorchMode("0", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (camera == null) {
                return;
            }
            camera.stopPreview();
            camera.release();
        }
    }
}
