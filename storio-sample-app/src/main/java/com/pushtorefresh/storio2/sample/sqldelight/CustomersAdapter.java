package com.pushtorefresh.storio2.sample.sqldelight;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pushtorefresh.storio2.sample.R;
import com.pushtorefresh.storio2.sample.sqldelight.entities.Customer;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder> {

    private List<Customer> customers;

    public void setCustomers(@Nullable List<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return customers == null ? 0 : customers.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_customer, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Customer customer = customers.get(position);

        holder.name.setText(String.format("%s %s", customer.name(), customer.surname()));
        holder.city.setText(customer.city());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.list_item_customer_name)
        TextView name;

        @Bind(R.id.list_item_customer_city)
        TextView city;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
