package bornbaby.com.pickmenow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import bornbaby.com.pickmenow.activity.WelcomeActivity;
import bornbaby.com.pickmenow.model.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private Button btn_sign_in;
    private Button btn_register;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference users;

    private RelativeLayout rl_main;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*execute font automatically*/
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_main);

        inITFirebaseauthe();

        inItUi();

    }

    private void inITFirebaseauthe() {
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("Users");
    }

    private void inItUi() {

        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_register = findViewById(R.id.btn_register);
        rl_main = findViewById(R.id.rl_main);

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });
    }

    private void showLoginDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please email to sign in");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View login_view = layoutInflater.inflate(R.layout.layout_sign_in, null);

        final MaterialEditText et_email_sign_in = login_view.findViewById(R.id.et_email_sign_in);
        final MaterialEditText et_password_sign_in = login_view.findViewById(R.id.et_password_sign_in);


        dialog.setView(login_view);
        dialog.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

                //validation for register
                if (TextUtils.isEmpty(et_email_sign_in.getText().toString())) {
                    showSnackbar("Please enter Email Address");
                }
                if (TextUtils.isEmpty(et_password_sign_in.getText().toString())) {
                    showSnackbar("Please enter Password");
                }
                if (et_password_sign_in.getText().toString().length() > 6) {
                    showSnackbar("Password Too short !!!!");

                }
                auth.signInWithEmailAndPassword(et_email_sign_in.getText().toString(), et_password_sign_in.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSnackbar("Failed " + e.getMessage());

                    }
                });
            }


        });

        dialog.setNegativeButton("CANCEL ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

            }
        });
        dialog.show();


    }


    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please email to register");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View register_view = layoutInflater.inflate(R.layout.layout_register, null);

        final MaterialEditText et_email = register_view.findViewById(R.id.et_email);
        final MaterialEditText et_password = register_view.findViewById(R.id.et_password);
        final MaterialEditText et_name = register_view.findViewById(R.id.et_name);
        final MaterialEditText et_phone = register_view.findViewById(R.id.et_phone);


        dialog.setView(register_view);
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

                //validation for register
                if (TextUtils.isEmpty(et_email.getText().toString())) {
                    showSnackbar("Please enter Email Address");
                }
                if (TextUtils.isEmpty(et_password.getText().toString())) {
                    showSnackbar("Please enter Password");
                }

                if (et_password.getText().toString().length() > 6) {
                    showSnackbar("Password Too short !!!!");

                }
                if (TextUtils.isEmpty(et_name.getText().toString())) {
                    showSnackbar("Please enter Name ");
                }
                if (TextUtils.isEmpty(et_phone.getText().toString())) {
                    showSnackbar("Please enter Phone");
                }

                //register with new user
                auth.createUserWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        //Save User TO Db

                        User user = new User();

                        user.setEmail(et_email.getText().toString());
                        user.setName(et_name.getText().toString());
                        user.setPassword(et_password.getText().toString());
                        user.setPhone(et_phone.getText().toString());

                        // user email to key

                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showSnackbar("Register Successfully !!!!");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showSnackbar("Failed " + e.getMessage());

                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSnackbar("Failed " + e.getMessage());

                    }
                });


            }
        });
        dialog.setNegativeButton("CANCEL ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

            }
        });
        dialog.show();


    }

    private void showSnackbar(String s) {
        Snackbar.make(rl_main, s, Snackbar.LENGTH_SHORT).show();
        return;
    }
}
