package com.hugin.emirhan;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText username, password, rePassword;
    String user, pass, repass;
    Button buttonRegister;
    TextView textViewLogin;
    DBHandler Db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initThis();
        initClickableItems();
        Db = new DBHandler(this);
        checkSettings();

    }

    private void initThis() {
        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_password);
        rePassword = findViewById(R.id.editText_rePassword);
        textViewLogin = findViewById(R.id.textview_Login);
        buttonRegister = findViewById(R.id.button_Register);
    }

    private void initClickableItems(){
        textViewLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });

        buttonRegister.setOnClickListener(view -> saveAndCheck());
    }


    private void saveAndCheck() {
        user = username.getText().toString();
        pass = password.getText().toString();
        repass = rePassword.getText().toString();


        if (user.equals("") || pass.equals("") || repass.equals("")) {
            Toast.makeText(RegisterActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
        } else {
            if (pass.equals(repass)) {
                Boolean checkUser = Db.checkUsername(user);
                if (!checkUser) {
                    Boolean insert = Db.insertData("Hugin", user, pass);
                    if (insert) {
                        Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), XmlParserActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }

                }
                Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkSettings(){
        Toast.makeText(RegisterActivity.this, "Please enter the required permissions.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}