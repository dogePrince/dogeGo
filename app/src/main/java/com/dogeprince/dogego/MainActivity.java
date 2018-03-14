package com.dogeprince.dogego;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private GoPanel goPanel;
    private int philosophyCount = 0;

    MenuItem showCount = null;
    MenuItem showPhilosophy = null;
    MenuItem showFreeBt = null;
    MenuItem showCancel = null;
    MenuItem showSave = null;

    String authorInfo = "Design by 姚舜";

    Button button1 = null;
    Button button2 = null;
    Button button3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);//全屏和

        goPanel = (GoPanel) findViewById(R.id.id_dogego);

        button1 = (Button) findViewById(R.id.bt1);
        button2 = (Button) findViewById(R.id.bt2);
        button3 = (Button) findViewById(R.id.bt3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPanel.freePiece = 0;
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPanel.freePiece = 1;
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPanel.freePiece = -2;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info,menu);
        showCount = menu.findItem(R.id.item02);
        showCancel = menu.findItem(R.id.item03);
        showPhilosophy = menu.findItem(R.id.item06);
        showFreeBt = menu.findItem(R.id.item05);
        showSave = menu.findItem(R.id.item07);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item01:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("DogeGo提示");
                alertDialog.setMessage("重新开始一局吗？");
                alertDialog.setNegativeButton("继续游戏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goPanel.restart();
                        showCancel.setEnabled(true);
                        showSave.setEnabled(true);
                        goPanel.freePiece = -1;
                        button1.setVisibility(Button.INVISIBLE);
                        button2.setVisibility(Button.INVISIBLE);
                        button3.setVisibility(Button.INVISIBLE);
                        showFreeBt.setTitle("开启自由落子");
                    }
                });
                alertDialog.show();
                break;

            case R.id.item02:
                goPanel.IsShow = !goPanel.IsShow;
                goPanel.invalidate();
                if (goPanel.IsShow)
                {
                    if (showCount != null)
                    {
                        showCount.setTitle("隐藏落子顺序");
                    }
                }else
                {
                    if (showCount != null)
                    {
                        showCount.setTitle("显示落子顺序");
                    }
                }
                break;
            case R.id.item03:
                if (goPanel.ban_cancel)
                {
                    Toast.makeText(this,"本局无法撤销",Toast.LENGTH_SHORT).show();
                    showCancel.setEnabled(false);
                    showSave.setEnabled(false);
                    break;
                }
                goPanel.cancelPiece();
                goPanel.invalidate();
                break;
            case R.id.item04:
                Toast.makeText(this, authorInfo ,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,AuthorInfo.class);
                startActivity(intent);
                philosophyCount ++;

                if (philosophyCount >4)
                {
                    showPhilosophy.setVisible(true);
                }
                break;
            case R.id.item05:
                if (goPanel.freePiece == -1)
                {
                    AlertDialog.Builder freePieceTip = new AlertDialog.Builder(MainActivity.this);
                    freePieceTip.setTitle("DogeGo提示");
                    freePieceTip.setMessage("在自由落子模式中产生落子后，本局将不能进行撤销和保存，是否继续？");
                    freePieceTip.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    freePieceTip.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            goPanel.freePiece = 0;
                            Toast.makeText(getBaseContext(), "自由落子已开启" , Toast.LENGTH_SHORT).show();
                            button1.setVisibility(Button.VISIBLE);
                            button2.setVisibility(Button.VISIBLE);
                            button3.setVisibility(Button.VISIBLE);
                            showFreeBt.setTitle("关闭自由落子");
                        }
                    });
                    freePieceTip.show();
                }
                else
                {
                    goPanel.freePiece = -1;
                    Toast.makeText(this, "自由落子已关闭" , Toast.LENGTH_SHORT).show();
                    button1.setVisibility(Button.INVISIBLE);
                    button2.setVisibility(Button.INVISIBLE);
                    button3.setVisibility(Button.INVISIBLE);
                    showFreeBt.setTitle("开启自由落子");
                }
                break;
            case R.id.item06:
                goPanel.philosophy = !goPanel.philosophy;
                if (goPanel.philosophy)
                {
                    if (showPhilosophy != null)
                    {
                        showPhilosophy.setTitle("奥♂义关");
                    }
                }else
                {
                    if (showPhilosophy != null)
                    {
                        showPhilosophy.setTitle("奥♂义开");
                    }
                }
                break;
            case R.id.item07:
                if (goPanel.ban_cancel)
                {
                    Toast.makeText(this,"本局无法保存",Toast.LENGTH_SHORT).show();
                    showCancel.setEnabled(false);
                    showSave.setEnabled(false);
                    break;
                }
                goPanel.saveGame();
                break;
            case R.id.item08:
                goPanel.loadGame();
                showCancel.setEnabled(true);
                showSave.setEnabled(true);
                goPanel.freePiece = -1;
                button1.setVisibility(Button.INVISIBLE);
                button2.setVisibility(Button.INVISIBLE);
                button3.setVisibility(Button.INVISIBLE);
                showFreeBt.setTitle("开启自由落子");
                break;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("退出DogeGo吗？");
            alertDialog.setMessage("离开前，记得保存对局");
            alertDialog.setNegativeButton("继续游戏", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.setPositiveButton("直接退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.setNeutralButton("保存对局", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goPanel.saveGame();
                }
            });
            alertDialog.show();
        }
        return false;
    }


}
