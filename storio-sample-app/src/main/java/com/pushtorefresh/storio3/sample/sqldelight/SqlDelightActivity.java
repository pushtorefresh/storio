package com.pushtorefresh.storio3.sample.sqldelight;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//
//import com.pushtorefresh.storio3.sample.R;
//import com.pushtorefresh.storio3.sample.SampleApp;
//import com.pushtorefresh.storio3.sample.sqldelight.entities.Customer;
//import com.pushtorefresh.storio3.sample.ui.UiStateController;
//import com.pushtorefresh.storio3.sample.ui.activity.BaseActivity;
//import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import javax.inject.Inject;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Action;
//import io.reactivex.functions.Consumer;
//import io.reactivex.functions.Function;
//import timber.log.Timber;
//
//import static com.pushtorefresh.storio3.sample.sqldelight.SQLUtils.makeReadQuery;
//import static com.pushtorefresh.storio3.sample.sqldelight.SQLUtils.mapFromCursor;
//import static com.pushtorefresh.storio3.sample.ui.Toasts.safeShowShortToast;
//import static io.reactivex.BackpressureStrategy.LATEST;
//import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
//
//public class SqlDelightActivity extends BaseActivity {
//
//    @Inject
//    StorIOSQLite storIOSQLite;
//
//    UiStateController uiStateController;
//
//    @Bind(R.id.customers_recycler_view)
//    RecyclerView recyclerView;
//
//    CustomersAdapter customersAdapter;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sqldelight);
//        SampleApp.get(this).appComponent().inject(this);
//        customersAdapter = new CustomersAdapter(LayoutInflater.from(this));
//
//        ButterKnife.bind(this);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(customersAdapter);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//
//        uiStateController = new UiStateController.Builder()
//                .withLoadingUi(findViewById(R.id.customer_loading_ui))
//                .withErrorUi(findViewById(R.id.customers_error_ui))
//                .withEmptyUi(findViewById(R.id.customers_empty_ui))
//                .withContentUi(recyclerView)
//                .build();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        loadData();
//    }
//
//    void loadData() {
//        uiStateController.setUiStateLoading();
//
//        final Disposable disposable = storIOSQLite
//                .get()
//                .cursor()
//                .withQuery(makeReadQuery(Customer.FACTORY.select_all()))
//                .prepare()
//                .asRxFlowable(LATEST)
//                .map(new Function<Cursor, List<Customer>>() {
//                    @Override
//                    public List<Customer> apply(Cursor cursor) {
//                        return mapFromCursor(cursor, Customer.CURSOR_MAPPER);
//                    }
//                })
//                .observeOn(mainThread())
//                .subscribe(new Consumer<List<Customer>>() {
//                    @Override
//                    public void accept(List<Customer> customers) {
//                        if (customers.isEmpty()) {
//                            uiStateController.setUiStateEmpty();
//                            customersAdapter.setCustomers(Collections.<Customer>emptyList());
//                        } else {
//                            uiStateController.setUiStateContent();
//                            customersAdapter.setCustomers(customers);
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) {
//                        Timber.e(throwable, "loadData()");
//                        uiStateController.setUiStateError();
//                        customersAdapter.setCustomers(Collections.<Customer>emptyList());
//                    }
//                });
//        disposeOnStop(disposable);
//    }
//
//    @OnClick(R.id.customers_empty_ui_add_button)
//    void addContent() {
//        final List<Customer> customers = new ArrayList<Customer>();
//
//        customers.add(Customer.builder().name("Elon").surname("Musk").city("Boring").build());
//        customers.add(Customer.builder().name("Jake").surname("Wharton").city("Pittsburgh").build());
//
//        List<ContentValues> contentValues = new ArrayList<ContentValues>(customers.size());
//        for (Customer customer : customers) {
//            contentValues.add(Customer.FACTORY.marshal(customer).asContentValues());
//        }
//
//        disposeOnStop(storIOSQLite
//                .put()
//                .contentValues(contentValues)
//                .withPutResolver(Customer.CV_PUT_RESOLVER)
//                .prepare()
//                .asRxCompletable()
//                .observeOn(mainThread())
//                .subscribe(
//                        new Action() {
//                            @Override
//                            public void run() {
//                                // no impl required
//                            }
//                        },
//                        new Consumer<Throwable>() {
//                            @Override
//                            public void accept(Throwable throwable) {
//                                safeShowShortToast(SqlDelightActivity.this, R.string.common_error);
//                            }
//                        }));
//    }
//}
//