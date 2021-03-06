package com.example.elemei.view.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.elemei.R;
import com.example.elemei.view.activity.ShoppingCarActivity;
import com.example.elemei.view.adapter.StoreAdapter;
import com.example.elemei.view.activity.LoginActivity;
import com.example.elemei.view.net.StoreCall;
import com.example.elemei.view.pojo.Store;
import com.example.elemei.view.pojo.StoreBean;
import com.example.elemei.view.util.AppBarStateChangeListenner;
import com.example.elemei.view.util.Const;
import com.example.elemei.view.util.MyItemDecoration;
import com.example.elemei.view.util.NetUtils;
import com.example.elemei.view.util.StatusBarUtils;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private RecyclerView recyclerView;
    private StoreCall storeCall = new StoreCall();
    private List<Store> current_stores = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private StoreAdapter storeAdapter;
    private MyItemDecoration myItemDecoration = new MyItemDecoration(0, 12);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }


    @Override
    public void onResume() {
        if (Const.customer_id > 0) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        if (!NetUtils.isNetConnected(getActivity())) {
            Toast.makeText(getActivity(), "无网络连接", Toast.LENGTH_SHORT).show();
        } else {
            getStores();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_message:
                if (Const.customer_id == 0) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    Toast.makeText(getActivity(), "功能还在开发中", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_login:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.view_search:
                if (Const.customer_id == 0) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    Toast.makeText(getActivity(), "功能还在开发", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_shopping_car:
                if (Const.customer_id == 0) {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getActivity(), ShoppingCarActivity.class));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //渲染stores 数据
    public void getStores() {
        storeCall.selectAll(new Callback<StoreBean>() {
            @Override
            public void onResponse(Call<StoreBean> call, Response<StoreBean> response) {
                if (response.body().isResult && response.body().getResult() != null) {
                    List<Store> stores = new ArrayList<>();
                    List<Store> noodle_stores = new ArrayList<>();
                    List<Store> rice_stores = new ArrayList<>();
                    List<Store> pickled_stores = new ArrayList<>();
                    List<Store> drink_stores = new ArrayList<>();
                    for (Store store : response.body().getResult()) {
                        if (store.getClassification().equals("面食")) {
                            noodle_stores.add(store);
                        } else if (store.getClassification().equals("甜点饮品")) {
                            drink_stores.add(store);
                        } else if (store.getClassification().equals("凉卤")) {
                            pickled_stores.add(store);
                        } else {
                            rice_stores.add(store);
                        }
                        stores.add(store);
                    }
                    recyclerView = getView().findViewById(R.id.rv_fragment_main_store);
                    storeAdapter = new StoreAdapter(R.layout.store);
                    storeAdapter.setList(stores);
                    recyclerView.setAdapter(storeAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    View footer = LayoutInflater.from(getActivity()).inflate(R.layout.footer_view, recyclerView, false);
                    View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_view, recyclerView, false);
                    header.findViewById(R.id.view_header_noodle).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (current_stores != noodle_stores) {
                                storeAdapter.setList(noodle_stores);
                                current_stores = noodle_stores;
                            }
                        }
                    });
                    header.findViewById(R.id.view_header_pickled_food).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (current_stores != pickled_stores) {
                                storeAdapter.setList(pickled_stores);
                                current_stores = pickled_stores;
                            }
                        }
                    });
                    header.findViewById(R.id.view_header_rice).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (current_stores != rice_stores) {
                                storeAdapter.setList(rice_stores);
                                current_stores = rice_stores;
                            }
                        }
                    });
                    header.findViewById(R.id.view_header_all).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (current_stores != stores) {
                                storeAdapter.setList(stores);
                                current_stores = stores;
                            }
                        }
                    });
                    header.findViewById(R.id.view_header_drink).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (current_stores != drink_stores) {
                                storeAdapter.setList(drink_stores);
                                current_stores = drink_stores;
                            }
                        }
                    });
                    storeAdapter.addFooterView(footer);
                    storeAdapter.addHeaderView(header);
                    if (recyclerView.getItemDecorationCount() == 0) {
                        recyclerView.addItemDecoration(myItemDecoration);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setBackgroundResource(R.color.grey);
                }
            }

            @Override
            public void onFailure(Call<StoreBean> call, Throwable t) {
                Toast.makeText(getActivity(), "系统错误", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setBackgroundResource(R.mipmap.net_err);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (!NetUtils.isNetConnected(getActivity())) {
            Toast.makeText(getActivity(), "无网络连接", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            getStores();
        }
    }

    //初始化view
    public void init(){
        getView().findViewById(R.id.tv_login).setOnClickListener(this);
        getView().findViewById(R.id.iv_message).setOnClickListener(this);
        getView().findViewById(R.id.view_search).setOnClickListener(this);
        getView().findViewById(R.id.iv_shopping_car).setOnClickListener(this);
        view = getView().findViewById(R.id.view_login);
        swipeRefreshLayout = getView().findViewById(R.id.refresh_main);
        swipeRefreshLayout.setOnRefreshListener(this);
        View statusbar = getView().findViewById(R.id.view_statusbar);
        StatusBarUtils.setStatusBarHeight(getActivity(),statusbar);
        AppBarLayout appBarLayout = getView().findViewById(R.id.abl_main);
        //添加折叠的监听
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListenner() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.IDLE) {
                    statusbar.setBackgroundColor(getResources().getColor(R.color.blue_trans));
                    appBarLayout.setBackgroundColor(getResources().getColor(R.color.blue_trans));
                    swipeRefreshLayout.setEnabled(false);
                }
                if (state == State.EXPANDED) {
                    statusbar.setBackgroundColor(getResources().getColor(R.color.blue));
                    appBarLayout.setBackgroundColor(getResources().getColor(R.color.blue));
                    swipeRefreshLayout.setEnabled(true);
                }
                if (state == State.COLLAPSED) {
                    statusbar.setBackgroundColor(getResources().getColor(R.color.white));
                    appBarLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
    }
}
