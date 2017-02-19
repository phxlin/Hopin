package com.yufanlin.hopin;

/**
 * Created by Yufan on 2/19/17.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignin;
    private EditText editEmail;
    private EditText editPassword;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth fireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fireBaseAuth = FirebaseAuth.getInstance();  //get instance of user

        if(fireBaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), SplitActivity.class));
        }

        progressDialog = new ProgressDialog(this);  //create a processDialog object

        buttonSignin = (Button) findViewById(R.id.buttonSignin);    //create button register

        editEmail = (EditText) findViewById(R.id.editEmail);        //take in user email input
        editPassword = (EditText) findViewById(R.id.editPassword);  //take in user password input

        textViewSignin = (TextView) findViewById(R.id.textViewSignin);  //take in user sign in option

        buttonSignin.setOnClickListener(this);    //record button press
        textViewSignin.setOnClickListener(this);    //record press on sign in
    }

    //attempts to register a user based off of their username and password
    private void userLogin() {
        String email = editEmail.getText().toString().trim();   //get email from user
        String password = editPassword.getText().toString().trim(); //get password from user

        if (TextUtils.isEmpty(email)) { //checks if the input email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {  //checks if the input password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging In");   //adds message to loading screen
        progressDialog.show();  //shows loading screen

        fireBaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //start next activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), SplitActivity.class));
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSignin) {   //if button is clicked
            userLogin();             //call register user function
        }
        if (view == textViewSignin) {
            //open login activity here
            finish();
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }
}
