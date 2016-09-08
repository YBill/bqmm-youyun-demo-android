package com.youyun.bqmm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ioyouyun.wchat.WeimiInstance;
import com.youyun.bqmm.chat.ChatActivity;

public class TempActivity extends AppCompatActivity {

    private TextView uidText;
    private EditText toUidEdit;
    private Button chatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        initView();
        addListener();
        initData();
    }

    private void initData() {
        uidText.setText(WeimiInstance.getInstance().getUID());
    }

    private void addListener() {
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = toUidEdit.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    Intent intent = new Intent(TempActivity.this, ChatActivity.class);
                    intent.putExtra("touid", text);
                    startActivity(intent);
                }
            }
        });
    }

    private void initView() {
        setToolBar();

        uidText = (TextView) findViewById(R.id.tv_uid);
        toUidEdit = (EditText) findViewById(R.id.et_uid);
        chatBtn = (Button) findViewById(R.id.btn_chat);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
