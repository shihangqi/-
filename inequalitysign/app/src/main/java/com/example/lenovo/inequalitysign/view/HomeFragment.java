package com.example.lenovo.inequalitysign.view;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.lenovo.inequalitysign.R;
import com.example.lenovo.inequalitysign.Utils;
import com.example.lenovo.inequalitysign.adapter.DiningAdapter;
import com.example.lenovo.inequalitysign.entity.Dining;
import com.example.lenovo.inequalitysign.http.Https;
import com.example.lenovo.inequalitysign.ui.DiningActivity;
import com.example.lenovo.inequalitysign.ui.DiningInformationActivity;
import com.example.lenovo.inequalitysign.ui.SearchActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private String u = "http://10.7.88.35:8090/shop/line_dining";
    private View view;
    private ImageButton btn1;
    private ImageButton btn2;
    private Button btn3;
    private Spinner spinner;
    private ListView lv;
    private DiningAdapter adapter;
    public List<Dining> ls = new ArrayList<>();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter = new DiningAdapter(getActivity().getApplicationContext(),ls);
            lv.setAdapter(adapter);
            setListViewHeightBasedOnChildren(lv);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //设置点击事件
                    Intent intent = new Intent();
                    intent.setClass(getActivity().getApplicationContext(), DiningInformationActivity.class);
                    intent.putExtra("Name",ls.get(i).getName());
                    intent.putExtra("Url",ls.get(i).getUrl());
                    startActivity(intent);
                }
            });

        }
    };

    private void setListViewHeightBasedOnChildren(ListView lv) {
        ListAdapter listAdapter = lv.getAdapter();
        if(listAdapter == null){
            return ;
        }
        int toalheight = 0;
        for(int i = 0 ,len = listAdapter.getCount(); i < len ; i++){
            View view = listAdapter.getView(i,null,lv);
            view.measure(0,0);
            toalheight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = toalheight;
        lv.setLayoutParams(params);

    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_pd:
                    Intent i = new Intent();
                    i.setClass(getActivity().getApplicationContext(), DiningActivity.class);
                    startActivity(i);
                    break;
                case R.id.btn_sys:
                    //实现扫二维码功能
                    break;
                case R.id.btn_search:
                    //跳转到搜寻界面
                    Intent ii = new Intent();
                    ii.setClass(getActivity().getApplicationContext(), SearchActivity.class);
                    startActivity(ii);
                    break;
            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findView();
        setOnClick();
        setaddress();

    }


    /**
     * 一旦地址发生变化，列表就会发生相应的变化
     *
     *
     */

    private void setaddress() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity().getApplicationContext(),
                R.array.languages,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                //获取选择的城市，并记录下来，
                String selected = arg0.getItemAtPosition(arg2).toString();
                Utils.city = selected;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Https http = new Https();
                        NameValuePair pair = new BasicNameValuePair("city",Utils.city);
                        ls = http.setAndGet(u,pair);
                        Message msg = new Message();
                        mHandler.sendMessage(msg);
                    }
                }).start();


            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                //如果首次使用，默认城市是北京，默认获取上次选择的城市，并传递过去获取参数
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Https http = new Https();
                        NameValuePair pair = new BasicNameValuePair("city","3");
                        Log.e("----------1",pair.getValue());
                        ls = http.setAndGet(u,pair);
                        Message msg = new Message();
                        mHandler.sendMessage(msg);
                    }
                }).start();


            }

        });
    }

    /**
     * 设置点击事件
     */
    private void setOnClick() {
        btn1.setOnClickListener(mListener);
        btn2.setOnClickListener(mListener);
        btn3.setOnClickListener(mListener);
    }

    /**
     * 找到view
     */
    private void findView() {
        btn1=(ImageButton)view.findViewById(R.id.btn_pd);
        btn2=(ImageButton)view.findViewById(R.id.btn_sys);
        btn3 = (Button)view.findViewById(R.id.btn_search);
        lv = (ListView)view.findViewById(R.id.lv);
        spinner = (Spinner)view.findViewById(R.id.spinnerid);

    }
}
