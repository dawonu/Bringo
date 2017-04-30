package com.example.bringo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.bringo.dataretriever.TravelListGetter;
import com.example.bringo.helperclasses.TravelCategory;
import com.example.bringo.helperclasses.TravelItem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CreateDestination2Activity extends AppCompatActivity {

    private Toolbar myToolbar;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            return true;
        }
    };

    private List<TravelCategory> category;
    private List<List<TravelItem>> travelList;
    private List<HashMap<String, Object>> list = null;

    private TravelCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_destination2);

        myToolbar = (Toolbar) findViewById(R.id.destination_toolbar);
        myToolbar.setTitle("Add destination 2/2");
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        new TravelListGetter(this);

        Button addBtn = (Button) findViewById(R.id.add_item_btn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                // get user input item name
                String inputItem = ((EditText)findViewById(R.id.input_item)).getText().toString();
                EditText editText = (EditText)findViewById(R.id.input_item);
                editText.setText("");

                adapter.addItem(inputItem);

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // to make the Toolbar has the functionality of Menu，do not delete
        getMenuInflater().inflate(R.menu.top_bar_save, menu);
        return true;
    }

    public void renderPage(List<TravelCategory> title, List<List<TravelItem>> content) {
        this.category = title;
        this.travelList = content;
//        System.out.println("out" +title);
//        System.out.println("out"+content);
        category.add(new TravelCategory("20", "Others"));
        travelList.add(new LinkedList<TravelItem>());

        list = new LinkedList<>();
        for (int i = 0; i < category.size(); i++) {
            HashMap<String, Object> map1 = new HashMap<>();
            map1.put("name", category.get(i));
            map1.put("status", false);
            list.add(map1);
            for (int j = 0; j < travelList.get(i).size(); j++) {
                HashMap<String, Object> map2 = new HashMap<>();
                map2.put("name", travelList.get(i).get(j));
                map2.put("status", false);
                list.add(map2);
            }
        }

        adapter = new TravelCategoryAdapter(this, list);
        for (int i = 0; i < category.size(); i++) {
            adapter.addSeparatorItem(category.get(i).toString());
            for (int j = 0; j < travelList.get(i).size(); j++) {
                adapter.addItem(travelList.get(i).get(j).toString());
            }
        }

        ListView listView = (ListView) findViewById(R.id.travel_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                TravelCategoryAdapter.ViewHolder holder = (TravelCategoryAdapter.ViewHolder) view.getTag();
//                holder.checkBox.toggle();
                if ((boolean) list.get(position).get("status") == true) {
                    list.get(position).put("status", false);
                } else {
                    list.get(position).put("status", true);
                }
                TravelCategoryAdapter.isSelected.put(position, holder.checkBox.isChecked());
            }

        });
    }

}
