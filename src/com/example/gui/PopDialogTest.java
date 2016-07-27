//package com.example.gui;
//
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.Toast;
//
//public class PopDialogTest extends Activity {
//    private Button btShare;
//    private Context mContext;
//    private Button btWeixin;
//    private Button btWeibo;
//    private Button btPengyouquan;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mContext = PopDialogTest.this;
//        btShare = (Button) findViewById(R.id.share);
//        btShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            	final Dialog dialog = new Dialog(mContext, R.style.Theme_Light_Dialog);
//                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.mydialog,null);
//                //获得dialog的window窗口
//                Window window = dialog.getWindow();
//                //设置dialog在屏幕底部
//                window.setGravity(Gravity.BOTTOM);
//                //设置dialog弹出时的动画效果，从屏幕底部向上弹出
//                window.setWindowAnimations(R.style.dialogStyle);
//                window.getDecorView().setPadding(0, 0, 0, 0);
//                //获得window窗口的属性
//                android.view.WindowManager.LayoutParams lp = window.getAttributes();
//                //设置窗口宽度为充满全屏
//                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//                //设置窗口高度为包裹内容
//                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                //将设置好的属性set回去
//                window.setAttributes(lp);
//                //将自定义布局加载到dialog上
//                dialog.setContentView(dialogView);
//                btWeixin = (Button) dialogView.findViewById(R.id.bt_weixin);
//                btWeibo = (Button) dialogView.findViewById(R.id.bt_weibo);
//                btPengyouquan = (Button) dialogView.findViewById(R.id.bt_pengyouquan);
//                dialog.show();
//                btWeixin.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(mContext,"分享到微信",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                btWeibo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(mContext,"分享到微博",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                btPengyouquan.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(mContext,"分享到朋友圈",Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//
//    }
//
//
//}
