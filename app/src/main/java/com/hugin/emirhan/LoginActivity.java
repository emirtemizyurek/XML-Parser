package com.hugin.emirhan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText editTextUsername, editTextPassword;
    String user, password;
    Button buttonLogin;
    DBHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initThis();
        initClickableItems();
        db = new DBHandler(this);

    }

    private void initThis(){
        editTextUsername = findViewById(R.id.login_activity_editText_username);
        editTextPassword =  findViewById(R.id.login_activity_editText_password);
        buttonLogin = findViewById(R.id.login_activity_button_login);
    }

    private void initClickableItems(){
        buttonLogin.setOnClickListener(view -> {
            checkAndLogin();
        });
    }

    private void checkAndLogin(){
         user = editTextUsername.getText().toString();
         password = editTextPassword.getText().toString();

        if(user.equals("")|| password.equals("")){
            Toast.makeText(LoginActivity.this,"Please enter all the fields",Toast.LENGTH_SHORT).show();
        }
        else{
            Boolean checkuserpass = db.checkUsernamePassword(user,password);
            if(checkuserpass){
                Toast.makeText(LoginActivity.this,"Sing in successful",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(getApplicationContext(),XmlParserActivity.class);
                startActivity(intent);

            }
            else{
                Toast.makeText(LoginActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
            }
        }
    }
}