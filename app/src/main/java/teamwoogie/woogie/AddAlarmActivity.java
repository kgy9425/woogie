package teamwoogie.woogie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker.OnTimeChangedListener;


public class AddAlarmActivity extends Activity implements OnDateChangedListener, OnTimeChangedListener {

    private static final int ORIGIN = 0;
    private static final int COMPARE = 1;
    // 알람 매니저
    private AlarmManager mManager;
    // 설정 일시
    private GregorianCalendar mCalendar;
    //일자 설정 클래스
    private DatePicker mDate;
    //시작 설정 클래스
    private TimePicker mTime;
    EditText repeat;
    ImageView ivOrigin = null;
    Bitmap bitOrigin = null;
    Bitmap bitCompare = null;
    ByteArrayOutputStream streamOrigin = new ByteArrayOutputStream();
    ByteArrayOutputStream streamCompare = new ByteArrayOutputStream();
    byte[] arrayOrigin = null;
    byte[] arrayCompare = null;
    int repeatTime = 0 ;
    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로

    private NotificationManager mNotification;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = new GregorianCalendar();
        //셋 버튼, 리셋버튼의 리스너를 등록
        setContentView(R.layout.activity_addalarm);

        Button b = (Button)findViewById(R.id.set);
        b.setOnClickListener (new View.OnClickListener() {
            public void onClick (View v) {
                setAlarm();
            }
        });

        b = (Button)findViewById(R.id.reset);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetAlarm();
            }
        });

        b= (Button)findViewById(R.id.picture) ;
        b.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,ORIGIN);
            }
        });

        //일시 설정 클래스로 현재 시각을 설정
        mDate = (DatePicker)findViewById(R.id.date_picker);
        mDate.init (mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
        mTime = (TimePicker)findViewById(R.id.time_picker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setOnTimeChangedListener(this);
    }
    //지정된시간에 수행할 동작
    private PendingIntent pendingIntent() {

        File photoFile = null;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, COMPARE);


       // startActivityForResult(intentCompare,0);

       // bitCompare = (Bitmap) photoUri.getExtras().get("data");

        bitCompare.compress(Bitmap.CompressFormat.PNG,100,streamCompare);
        arrayCompare = streamCompare.toByteArray();


        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, 0);


        return pi;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);

            bitOrigin = (Bitmap) data.getExtras().get("data");
            bitOrigin.compress(Bitmap.CompressFormat.PNG, 100, streamOrigin);
            arrayOrigin = streamOrigin.toByteArray();
            Log.e("tag", "request0");

        }


    //알람의 설정
    public void setAlarm() {

        repeat = (EditText) findViewById(R.id.repeat);
        repeatTime = Integer.parseInt(repeat.getText().toString());
        int repeatTimeformills = repeatTime * 60 * 60 * 1000;

        mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis()-30000, pendingIntent());
        mManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), repeatTimeformills, pendingIntent());
        SimpleDateFormat sdf = new SimpleDateFormat("H-mm a");
        String strDate = sdf.format(mCalendar.getTime());
        String timeToRepeat = repeat.getText().toString();

        Intent data = new Intent(this, AlarmResultActivity.class);
        data.putExtra("alarm_time", strDate); //string으로 변환한 입력날짜를 전달
        data.putExtra("repeat_time",timeToRepeat);//반복시간전달
        startActivity(data);

    }

    //알람의 해제  //사진찍을시해제
    private void resetAlarm() {
        mManager.cancel(pendingIntent());
    }
    //알람의 설정 시각에 발생하는 인텐트 작성 (여기다가 사진찍는거 구현하면됨)

    //일자 설정 클래스의 상태변화 리스너
    public void onDateChanged (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set (year, monthOfYear, dayOfMonth, mTime.getCurrentHour(), mTime.getCurrentMinute());
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
    }
    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged (TimePicker view, int hourOfDay, int minute) {
        mCalendar.set (mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), hourOfDay, minute);
        Log.i("HelloAlarmActivity",mCalendar.getTime().toString());
    }

}


