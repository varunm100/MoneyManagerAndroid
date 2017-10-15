package com.example.meta_knight.moneymanagerdatabase;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CompaniesAdapter extends RecyclerView.Adapter<CompaniesAdapter.MyViewHolder> {
    private List<CompanyItem> companyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, name;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    public CompaniesAdapter (List<CompanyItem> CompanyList) {
        this.companyList = CompanyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.company_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CompanyItem CompItem = companyList.get(position);
        holder.title.setText(CompItem.getCompName());
        holder.name.setText(CompItem.getOwner());
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }
}
