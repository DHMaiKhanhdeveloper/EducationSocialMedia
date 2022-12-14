package com.example.kmapp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kmapp.R;
import com.example.kmapp.ReplaceActivity;
import com.example.kmapp.utils_service.UtilService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordFragment extends Fragment {

    private TextView tvLogin;
    private Button btnRecover ;
    private EditText edtEmail;

    private FirebaseAuth auth;
    private ProgressDialog loadingBar;
    private String strEmail;
    private UtilService utilService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        clickListener();
    }

    private void init(View view) {

        tvLogin = view.findViewById(R.id.loginTV);
        edtEmail = view.findViewById(R.id.emailET);
        btnRecover = view.findViewById(R.id.recoverBtn);

        auth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(getActivity());
        utilService = new UtilService();

    }

    private void clickListener(){

        btnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail(v);
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReplaceActivity) getActivity()).setFragment(new LoginFragment());
            }
        });

    }

    private void sendPasswordResetEmail(View view) {
        strEmail = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(strEmail)) {
            showMessage("Vui l??ng nh???p email c???a b???n");
            utilService.showSnackBar(view,"Vui l??ng nh???p email c???a b???n");
            edtEmail.setError("B???t bu???c ph???i nh???p email");
            edtEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            showMessage("Vui l??ng nh???p l???i email c???a b???n");
            utilService.showSnackBar(view,"Vui l??ng nh???p email c???a b???n");
            edtEmail.setError("B???t bu???c ph???i nh???p email");
            edtEmail.requestFocus();
        }else {
            loadingBar.setTitle("T???o t??i kho???n google m???i");
            loadingBar.setMessage("Vui l??ng ?????i, trong khi ch??ng t??i ??ang t???o T??i kho???n m???i c???a b???n...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            auth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Password reset email send successfully",
                                Toast.LENGTH_SHORT).show();

                        showAlertDialog();

                    }else{
                        String errMsg = task.getException().getMessage();
                        Toast.makeText(getContext(), "Error: "+errMsg, Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();
                }
            });
        }
    }

    private void showAlertDialog() {
        //thi???t l???p tr??nh t???o c???nh b??o
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Email ch??a ???????c x??c minh");
        builder.setMessage("Vui l??ng x??c minh email c???a b???n ngay b??y gi???. B???n kh??ng th??? ????ng nh???p m?? kh??ng x??c minh email. ");


        builder.setPositiveButton("Ti???p t???c", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                //G???i ???ng d???ng email trong c???a s??? m???i v?? kh??ng ph???i trong ???ng d???ng c???a ch??ng t??i
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void showMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}