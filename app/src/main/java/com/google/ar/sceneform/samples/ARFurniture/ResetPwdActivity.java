package com.google.ar.sceneform.samples.ARFurniture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ResetPwdActivity extends AppCompatActivity {

    Connection conn;
    EditText old_pwd, new_pwd, confirm_pwd;
    Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);

        btn_confirm = findViewById(R.id.btn_confirm);

        //提交修改信息
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加载数据库驱动
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Log.d("MainActivity", "加载JDBC驱动成功！");
                } catch (ClassNotFoundException e) {
                    Log.d("MainActivity", "加载JDBC驱动失败！");
                    return;
                }
                //连接数据库（开辟一个新线程）
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 反复尝试连接，直到连接成功后退出循环

                        // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                        String ip = "localhost";                 //本机IP
                        int port = 3306;                              //mysql默认端口
                        String dbName = "arapp";             //自己的数据库名
                        String url = "jdbc:mysql://10.192.16.86:3306/arapp?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8";
                        //String url = "jdbc:mysql://" + ip + ":" + port
                        // + "/" + dbName; // 构建连接mysql的字符串
                        String user = "root";                //自己的用户名
                        String password = "111";           //自己的密码

                        // 3.连接JDBC
                        try {
                            conn = DriverManager.getConnection(url, user, password);
                            Log.d("MainActivity", "连接数据库成功!");

                            //检查用户是否可以注册
                            old_pwd = findViewById(R.id.old_pwd);
                            new_pwd = findViewById(R.id.new_pwd);
                            confirm_pwd = findViewById(R.id.new_pwd_twice);

                            String Opwd = old_pwd.getText().toString().trim();
                            String Npwd = new_pwd.getText().toString().trim();
                            String Npwd2 = confirm_pwd.getText().toString().trim();

                            //先比较两次密码是否相同,相同则提醒
                            if (!Npwd.equals(Npwd2)){
                                Toast.makeText(ResetPwdActivity.this, "两次密码输入不同！", Toast.LENGTH_SHORT).show();

                            }

                            //否则在数据库中修改密码
                            else{
                                String sql = "update user_info set password=Npwd where ";
                                Statement st = (Statement)conn.createStatement();
                                st.executeQuery(sql);
                            }


                            //关闭数据库
                            try {
                                conn.close();
                                Log.d("MainActivity", "关闭连接成功。");
                            } catch (SQLException e) {
                                Log.d("MainActivity", "关闭连接失败。");
                            }
                            //跳转到主页
                            Intent intent = new Intent(ResetPwdActivity.this, HomepageActivity.class);
                            startActivity(intent);

                            return;
                        } catch (SQLException e) {
                            Log.d("MainActivity", "连接数据库失败!");
                        }

                    }
                });
                thread.start();

                //先跳转，等数据库问题解决删去
                Intent intent = new Intent(ResetPwdActivity.this, HomepageActivity.class);
                startActivity(intent);

            }
        });

    }
}