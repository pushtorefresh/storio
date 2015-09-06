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
import com.pushtorefresh.storio.sample.db.entities.Ant;
import com.pushtorefresh.storio.sample.db.entities.Queen;
import com.pushtorefresh.storio.sample.db.tables.QueensTable;
import com.pushtorefresh.storio.sample.ui.DividerItemDecoration;
import com.pushtorefresh.storio.sample.ui.UiStateController;
import com.pushtorefresh.storio.sample.ui.adapter.QueenAntsAdapter;
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
import rx.functions.Action1;
import timber.log.Timber;

import static com.pushtorefresh.storio.sample.ui.Toasts.safeShowShortToast;
import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class QueenAntsFragment extends BaseFragment {

    // In this sample app we use dependency injection (DI) to keep the code clean
    // Just remember that it's already configured instance of StorIOSQLite from DbModule
    @Inject
    StorIOSQLite storIOSQLite;

    UiStateController uiStateController;

    @InjectView(R.id.queen_ants_recycler_view)
    RecyclerView recyclerView;

    QueenAntsAdapter queenAntsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApp.get(getActivity()).appComponent().inject(this);
        queenAntsAdapter = new QueenAntsAdapter();
        queenAntsAdapter.storIOSQLite = storIOSQLite;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_queen_ants, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(queenAntsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        uiStateController = new UiStateController.Builder()
                .withLoadingUi(view.findViewById(R.id.queen_ants_loading_ui))
                .withErrorUi(view.findViewById(R.id.queen_ants_error_ui))
                .withEmptyUi(view.findViewById(R.id.queen_ants_empty_ui))
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
                .listOfObjects(Queen.class)
                .withQuery(QueensTable.QUERY_ALL)
                .prepare()
                .createObservable() // it will be subscribed to changes in tweets table!
//                .delay(1, SECONDS) // for better User Experience :) Actually, StorIO is so fast that we need to delay emissions (it's a joke, or not)
                .observeOn(mainThread())
                .subscribe(new Action1<List<Queen>>() {
                    @Override
                    public void call(List<Queen> queens) {
                        // Remember: subscriber will automatically receive updates
                        // Of tables from Query (tweets table in our case)
                        // This makes your code really Reactive and nice!

                        // We guarantee, that list of objects will never be null (also we use @NonNull/@Nullable)
                        // So you just need to check if it's empty or not
                        if (queens.isEmpty()) {
                            uiStateController.setUiStateEmpty();
                            queenAntsAdapter.setQueens(null);
                        } else {
                            uiStateController.setUiStateContent();
                            queenAntsAdapter.setQueens(queens);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        // In cases when you are not sure that query will be successful
                        // You can prevent crash of the application via error handler
                        Timber.e(throwable, "reloadData()");
                        uiStateController.setUiStateError();
                        queenAntsAdapter.setQueens(null);
                    }
                });

        // Preventing memory leak (other Observables: Put, Delete emit result once so memory leak won't live long)
        // Because rx.Observable from Get Operation is endless (it watches for changes of tables from query)
        // You can easily create memory leak (in this case you'll leak the Fragment and all it's fields)
        // So please, PLEASE manage your subscriptions
        // We suggest same mechanism via storing all subscriptions that you want to unsubscribe
        // In something like CompositeSubscription and unsubscribe them in appropriate moment of component lifecycle
        unsubscribeOnStop(subscription);
    }

    // TODO add examples of queen-ants
    @OnClick(R.id.queen_ants_empty_ui_add_queen_ants_button)
    void addQueenAnts() {
        final List<Queen> queens = new ArrayList<>();

        Queen queen = Queen.newQueen("Jennifer");
        queen.getAnts(null).add(Ant.newAnt("ant jen-1"));
        queen.getAnts(null).add(Ant.newAnt("ant jen-2"));
        queens.add(queen);

        queen = Queen.newQueen("Sabrina");
        queen.getAnts(null).add(Ant.newAnt("ant sab-1"));
        queen.getAnts(null).add(Ant.newAnt("ant sab-2"));
        queen.getAnts(null).add(Ant.newAnt("ant sab-3"));
        queens.add(queen);

        Queen.saveQueen(storIOSQLite, queens);

    }
}
