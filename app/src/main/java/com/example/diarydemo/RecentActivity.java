package com.example.diarydemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.N)
public class RecentActivity extends AppCompatActivity {

    //控件集
    TextView total,summaryTitle,summary;

    ArrayList<Diary> diaryList = new ArrayList<>();     //定义ArrayList用于存放来自数据库的日记数据

    //使用DiaryDatabaseHelper操作数据库
    private DiaryDatabaseHelper dbHelper;

    //根据系统日期默认设置日记的日期
    Calendar calendar = Calendar.getInstance();
    public int system_year = calendar.get(Calendar.YEAR);
    public int system_month = calendar.get(Calendar.MONTH) + 1;        //在Calendar中封装的月份是以0为首月的，因此要+1
    public int system_day = calendar.get(Calendar.DAY_OF_MONTH);

    //对于不同情况用户的建议
    final private String happyAdv = "您最近的状态比较乐观。生活对您来说充满着乐趣，所以好好把握现在的时光吧，因为唯有这样，以后的你才不至于后悔。";
    final private String angryAdv = "您最近的心情非常糟糕。或许闲暇下来一个人听听音乐，或是静下心来读一本书，亦或是走出屋去散散心都是不错的选择，您不必过分纠结那些令您烦心的事，因为他们终将烟消云散。";
    final private String sadAdv = "您最近的心情很低落。人生处处有低谷，可是低谷也总有通往山巅的路，不管它是多么蜿蜒无尽，亦或是何等隐隐若现，它就在那，等着您，所以请尽可能放下使您伤心的事，去找寻新的起点吧。";
    final private String scaredAdv = "您最近常常处于恐惧之中。是有什么事情使您后悔，还是碰上了一些麻烦？但是无论怎样来说，勇于面对总好过盲目逃避，祝您好运。";
    final private String anxiousAdv = "您最近很忧愁。朋友，请记住，人生在世，苦多乐少，多记住一些美好的事就好了，至于忧愁呢，不必担心，时间终究会让它烟消云散。";
    final private String mixAdv = "您最近的心情很复杂，您最近经历的事有些多，大起大落就像海上的波澜一般，适当放松一下，或许你会看到一个更好的自己。";
    final private String noneAdv = "您最近没有对我说过任何事，或许您最近过的很平淡，愿您事事如意，在您愿意诉说的时候我永远乐于倾听。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        //绑定控件
        total = (TextView) findViewById(R.id.activity_recent_totalamount);
        summaryTitle = (TextView) findViewById(R.id.activity_recent_summarytitle);
        summary = (TextView) findViewById(R.id.activity_recent_summary);

        total.setAlpha(0);
        summaryTitle.setAlpha(0);
        summary.setAlpha(0);


        //从数据库中取出近一周的日记
        readDataAboutThisWeek(diaryList);

        int[] diaryAmount = new int[]{0, 0, 0, 0, 0, 0};      //{ 日记总数, 开心篇数, 愤怒篇数, 悲伤篇数, 惊恐篇数, 忧愁篇数 };

        //分析数据填写数目
        for (Diary d : diaryList) {
            diaryAmount[0]++;   //总数加一
            switch (d.getMood()) {
                case "开心":
                    diaryAmount[1]++;
                    break;
                case "愤怒":
                    diaryAmount[2]++;
                    break;
                case "悲伤":
                    diaryAmount[3]++;
                    break;
                case "惊恐":
                    diaryAmount[4]++;
                    break;
                default:
                    diaryAmount[5]++;
            }
        }

        //根据日记数目分析用户描述
        String user_description = new String();

        if (diaryAmount[0] > 7) {
            user_description = "是一个多愁善感又善于记录的人";
        } else if (diaryAmount[0] == 7) {
            user_description = "有天天记日记的良好习惯";
        } else if (diaryAmount[0] < 7 && diaryAmount[0] > 3) {
            user_description = "是一个在乎生活而又不愿多说的人";
        } else {
            user_description = "很少在这里诉说";
        }


        //设置 日记数目 控件的内容

        String LongText = new String();

