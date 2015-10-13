package com.citrus.sdk.ui.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.utils.Utils;
import com.citrus.sdk.ui.widgets.CitrusTextView;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by akshaykoul on 7/6/15.
 */
public class CardListAdapter extends BaseAdapter{
    private Activity activity;
    private List<CardOption> cardOptionList;
    private LayoutInflater layoutInflater;
    public static final String TAG = "CardListAdapter$";
    private DeleteClickListener deleteClickListener;
    public CardListAdapter(Activity activity,List<CardOption>cardOptionList,DeleteClickListener deleteClickListener) {
        this.cardOptionList = cardOptionList;
        this.activity = activity;
        this.layoutInflater = LayoutInflater.from(activity);
        this.deleteClickListener = deleteClickListener;
    }

    @Override
    public int getCount() {
        return cardOptionList != null && !cardOptionList.isEmpty() ? cardOptionList.size() : 0;
    }

    @Override
    public CardOption getItem(int position) {
        return cardOptionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        CardOption cardOption = getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_saved_card_item,
                    parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.cardNumber.setText(Utils.getFormattedCardNumber(cardOption.getCardNumber()));
        if(cardOption.getCardScheme()==null){
            holder.cardType.setText(cardOption.getCardType());
        }
        else{
            holder.cardType.setText(cardOption.getCardType() + " - " + cardOption.getCardScheme());
        }

        Drawable cardDrawable = cardOption.getOptionIcon(activity);
        if (cardDrawable != null) {
            Logger.d(TAG+" Card image found");
            holder.cardImage.setImageDrawable(cardDrawable);
        }else{
            Logger.d(TAG+" Card image not found");
        }
        holder.deleteImage.setVisibility(View.VISIBLE);
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClickListener.deleteItem(position);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        public ImageView cardImage;
        public ImageView deleteImage;
        public CitrusTextView cardNumber;
        public TextView cardType;

        public ViewHolder(View convertView) {
            this.cardImage = (ImageView)convertView.findViewById(R.id.card_image);
            this.deleteImage = (ImageView)convertView.findViewById(R.id.delete_image);
            this.cardNumber = (CitrusTextView)convertView.findViewById(R.id.card_number);
            this.cardType = (TextView)convertView.findViewById(R.id.card_type);
        }
    }

    public interface DeleteClickListener {
        public void deleteItem(int position);
    }
}
