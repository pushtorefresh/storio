package com.pushtorefresh.storio.sample.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pushtorefresh.storio.sample.R;
import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.entities.Person;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class PersonCarsAdapter extends RecyclerView.Adapter<PersonCarsAdapter.ViewHolder> {

    private List<Person> persons;

    public void setPersons(@Nullable List<Person> persons) {
        this.persons = persons;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return persons == null ? 0 : persons.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_person_cars, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Person person = persons.get(position);

        holder.personTextView.setText("Owner: " + person.name());

        String allCars = "Cars: ";
        allCars += android.text.TextUtils.join(",", person.cars());
//        List<Car> cars = person.cars();
//        for (Car car : cars) {
//            allCars += ", " + car.model();
//        }
//        allCars = allCars.substring(0, allCars.length()-2);
        holder.carsTextView.setText(allCars);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.list_item_person_data)
        TextView personTextView;

        @Bind(R.id.list_item_cars)
        TextView carsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
