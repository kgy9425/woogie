package teamwoogie.woogie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.widget.TextView;

import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import java.util.List;
import android.os.Looper;
import android.util.Log;
import java.util.Date;
import java.util.Locale;

import teamwoogie.woogie.model.MonthDiseaseData;

public class MainActivity extends AppCompatActivity {

    EditText login_id, password;
    String strLogin, strPassword, strName;
    ProgressDialog dialog = null;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this,SplashActivity.class));

        login_id = (EditText) findViewById(R.id.login_id);
        password = (EditText) findViewById(R.id.password);

        password.setInputType(InputType.TYPE_CLASS_TEXT);
        PasswordTransformationMethod passwdtm = new PasswordTransformationMethod();
        password.setTransformationMethod(passwdtm);

        Log.e("TAG",getDateString());
        String tmp=getDateString();
        Log.e("TAG", String.valueOf(tmp.charAt(6))+String.valueOf(tmp.charAt(7)));

    }

    public String getDateString()
    {
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);
        String str_date = df.format(new Date());

        return str_date;
    }

    //임시 예방법 보여주는 버튼
    public void monthDiseaseClicked(View v){

        startActivity((new Intent(getApplicationContext(),ShowMonthDisease.class)));
    }

    public void diseaseRecordClicked(View v){
        startActivity((new Intent(getApplicationContext(),ShowDiseaseRecord.class)));
    }



    public void loginClicked(View v) {

        dialog = ProgressDialog.show(MainActivity.this, "",
                "Validating user...", true);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                login();
                Looper.loop();
            }
        }).start();

    }

    void login() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://ppmj789.dothome.co.kr/php/login.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", login_id.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));
            strLogin=login_id.getText().toString();
            strPassword=password.getText().toString();
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });


            //로그인 성공했을 때 echo로 값
            if (response.equalsIgnoreCase("User Found")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    }
                });

                //성공하고 다른 activity로 넘어감
                Intent intent = new Intent(getApplicationContext(), AfterLoginActivity.class);
                intent.putExtra("userID",strLogin);
                startActivity(intent);

                finish();

            } else {
                Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    ////////////////////////////////////////////////가영아! 여기에 strName에다가 로그인한 사용자 이름 좀 넣어주렴 화이팅 ^^
    void getname(){

    }
    ////////////////////////////////////////////////////////
    //회원가입
    public void signUpClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
    }


    public String getUserID() {
        return strLogin;
    }

    public static class RecordAdapter  extends RecyclerView.Adapter<RecordAdapter.CustomViewHolder>{
        private ArrayList<MonthDiseaseData> mList = null;
        private Activity context = null;



        public RecordAdapter(Activity context, ArrayList<MonthDiseaseData> list) {
            this.context = context;
            this.mList = list;
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            protected TextView diseaseName;
            protected TextView precaution;


            public CustomViewHolder(View view) {
                super(view);
                this.diseaseName= (TextView) view.findViewById(R.id.textView_list_name);
                this.precaution = (TextView) view.findViewById(R.id.textView_list_precaution);
                diseaseName.setTextColor(Color.BLACK);
                precaution.setTextColor(Color.BLACK);
            }
        }


        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

            viewholder.diseaseName.setText(mList.get(position).getDisease_name());
            viewholder.precaution.setText(mList.get(position).getDisease_precaution());
        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() : 0);
        }

    }
}