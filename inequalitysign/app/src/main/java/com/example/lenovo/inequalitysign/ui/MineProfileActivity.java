package com.example.lenovo.inequalitysign.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.lenovo.inequalitysign.R;

import java.io.File;

public class MineProfileActivity extends AppCompatActivity {

    private ImageButton btn_back;
    private ImageButton btn1;
    private ImageButton btn2;
    private ImageButton btn3;
    private Button btn11;
    private Button btn12;
    private Button btn13;
    private TextView tv_name;
    private TextView tv_sex;
    protected static Uri tempUri;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.mine_profileB1:
                    Intent i = new Intent();
                    i.setClass(MineProfileActivity.this,AlreadyLogin.class);
                    startActivity(i);
                    break;
                case R.id.mine_profileB2:
                    //设置头像 从本地选择或者通过照相获取
                   LoadImg();
                    break;
                case R.id.mine_profileB3:
                    //设置昵称
                    Intent ii = new Intent();
                    ii.setClass(MineProfileActivity.this,EditnameActivity.class);
                    startActivity(ii);
                    break;
                case R.id.mine_profileB4:
                    //设置性别
                    Intent intent = new Intent();
                    intent.setClass(MineProfileActivity.this,Gender.class);
                    startActivity(intent);
                    break;
            }
        }
    };
    private ImageView iv;


    /**
     * 加载popupwindow
     */
    private void LoadImg() {
        View view = LayoutInflater.from(this).inflate(R.layout.manager_popupwindow, null);
        btn11 = (Button)findViewById(R.id.btn1);//照相；
        btn12 = (Button)findViewById(R.id.btn2);//从相册中获取
        btn13 = (Button)findViewById(R.id.btn3);//取消
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(false);
        View parent = LayoutInflater.from(this).inflate(R.layout.activity_mine_profile, null);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //popupWindow在弹窗的时候背景半透明
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.5f;
        getWindow().setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            }
        });

        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到调用系统相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
               //制定照片保存路径sd卡，image.jpg为一个临时文件，每次拍照后都会被替换
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                startActivityForResult(intent, TAKE_PICTURE);
                popupWindow.dismiss();
            }
        });
        btn12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到调用系统图库
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "请选择图片"), CHOOSE_PICTURE);
                popupWindow.dismiss();
            }
        });
        btn13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_OK){//如果返回码可用
            switch (requestCode){
                case TAKE_PICTURE:   //开始对图片进行裁剪
                    startPhotoZoom(tempUri);
                    break;
                case CHOOSE_PICTURE: //开始对图片进行裁剪处理
                    startPhotoZoom(data.getData());
                    break;
                case CROP_SMALL_PICTURE: //让刚才裁剪的照片显示
                    if(data != null){
                        setImageToview(data);
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImageToview(Intent data) {
        Bundle extras = data.getExtras();
        if(extras != null){
            Bitmap photo = extras.getParcelable("data");
            
        }
    }

    /**
     * 裁剪图片实现方法
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_profile);
        findView();
        setOnClick();
        setContent();
    }

    private void setContent() {
        SharedPreferences spf = getSharedPreferences("count", Context.MODE_APPEND);
        String name = spf.getString("Name","");
        String sex = spf.getString("Sex","");
        tv_name.setText(name);
        tv_sex.setText(sex);
    }

    private void setOnClick() {
        btn_back.setOnClickListener(mListener);
        btn1.setOnClickListener(mListener);
        btn2.setOnClickListener(mListener);
        btn3.setOnClickListener(mListener);
    }

    private void findView() {
        btn_back = (ImageButton)findViewById(R.id.mine_profileB1);//回退按钮
        btn1 = (ImageButton)findViewById(R.id.mine_profileB2);//设置头像
        iv = (ImageView)findViewById(R.id.iv);
        btn2  = (ImageButton)findViewById(R.id.mine_profileB3);//设置昵称
        btn3 = (ImageButton)findViewById(R.id.mine_profileB4);//设置性别
        tv_name = (TextView)findViewById(R.id.tv_name);
        tv_sex = (TextView)findViewById(R.id.tv_sex);

    }
}
