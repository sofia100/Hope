package com.example.android.hope;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;



/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    Button btnGenerateOTP, btnSignIn;
    EditText etPhoneNumber, etOTP;

//global variable for verification state change call back that we have to pass as a parameter.
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;



    String phoneNumber, otp;

    FirebaseAuth auth;
    private String verificationCode;
    private Spinner numberSpinner;

    void setupSpinner()
    {
        ArrayAdapter numberSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.valid_numbers,android.R.layout.simple_spinner_dropdown_item);

        numberSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        numberSpinner=findViewById(R.id.spinner_phone_number);
        numberSpinner.setAdapter(numberSpinnerAdapter);
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection))
                {
                    if(selection.equals("7655912341"))
                    {
                        etPhoneNumber.setText("7655912341");
                    }
                    if (selection.equals("9938178512"))
                    {
                        etPhoneNumber.setText("9938178512");
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
etPhoneNumber.setText("select the phone number");
            }
        });

    }
    private void findViews() {
        btnGenerateOTP=findViewById(R.id.btn_generate_otp);
        btnSignIn=findViewById(R.id.btn_sign_in);
        etPhoneNumber=findViewById(R.id.et_phone_number);
        etOTP=findViewById(R.id.txt_pin_entry);
    }
    private void StartFirebaseLogin() {
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this,"verification failed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(LoginActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };
    }
    BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                if(intent !=null && intent.getExtras()!=null && intent.getStringExtra("msgs")!=null)
                {
                    final String message = intent.getStringExtra("msgs");
                    String otp = message.replaceAll("\\D+","");
                    etOTP.setText(otp);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                //Toast.makeText(this,"ERROR!!",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver2);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerReceiver(receiver2,new IntentFilter("Message Receiver"));
        if(ContextCompat.checkSelfPermission(getBaseContext(),"android.permission.RECEIVE_SMS")!= PackageManager.PERMISSION_GRANTED)
        {
            int CODEREQ = 124;
            ActivityCompat.requestPermissions(this,new String[]{"android.permission.RECEIVE_SMS"},CODEREQ);
        }

        if(ContextCompat.checkSelfPermission(getBaseContext(),"android.permission.READ_SMS")!= PackageManager.PERMISSION_GRANTED)
        {
            int CODEREQ = 123;
            ActivityCompat.requestPermissions(this,new String[]{"android.permission.READ_SMS"},CODEREQ);
        }

        findViews();
        StartFirebaseLogin();

        etOTP.setInputType(InputType.TYPE_NULL);

        btnGenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber=etPhoneNumber.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,                     // Phone number to verify
                        60,                           // Timeout duration
                        TimeUnit.SECONDS,                // Unit of timeout
                        LoginActivity.this,        // Activity (for callback binding)
                        mCallback);                      // OnVerificationStateChangedCallbacks
            }
        });
        //Above method will send an SMS to the provided phone number. As verifyPhoneNumber() is reentrant, it will not send another SMS on button click until the original request is timed out.

        etOTP.setInputType(InputType.TYPE_CLASS_TEXT);
        etOTP.requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(etOTP, InputMethodManager.SHOW_FORCED);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp=etOTP.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                SigninWithPhone(credential);
            }
        });

    setupSpinner();
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this,NewActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}

//keypad,limit log karenge,,activity back or left,memory optimization,