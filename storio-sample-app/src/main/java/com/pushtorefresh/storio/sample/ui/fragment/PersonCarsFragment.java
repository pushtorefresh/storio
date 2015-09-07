package com.pushtorefresh.storio.sample.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import timber.log.Timber;

import static com.pushtorefresh.storio.sample.ui.Toasts.safeShowShortToast;
import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class PersonCarsFragment extends BaseFragment {

    // In this sample app we use dependency injection (DI) to keep the code clean
    // Just remember that it's already configured instance of StorIOSQLite from DbModule
    @Inject
    StorIOSQLite storIOSQLite;

    UiStateController uiStateController;

    @InjectView(R.id.person_cars_recycler_view)
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
        ButterKnife.inject(this, view);

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
                .withQuery(PersonsTable.QUERY_ALL)
                .prepare()
                .createObservable()
                .observeOn(mainThread())
                .subscribe(persons -> {
                    if (persons.isEmpty()) {
                        uiStateController.setUiStateEmpty();
                        personCarsAdapter.setPersons(null);
                    } else {
                        // now load all cars
                        int count = persons.size();
                        for (int i=0; i<count; i++) {
                            persons.get(i).getCars(storIOSQLite);
                        }
                        uiStateController.setUiStateContent();
                        personCarsAdapter.setPersons(persons);
                    }
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

        // 1) create person, add new cars to that persons -> save persons
        // 2) create cars and add to existing persons; save persons

        // 1)
        Person person = Person.newPerson("Jennifer");
        person.getCars(storIOSQLite).add(Car.newCar("BMW X3"));
        person.getCars(storIOSQLite).add(Car.newCar("Chevrolet Tahoe"));
        persons.add(person);

        person = Person.newPerson("Sam");
        person.getCars(storIOSQLite).add(Car.newCar("Maserati GranTurismo"));
        persons.add(person);

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
        ////////////////////////////////////////////////////////////////

        persons.clear();

        ////////////////////////////////////////////////////////////////
        // 2)
        persons.add(Person.newPerson("Michael"));
        persons.add(Person.newPerson("Sam"));
        persons.add(Person.newPerson("Betty"));

        // a) now save the persons
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

        // b) now create the cars and save them
        persons.get(0).getCars(storIOSQLite).add(Car.newCar("Toyota Yaris"));
        persons.get(0).getCars(storIOSQLite).add(Car.newCar("VW Golf"));

        persons.get(1).getCars(storIOSQLite).add(Car.newCar("Honda Accord"));
        persons.get(1).getCars(storIOSQLite).add(Car.newCar("Cadillac De Ville Coupe"));
        persons.get(1).getCars(storIOSQLite).add(Car.newCar("Austin Healey 3000 BJ8"));

        persons.get(2).getCars(storIOSQLite).add(Car.newCar("Lotus Elise"));

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
    }
}