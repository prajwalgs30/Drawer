package in.prajwalgs.drawer.Activities;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.prajwalgs.drawer.R;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout addSquare;
    private List<WindowManager> squares = new ArrayList<>();
    private List<ImageView> squaresImage = new ArrayList<>();
    private List<WindowManager.LayoutParams> squaresParams = new ArrayList<>();
    private ExecutorService executorService;
    WindowManager.LayoutParams params;
    private int LAYOUT_FLAG;
    private SeekBar changeSize;
    private Button get_coordinates;
    private int length = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        addSquare = findViewById(R.id.addSquare);
        changeSize = findViewById(R.id.changeSize);
        get_coordinates = findViewById(R.id.get_coordinates);

        get_coordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = 0;
                List<String> squareList = new ArrayList<>();
                for (WindowManager.LayoutParams param : squaresParams){
                    int x = param.x;
                    int y = param.y;

                    int x1 = x - (length/2);
                    int y1 = y - (length/2);

                    int x2 = x + (length/2);
                    int y2 = y - (length/2);

                    int x3 = x - (length/2);
                    int y3 = y + (length/2);

                    int x4 = x + (length/2);
                    int y4 = y + (length/2);

                    String coordinates = "coordinates:[("+x1+","+y1+"),"+
                            "("+x2+","+y2+"),"+
                            "("+x3+","+y3+"),"+
                            "("+x4+","+y4+")]";

                    squareList.add(coordinates);
                    i++;

                }
                Gson gson = new Gson();
                String JSON = gson.toJson(squareList);

                Toast.makeText(MainActivity.this,"{rectangles:"+ JSON +"}", Toast.LENGTH_LONG).show();
            }
        });

        changeSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int i = 0;
                for(WindowManager windowManager : squares){
                    params = squaresParams.get(i);
                    params.width = seekBar.getProgress();
                    params.height = seekBar.getProgress();
                    length = seekBar.getProgress();
                    windowManager.updateViewLayout(squaresImage.get(i),params);
                    i++;
                }
            }
        });

        addSquare.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:

                        return true;

                    case MotionEvent.ACTION_UP: {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (Settings.canDrawOverlays(MainActivity.this)) {

                                ImageView square = new ImageView(MainActivity.this);
                                square.setImageDrawable(getResources().getDrawable(R.drawable.square));
                                executorService = Executors.newSingleThreadExecutor();
                                params = new WindowManager.LayoutParams(
                                        LAYOUT_FLAG,
                                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                                        PixelFormat.TRANSLUCENT);

                                params.width = 100;
                                params.height = 100;
                                params.gravity = Gravity.TOP | Gravity.LEFT;
                                params.x = (int) event.getX();
                                params.y = (int) event.getY();
                                Log.d("X : "+event.getX(),"Y : "+event.getY());
                                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                                wm.addView(square, params);
                                squares.add(wm);
                                squaresImage.add(square);
                                squaresParams.add(params);

                            } else {

                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                                Toast.makeText(MainActivity.this, R.string.provide_overlay_permission, Toast.LENGTH_LONG).show();
                            }
                        }else{
                            ImageView square = new ImageView(MainActivity.this);
                            square.setImageDrawable(getResources().getDrawable(R.drawable.square));
                            executorService = Executors.newSingleThreadExecutor();
                            params = new WindowManager.LayoutParams(
                                    LAYOUT_FLAG,
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                                    PixelFormat.TRANSLUCENT);

                            params.width = 100;
                            params.height = 100;
                            params.gravity = Gravity.TOP | Gravity.LEFT;
                            params.x = (int) event.getX();
                            params.y = (int) event.getY();
                            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                            wm.addView(square, params);
                            squares.add(wm);
                            squaresImage.add(square);
                            squaresParams.add(params);
                        }

                        return true;
                    }

                }
                return false;
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        try {
            for (ImageView imageView : squaresImage) {
                if (imageView != null) {
                    ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(imageView);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try {
            for (ImageView imageView : squaresImage) {
                if (imageView != null) {
                    ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(imageView);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
