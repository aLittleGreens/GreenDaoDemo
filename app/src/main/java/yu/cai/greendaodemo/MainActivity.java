package yu.cai.greendaodemo;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import app.App;
import bean.Book;
import yu.cai.greendao.gen.BookDao;
import yu.cai.greendao.gen.DaoSession;

public class MainActivity extends AppCompatActivity {
    private  static final String TAG = "MainActivity";
    private DaoSession daoSession;
    private BookDao bookDao;

    private LinearLayout activityMain;
    private Toolbar toolBar;
    private EditText nameEdit;
    private EditText salaryEdit;
    private Button addBt;
    private Button deleteBt;
    private Button updateBt;
    private TextView emptyView;
    private ListView listView;
    private MyAdapter myAdapter;
    private List<Book> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up toolbar as actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        //query all employee to show in the list
        daoSession = ((App) getApplication()).getDaoSession();
        bookDao = daoSession.getBookDao();
        list = bookDao.queryBuilder().list();
        findView();
        findListener();

    }
    private void findView() {
        activityMain = (LinearLayout) findViewById(R.id.activity_main);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        nameEdit = (EditText) findViewById(R.id.name_edit);
        salaryEdit = (EditText) findViewById(R.id.salary_edit);
        addBt = (Button) findViewById(R.id.add_bt);
        deleteBt = (Button) findViewById(R.id.delete_bt);
        updateBt = (Button) findViewById(R.id.update_bt);
        emptyView = (TextView) findViewById(R.id.empty_view);
        listView = (ListView) findViewById(R.id.list_view);
        myAdapter = new MyAdapter(list, this);
        listView.setAdapter(myAdapter);
    }

    private void findListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteAlertDialog(list.get(position).getId());
            }
        });
    }

    /**
     * @param id id of employee in database table
     */
    private void showDeleteAlertDialog(final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteEmployee(id);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.create().show();
    }

    public void addBook(View view) {
        Book book = new Book(null, nameEdit.getText().toString(), Float.parseFloat(salaryEdit.getText().toString()), "IFREECOMM");
        bookDao.insert(book);
        refleshList();
        cleanEditText();
    }

    public void updateBook(View view) {
        Book book = bookDao.queryBuilder().where(BookDao.Properties.Name.eq(nameEdit.getText().toString().trim())).build().unique();
        book.setPrice(Float.parseFloat(salaryEdit.getText().toString()));
        bookDao.update(book);
        refleshList();
    }

    public void deleteBook(View view) {
        bookDao.deleteAll();
        refleshList();
    }

    public void refleshList() {
        list.clear();
        list.addAll(bookDao.queryBuilder().orderDesc(BookDao.Properties.Price).build().list());
        myAdapter.notifyDataSetChanged();
    }


    private void deleteEmployee(long id) {
        bookDao.deleteByKey(id);
        refleshList();
    }

    //set EditText to null
    private void cleanEditText() {
        nameEdit.setText("");
        salaryEdit.setText("");
    }

    //show the queryResult in the listView
    @Override
    protected void onNewIntent(Intent intent) {
        Log.e(TAG,"onNewIntent");
        // get and process search query here
        handlerIntent(intent);
        super.onNewIntent(intent);
    }

    private void handlerIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            list.clear();
            list.addAll(bookDao.queryBuilder().where(BookDao.Properties.Name.eq(query)).list());
            myAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        //set searchView configuration to search something
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //show appropriate employee when query text changed
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG,"onQueryTextSubmit");
                if (query.equals("")) {
                    listView.setAdapter(myAdapter);
                    refleshList();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG,"onQueryTextChange");
                if (newText.equals("")) {
                    listView.setAdapter(myAdapter);
                    refleshList();
                    return true;
                } else {
                    return false;
                }
            }
        });

        //show all employee in the list when searchView collapse
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.e(TAG,"onMenuItemActionExpand");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.e(TAG,"onMenuItemActionCollapse");
                listView.setAdapter(myAdapter);
                refleshList();
                return true;
            }
        });

        return true;
    }


}
