package com.example.froze.remote_httpurlconnection_cookie;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // SharedPreference에 저장 : 앱을 종료시켜도 유지된다
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Button btn;
    TextView tvResult;
    EditText etID, etPw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getApplicationContext().getSharedPreferences("cookie",0);
        editor = sp.edit();

        etID=(EditText)findViewById(R.id.editTextID);
        etPw=(EditText)findViewById(R.id.editTextpw);
        tvResult=(TextView)findViewById(R.id.textViewResult);
        btn=(Button)findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

        String keyID = "USERID";
        String keyPW = "USERPW";
        tvResult.setText(keyID+sp.getString(keyID,"")+";"+keyPW+sp.getString(keyPW,""));

    }

    public void signin(){

        Map userinfo = new HashMap();
        userinfo.put("user_id",etID.getText().toString());
        userinfo.put("user_pw",etPw.getText().toString());

        new AsyncTask<Map, Void, String>(){
            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("다운로드");
                progress.setMessage("downloading");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected String doInBackground(Map... params) {
                String result = "";
                String url = "http://192.168.0.158/setCookies.jsp";
                try {
                    result = Remote.postData(url, params[0], "POST");
                }catch(Exception e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                List<HttpCookie> cookies = Remote.cookieManager.getCookieStore().getCookies();
                StringBuffer cookieString = new StringBuffer();
                for(HttpCookie cookie : cookies){
                    cookieString.append(cookie.getName()+"="+cookie.getValue()+"\n");
                    editor.putString(cookie.getName(),cookie.getValue());
                    // 삭제 editor.remove("키"), 전체 삭제 editor.clear();
                }
                editor.commit();

                //tvResult.setText("Cookie :"+cookieString.toString());

                progress.dismiss();
            }
        }.execute(userinfo);
    }
}
