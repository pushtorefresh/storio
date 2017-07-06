package com.pushtorefresh.storio.sample.many_to_many_sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pushtorefresh.storio.sample.R;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.Car;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.ViewHolder> {

    @NonNull
    private List<Person> persons = Collections.emptyList();

    @NonNull
    private final Callbacks callbacks;

    public PersonsAdapter(@NonNull Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void setPersons(@NonNull List<Person> persons) {
        this.persons = persons;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    @Override
    public
    @NonNull
    ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_person, parent, false), callbacks);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(persons.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final TextView personTextView;

        @NonNull
        private final TextView marksTextView;

        @Nullable
        private Person person;

        ViewHolder(@NonNull View itemView, @NonNull final Callbacks callbacks) {
            super(itemView);
            personTextView = (TextView) itemView.findViewById(R.id.list_item_person);
            marksTextView = (TextView) itemView.findViewById(R.id.list_item_cars);

            itemView.findViewById(R.id.add_car_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    assert person != null;  // should be already bound
                    callbacks.onAddCarClick(person);
                }
            });

            itemView.findViewById(R.id.remove_car_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    assert person != null;  // should be already bound
                    callbacks.onRemoveCarClick(person);
                }
            });
        }

        void bind(@NonNull Person person) {
            this.person = person;

            personTextView.setText(String.format("@%s", person.name()));

            final List<Car> cars = person.cars();
            final List<String> marks;
            if (cars == null) {
                marks = Collections.<String>emptyList();
            } else {
                marks = new ArrayList<String>(cars.size());
                for (Car car : cars) {
                    marks.add(car.mark());
                }
            }
            marksTextView.setText(TextUtils.join(", ", marks));
        }
    }

    interface Callbacks {

        void onAddCarClick(@NonNull Person person);

        void onRemoveCarClick(@NonNull Person person);
    }
}
