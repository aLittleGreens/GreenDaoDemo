package yu.cai.greendaodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.App;
import bean.Book;
import yu.cai.greendao.gen.BookDao;
import yu.cai.greendao.gen.DaoSession;


public class LimitActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "LimitActivity";
    private BookDao bookDao;

    private Button previousPage;
    private Button nextPage;
    private TextView txPage;
    private Button loadData;
    private ListView listView;

    int page = 1;
    long pageCount;
    int countP;
    int num = 8;
    private List<Book> datas = new ArrayList<>();
    private MyAdapter adapter;
    private Spinner spinner;
    private long bookCount;

    private int selectPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit);
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        bookDao = daoSession.getBookDao();

        initView();
        initListener();
    }

    private void initListener() {
        previousPage.setOnClickListener(this);
        nextPage.setOnClickListener(this);
        loadData.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);
    }

    private void initView() {
        spinner = (Spinner) findViewById(R.id.spinner);
        previousPage = (Button) findViewById(R.id.previous_page);
        nextPage = (Button) findViewById(R.id.next_page);
        txPage = (TextView) findViewById(R.id.tx_page);
        loadData = (Button) findViewById(R.id.loadData);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new MyAdapter(datas, this);
        listView.setAdapter(adapter);

    }

    private void setPage() {

        if (bookCount != 0) {
            if (bookCount < num) {
                pageCount = 1;
            } else if (bookCount % num == 0) {
                pageCount = bookCount / num;
            } else {
                pageCount = bookCount / num + 1;
            }
        }

        txPage.setText(page + "/" + (pageCount-1));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.previous_page:

                if (page < 1) {
                    Toast.makeText(this, "page==" + page, Toast.LENGTH_SHORT).show();
                    return;
                }
                page--;
                datas.clear();
                datas.addAll(getbeforeTimeBook(selectPosition,page));

                setPage();
                break;

            case R.id.next_page:

//                if (page > pageCount - 2 && page > 0) {
//                    Toast.makeText(this, "page==" + page, Toast.LENGTH_SHORT).show();
//                    return;
//                }
                page++;
                getTwentyRec(page);
                datas.clear();
                datas.addAll(getbeforeTimeBook(selectPosition,page));
                setPage();
                break;

            case R.id.loadData:
                if (bookDao.count() == 0) {
                    for (int i = 0; i < 110; i++) {
                        Date beforeDate = getBeforeDate(i);
                        Book book = new Book(null, "书:" + i, i, DateTimeUtil.date2Str(beforeDate, DateTimeUtil.DATE_FORMAT_YMDHMS));
                        bookDao.insert(book);
                    }
                }
                break;
        }
    }

    /**
     * 全部
     *
     * @param offset
     * @return
     */
    public List<Book> getTwentyRec(int offset) {

        List<Book> listMsg = bookDao.queryBuilder()
                .offset(offset * num).limit(num).list();
        return listMsg;

    }

    public List<Book> getbeforeTimeBook(int selectPosition, int offset) {
        int time = 0;
        switch (selectPosition){
            case 0://一周
                time = 7;
                break;
            case 1://2周
                time = 14;
                break;

            case 2://一个月
                time = 30;
                break;
            case 3://三个月
                time = 90;
                break;
            case 4://全部
                return getTwentyRec(offset);

        }
        if(page>2){
            Log.e(TAG,"");
        }
        String beforeTime = DateTimeUtil.date2Str(getBeforeDate(time), DateTimeUtil.DATE_FORMAT_YMDHMS);
        List<Book> books = bookDao.queryBuilder().where(BookDao.Properties.Address.ge(beforeTime)).offset(offset * num).limit(num).list();

        return books;
    }

    public long getBeforeCount(int time){
        String beforeTime = DateTimeUtil.date2Str(getBeforeDate(time), DateTimeUtil.DATE_FORMAT_YMDHMS);
        return bookDao.queryBuilder().where(BookDao.Properties.Address.ge(beforeTime)).count();
    };




    /**
     * @param deleteDay
     * @return 得到�?前推deleteDay天的日期
     */
    public static Date getBeforeDate(int deleteDay) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance(); // 得到日历
        calendar.setTime(date);// 把当前时间赋给日�?
        calendar.add(Calendar.DAY_OF_MONTH, 0 - deleteDay);
        return calendar.getTime();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView tv = (TextView) view;
        tv.setTextColor(getResources().getColor(R.color.colorPrimary)); //设置颜色
        Log.e(TAG, "onItemSelected: position" + position);
        selectPosition = position;
        switch (position) {

            case 0://一周
                bookCount = getBeforeCount(7);
                break;
            case 1://2周
                bookCount = getBeforeCount(14);

                break;

            case 2://一个月
                bookCount = getBeforeCount(30);
                break;
            case 3://三个月
                bookCount = getBeforeCount(90);
                break;
            case 4://全部
                bookCount = bookDao.count();
                break;
        }
        page = 0;
        datas.clear();
        datas.addAll(getbeforeTimeBook(selectPosition,page));
        setPage();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