        if (diaryAmount[0] == 0) {
            LongText += "<font color='#000000' >本周您没有留下任何日记。</font>";
        } else {

            //总数
            LongText += "<font color=\'#000000\' >本周您在管家这里一共留下了</font>" +
                    "<font color=\'#990000\' ><big><big><big><big> " + String.valueOf(diaryAmount[0]) + " </big></big></big></big></font>" +
                    "<font color='#000000' >篇日记</font>";

            //描述
            LongText += "<font color=\'#000000\' >，您</font>" +
                    "<font color=\'#990000\' ><big><big> " + user_description + " </big></big></font>";

            if (diaryAmount[1] != 0) {
                //开心
                LongText += "<font color=\'#000000\' >，在您留下的日记中，有</font>" +
                        "<font color=\'#990000\' ><big><big><big><big> " + String.valueOf(diaryAmount[1]) + " </big></big></big></big></font>" +
                        "<font color='#000000' >篇为开心的记忆</font>";
            }

            if (diaryAmount[2] != 0) {
                //愤怒
                LongText += "<font color=\'#000000\' >，有</font>" +
                        "<font color=\'#990000\' ><big><big><big><big> " + String.valueOf(diaryAmount[2]) + " </big></big></big></big></font>" +
                        "<font color='#000000' >篇记录着您的不满</font>";
            }

            if (diaryAmount[3] != 0) {
                //悲伤
                LongText += "<font color=\'#000000\' >，</font>" +
                        "<font color=\'#990000\' ><big><big><big><big> " + String.valueOf(diaryAmount[3]) + " </big></big></big></big></font>" +
                        "<font color='#000000' >篇记录着您不愿回忆的经历</font>";
            }

            if (diaryAmount[4] != 0) {
                //恐惧
                LongText += "<font color=\'#000000\' >，还有</font>" +
                        "<font color=\'#990000\' ><big><big><big><big> " + String.valueOf(diaryAmount[4]) + " </big></big></big></big></font>" +
                        "<font color='#000000' >篇写下了您的恐惧</font>";
            }

            if (diaryAmount[5] != 0) {
                //忧愁
                LongText += "<font color=\'#000000\' >,</font>" +
                        "<font color=\'#990000\' ><big><big><big><big> " + String.valueOf(diaryAmount[5]) + " </big></big></big></big></font>" +
                        "<font color='#000000' >篇包含着您的忧愁</font>";
            }

            //。
            LongText += "<font color='#000000' >。<big><big><big><big> </big></big></big></big></font>";


        }


        total.setText(Html.fromHtml(LongText));


        //设置 用户总结 控件的内容

        int isNull = 0;                 //记录为0的种类
        int isNotNull = 0;              //记录不为0的种类
        int summaryNotNull = 0;         //记录控件内容是否已填写（0-未填写，1-已填写）

        for (int i = 1; i <= 5; i++) {
            if (diaryAmount[i] != 0) {
                isNotNull++;
            } else {
                isNull++;
            }
        }

        if (isNotNull >= 3) {
            //种类大于等于3
            summary.setText(Html.fromHtml("<font color='#990000' > <big> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp " + mixAdv + " </big> </font>"));
            summaryNotNull = 1;
            //picture.setImageResource(R.drawable.recent_mix);
        } else {
            if (isNotNull == 0) {
                //什么都没记
                summary.setText(Html.fromHtml("<font color='#990000' > <big> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp " + noneAdv + " </big> </font>"));
                summaryNotNull = 1;
                //picture.setImageResource(R.drawable.recent_null);
            } else {
                //记1-2类

                if (diaryAmount[1] > diaryAmount[0] / 2) {
                    //开心占大半
                    summary.setText(Html.fromHtml("<font color='#990000' > <big> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp " + happyAdv + " </big> </font>"));
                    summaryNotNull = 1;
                    //picture.setImageResource(R.drawable.recent_happy);
                } else if (diaryAmount[2] > diaryAmount[0] / 2) {
                    //愤怒占大半
                    summary.setText(Html.fromHtml("<font color='#990000' > <big> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp " + angryAdv + " </big> </font>"));
                    summaryNotNull = 1;
                    //picture.setImageResource(R.drawable.recent_angry);
                } else if (diaryAmount[3] > diaryAmount[0] / 2) {
                    //悲伤占大半
                    summary.setText(Html.fromHtml("<font color='#990000' > <big> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp " + sadAdv + " </big> </font>"));
                    summaryNotNull = 1;
                    //picture.setImageResource(R.drawable.recent_sad);
                } else if (diaryAmount[4] > diaryAmount[0] / 2) {
                    //惊恐占大半
                    summary.setText(Html.fromHtml("<font color='#990000' > <big> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp " + scaredAdv + " </big> </font>"));
                    summaryNotNull = 1;
                    //picture.setImageResource(R.drawable.recent_scared);
                } else if (diaryAmount[5] > diaryAmount[0] / 2) {
                    //忧愁占大半
                    summary.setText(Html.fromHtml("<font color='#990000' > <big> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp " + anxiousAdv + " </big> </font>"));
                    summaryNotNull = 1;
                    //picture.setImageResource(R.drawable.recent_anxious);
                }

                if (summaryNotNull == 0) {
                    //以上都不符合
                    summary.setText(Html.fromHtml("<font color='#990000' > <big> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp " + mixAdv + " </big> </font>"));
                    summaryNotNull = 1;
                    //picture.setImageResource(R.drawable.recent_mix);
                }

            }


        }

