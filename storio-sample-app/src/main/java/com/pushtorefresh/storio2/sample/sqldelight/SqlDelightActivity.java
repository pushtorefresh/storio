package com.pushtorefresh.storio2.sample.sqldelight;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.pushtorefresh.storio2.sample.R;
import com.pushtorefresh.storio2.sample.SampleApp;
import com.pushtorefresh.storio2.sample.sqldelight.entities.Customer;
import com.pushtorefresh.storio2.sample.ui.UiStateController;
import com.pushtorefresh.storio2.sample.ui.activity.BaseActivity;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

import static com.pushtorefresh.storio2.sample.sqldelight.SQLUtils.makeReadQuery;
import static com.pushtorefresh.storio2.sample.sqldelight.SQLUtils.mapFromCursor;
import static com.pushtorefresh.storio2.sample.ui.Toasts.safeShowShortToast;
import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class SqlDelightActivity extends BaseActivity {

    @Inject
    StorIOSQLite storIOSQLite;

    UiStateController uiStateController;

    @Bind(R.id.customers_recycler_view)
    RecyclerView recyclerView;

    CustomersAdapter customersAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqldelight);
        SampleApp.get(this).appComponent().inject(this);
        customersAdapter = new CustomersAdapter(LayoutInflater.from(this));

        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customersAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        uiStateController = new UiStateController.Builder()
                .withLoadingUi(findViewById(R.id.customer_loading_ui))
                .withErrorUi(findViewById(R.id.customers_error_ui))
                .withEmptyUi(findViewById(R.id.customers_empty_ui))
                .withContentUi(recyclerView)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    void loadData() {
        uiStateController.setUiStateLoading();

        final Subscription subscription = storIOSQLite
                .get()
                .cursor()
                .withQuery(makeReadQuery(Customer.FACTORY.select_all()))
                .prepare()
                .asRxObservable()
                .map(new Func1<Cursor, List<Customer>>() {
                    @Override
                    public List<Customer> call(Cursor cursor) {
                        return mapFromCursor(cursor, Customer.CURSOR_MAPPER);
                    }
                })
                .observeOn(mainThread())
                .subscribe(new Action1<List<Customer>>() {
                    @Override
                    public void call(List<Customer> customers) {
                        if (customers.isEmpty()) {
                            uiStateController.setUiStateEmpty();
                            customersAdapter.setCustomers(Collections.<Customer>emptyList());
                        } else {
                            uiStateController.setUiStateContent();
                            customersAdapter.setCustomers(customers);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable, "loadData()");
                        uiStateController.setUiStateError();
                        customersAdapter.setCustomers(Collections.<Customer>emptyList());
                    }
                });
        unsubscribeOnStop(subscription);
    }

    @OnClick(R.id.customers_empty_ui_add_button)
    void addContent() {
        final List<Customer> customers = new ArrayList<Customer>();

        customers.add(Customer.builder().name("Elon").surname("Musk").city("Boring").build());
        customers.add(Customer.builder().name("Jake").surname("Wharton").city("Pittsburgh").build());

        List<ContentValues> contentValues = new ArrayList<ContentValues>(customers.size());
        for (Customer customer : customers) {
            contentValues.add(Customer.FACTORY.marshal(customer).asContentValues());
        }

        storIOSQLite
                .put()
                .contentValues(contentValues)
                .withPutResolver(Customer.CV_PUT_RESOLVER)
                .prepare()
                .asRxCompletable()
                .observeOn(mainThread())
                .subscribe(
                        new Action0() {
                            @Override
                            public void call() {
                                // no impl required
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                safeShowShortToast(SqlDelightActivity.this, R.string.common_error);
                            }
                        });
    }
}
