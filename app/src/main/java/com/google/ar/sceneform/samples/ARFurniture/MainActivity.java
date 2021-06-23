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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    Button btn_login, btn_reg;
    Connection conn;
    EditText name, pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = findViewById(R.id.button_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
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
                            //检查用户是否可以登录
                            name = findViewById(R.id.username);
                            pwd = findViewById(R.id.password);
                            String uname = name.getText().toString().trim();
                            String upwd = pwd.getText().toString().trim();

                            String sql = "select username, password from user_info where username = 'uname' and password = 'upwd'";
                            Statement st = (Statement)conn.createStatement();
                            ResultSet rs = st.executeQuery(sql);
                            if (rs == null){
                                Toast.makeText(MainActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //关闭数据库
                                try {
                                    conn.close();
                                    Log.d("MainActivity", "关闭连接成功。");
                                } catch (SQLException e) {
                                    Log.d("MainActivity", "关闭连接失败。");
                                }

                                Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
                                intent.putExtra("username", uname); //将用户名作为参数传入主页
                                Toast.makeText(MainActivity.this, uname, Toast.LENGTH_SHORT).show();

                                startActivity(intent);
                            }

                            return;
                        } catch (SQLException e) {
                            Log.d("MainActivity", "连接数据库失败!");
                        }

                    }
                });
                thread.start();

                //先跳转，等数据库问题解决删去
                Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
                intent.putExtra("username", "zhangsan"); //将用户名作为参数传入主页

                startActivity(intent);

            }
        });

        btn_reg = findViewById(R.id.button_resign);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ResignActivity.class);
                startActivity(intent);
            }
        });
    }
}