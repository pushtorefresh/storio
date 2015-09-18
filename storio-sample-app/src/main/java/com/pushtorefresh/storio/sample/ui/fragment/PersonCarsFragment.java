package com.pushtorefresh.storio.sample.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pushtorefresh.storio.sample.R;
import com.pushtorefresh.storio.sample.SampleApp;
import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.tables.PersonsTable;
import com.pushtorefresh.storio.sample.ui.DividerItemDecoration;
import com.pushtorefresh.storio.sample.ui.UiStateController;
import com.pushtorefresh.storio.sample.ui.adapter.PersonCarsAdapter;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import timber.log.Timber;

import static com.pushtorefresh.storio.sample.ui.Toasts.safeShowShortToast;
import static java.util.Arrays.asList;
import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class PersonCarsFragment extends BaseFragment {

    private final String TAG = this.getClass().getSimpleName();

    // In this sample app we use dependency injection (DI) to keep the code clean
    // Just remember that it's already configured instance of StorIOSQLite from DbModule
    @Inject
    StorIOSQLite storIOSQLite;

    UiStateController uiStateController;

    @Bind(R.id.person_cars_recycler_view)
    RecyclerView recyclerView;

    PersonCarsAdapter personCarsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApp.get(getActivity()).appComponent().inject(this);
        personCarsAdapter = new PersonCarsAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person_cars, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(personCarsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        uiStateController = new UiStateController.Builder()
                .withLoadingUi(view.findViewById(R.id.person_cars_loading_ui))
                .withErrorUi(view.findViewById(R.id.person_cars_error_ui))
                .withEmptyUi(view.findViewById(R.id.person_cars_empty_ui))
                .withContentUi(recyclerView)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadData();
    }

    void reloadData() {
        uiStateController.setUiStateLoading();

        final Subscription subscription = storIOSQLite
                .get()
                .listOfObjects(Person.class)
                .withQuery(Query.builder()
                        .table(PersonsTable.TABLE_NAME)
                        .build())
                .prepare()
                .createObservable()
                .observeOn(mainThread())
                .subscribe(persons -> {
                    Log.d(TAG, "before - get");
                    if (persons.isEmpty()) {
                        uiStateController.setUiStateEmpty();
                        personCarsAdapter.setPersons(null);
                    } else {
                        uiStateController.setUiStateContent();
                        personCarsAdapter.setPersons(persons);
                    }
                    Log.d(TAG, "after - get");
                }, throwable -> {
                    Timber.e(throwable, "reloadData()");
                    uiStateController.setUiStateError();
                    personCarsAdapter.setPersons(null);
                });

        unsubscribeOnStop(subscription);
    }

    // TODO add examples of person-cars
    @OnClick(R.id.person_cars_empty_ui_add_person_cars_button)
    void addPersonCars() {
        final List<Person> persons = new ArrayList<>();

        Person person = new Person
                .Builder("Jennifer")
                .cars(asList(new Car.Builder("BMW X3").build(), new Car.Builder("Chevrolet Tahoe").build()))
                .build();
        persons.add(person);

        person = new Person
                .Builder("Sam")
                .cars(asList(new Car.Builder("Maserati GranTurismo").build(),
                        new Car.Builder("Cadillac De Ville Coupe").build(),
                        new Car.Builder("Austin Healey 3000 BJ8").build()))
                .build();
        persons.add(person);

        person = new Person
                .Builder("person x")
                .cars(asList(new Car.Builder("car x").build(), new Car.Builder("car y").build(), new Car.Builder("car z").build()))
                .build();

        // huge example
        for (int i=0; i<1000; i++) {
//            person = new Person(null, "person "+i,
//                    asList(new Car.Builder("car "+i).build(), new Car.Builder("car "+i).build(), new Car.Builder("car "+i).build())
//            );
            persons.add(person);
        }

        Log.d(TAG, "before - put");
        storIOSQLite
                .put()
                .objects(persons)
                .prepare()
                .createObservable()
                .observeOn(mainThread()) // Remember, all Observables in StorIO already subscribed on Schedulers.io(), you just need to set observeOn()
                .subscribe(new Observer<PutResults<Person>>() {
                    @Override
                    public void onError(Throwable e) {
                        safeShowShortToast(getActivity(), R.string.person_cars_add_error_toast);
                    }

                    @Override
                    public void onNext(PutResults<Person> putResults) {
                        // After successful Put Operation our subscriber in reloadData() will receive update!
                    }

                    @Override
                    public void onCompleted() {
                        // no impl required
                    }
                });
        Log.d(TAG, "after - put");
    }
}