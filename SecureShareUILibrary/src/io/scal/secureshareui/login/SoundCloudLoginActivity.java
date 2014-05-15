
package io.scal.secureshareui.login;

import java.io.IOException;

import io.scal.secureshareui.controller.SoundCloudSiteController;
import io.scal.secureshareui.soundcloud.ApiWrapper;
import io.scal.secureshareui.soundcloud.Token;
import io.scal.secureshareuilibrary.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SoundCloudLoginActivity extends Activity {

    private int mAccessResult = Activity.RESULT_CANCELED;
    private String mAccessToken = null;

    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_soundcloud_login);

        init();
    }

    private void init() {
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                new CheckCredentialsAsync().execute(username, password);
            }
        });
    }

    private class CheckCredentialsAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            final ApiWrapper wrapper = new ApiWrapper(SoundCloudSiteController.APP_CLIENT_ID,
                    SoundCloudSiteController.APP_CLIENT_SECRET,
                    null,
                    null);
            Token token = null;
            try {
                token = wrapper.login(params[0], params[1], Token.SCOPE_NON_EXPIRING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (token != null) { // success
                mAccessToken = token.access;
                return "-1";
            }

            return "0";
        }

        @Override
        protected void onPostExecute(String resultStr) {
            int result = Integer.parseInt(resultStr);

            btnSignIn.setEnabled(true);
            TextView tvLoginError = (TextView) findViewById(R.id.tvLoginError);

            if (result == 0) {
                mAccessResult = Activity.RESULT_CANCELED;
                tvLoginError.setVisibility(View.VISIBLE);
            }
            else {
                mAccessResult = Activity.RESULT_OK;
                tvLoginError.setVisibility(View.GONE);
                finish();
            }
        }
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("credentials", mAccessToken);

        setResult(mAccessResult, data);
        super.finish();
    }
}
