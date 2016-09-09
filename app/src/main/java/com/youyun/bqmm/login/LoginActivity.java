package com.youyun.bqmm.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.youyun.bqmm.R;
import com.youyun.bqmm.TempActivity;
import com.youyun.bqmm.login.mvp.LoginPresenter;
import com.youyun.bqmm.login.mvp.LoginView;
import com.youyun.bqmm.receive.ReceiveRunnable;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private Button loginBtn;
    private ProgressBar progressBar;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        addListener();
        initData();
    }

    private void initData() {
        presenter = new LoginPresenter(this, this);
    }

    private void addListener() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.login();
            }
        });
    }

    private void initView() {
        setToolBar();
        loginBtn = (Button) findViewById(R.id.btn_login);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    @Override
    public void loginSuccess() {
        ReceiveRunnable receiveRunnable = new ReceiveRunnable(getApplicationContext());
        Thread msgHandler = new Thread(receiveRunnable);
        msgHandler.start();

        Intent intent = new Intent(this, TempActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFail() {
        Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