        //添加渐变载入动画
        final Animation show = AnimationUtils.loadAnimation(RecentActivity.this, R.anim.alpha);

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        total.setAlpha(1);
                        total.startAnimation(show);
                        summaryTitle.setAlpha(1);
                        summaryTitle.startAnimation(show);
                        summary.setAlpha(1);
                        summary.startAnimation(show);
                        break;
                }
                super.handleMessage(msg);
            }
        };






        //设置流水载入
        Timer timer = new Timer();


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);

            }
        };
        timer.schedule(task, 1000);



    }


    public void readDataAboutThisWeek(ArrayList<Diary> list){
        //近一周
        list.clear();      //清空上次的数据

        //从数据库中取出日记到数组
        dbHelper = new DiaryDatabaseHelper(RecentActivity.this, "Diary.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int year = system_year;
        int month = system_month;
        int day = system_day;
        //Date date = new Date(year, month, day);

        for(int i = 0 ; i < 7 + 1 ; i++){       //往前推7天

            Cursor cursor = db.query("diary", null, "year='" + year + "' and " + "month='" + month + "' and " +
                    "day='" + day + "'", null, null, null, null);

            if(cursor.moveToLast()){

                do{
                    Diary newDiary = new Diary();
                    newDiary.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    newDiary.setDate(new Date(cursor.getInt(cursor.getColumnIndex("year")), cursor.getInt(cursor.getColumnIndex("month")), cursor.getInt(cursor.getColumnIndex("day"))));
                    newDiary.setMood(cursor.getString(cursor.getColumnIndex("mood")));
                    newDiary.setDetail(cursor.getString(cursor.getColumnIndex("detail")));

                    list.add(newDiary);

                }while(cursor.moveToPrevious());

            }
            cursor.close();




            /* 日期递变规律 */
            if(day == 1){
                switch(month - 1){
                    case Calendar.JANUARY:
                        year--;
                        month = 12;
                        day = 31;
                        break;
                    case Calendar.FEBRUARY:
                        month--;
                        day = 31;
                        break;
                    case  Calendar.MARCH:
                        month--;
                        if(year % 4 == 0 && year % 100 == 0 && year % 400 != 0){
                            //闰年
                            day = 29;
                        }else{
                            //平年
                            day = 28;
                        }
                        break;
                    case Calendar.APRIL:
                        month--;
                        day = 31;
                        break;
                    case Calendar.MAY:
                        month--;
                        day = 30;
                        break;
                    case Calendar.JUNE:
                        month--;
                        day = 31;
                        break;
                    case Calendar.JULY:
                        month--;
                        day = 30;
                        break;
                    case  Calendar.AUGUST:
                        month--;
                        day = 31;
                        break;
                    case Calendar.SEPTEMBER:
                        month--;
                        day = 31;
                        break;
                    case Calendar.OCTOBER:
                        month--;
                        day = 30;
                        break;
                    case Calendar.NOVEMBER:
                        month--;
                        day = 31;
                        break;
                    case Calendar.DECEMBER:
                        month--;
                        day = 30;
                        break;
                }


            }else {
                day--;
            }
                        /* 日期递变规律 */



        }


    }


}
