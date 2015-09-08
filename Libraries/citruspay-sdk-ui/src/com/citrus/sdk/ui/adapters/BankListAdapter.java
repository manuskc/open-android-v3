package com.citrus.sdk.ui.adapters;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrus.sdk.classes.PGHealth;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.ui.R;

import java.util.List;

/**
 * Created by akshaykoul on 6/5/15.
 */
public class BankListAdapter extends BaseAdapter {
    private Activity activity;
    private List<NetbankingOption> netbankingOptions;
    private LayoutInflater layoutInflater;
    public BankListAdapter(Activity activity,List<NetbankingOption>netbankingOptions) {
        this.netbankingOptions = netbankingOptions;
        this.activity = activity;
        this.layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return netbankingOptions != null && !netbankingOptions.isEmpty() ? netbankingOptions.size() : 0;
    }

    @Override
    public NetbankingOption getItem(int position) {
        return netbankingOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        NetbankingOption item = getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_bank_item,
                    parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bankName.setText(item.getBankName());
        holder.bankIcon.setImageDrawable(item.getOptionIcon(activity));
        if(item.getPgHealth().equals(PGHealth.GOOD)){
            holder.warnIcon.setVisibility(View.GONE);
        }else{
            holder.warnIcon.setVisibility(View.VISIBLE);
        }
        holder.warnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar.make(parent, R.string.bank_caution, Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(activity.getResources().getColor(R.color.citrus_warn_color));
                snackbar.show();
            }

        });
        return convertView;
    }

    static class ViewHolder {
        public ImageView bankIcon;
        public ImageView warnIcon;
        public TextView bankName;

        public ViewHolder(View convertView) {
            this.bankIcon = (ImageView)convertView.findViewById(R.id.bank_icon);
            this.bankName = (TextView)convertView.findViewById(R.id.bank_name);
            this.warnIcon = (ImageView)convertView.findViewById(R.id.warn_icon);
        }
    }
}
