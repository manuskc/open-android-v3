package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.adapters.CardListAdapter;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;


public class CardListFragment extends Fragment implements CardListAdapter.DeleteClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CardListFragment$";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View layout;
    private FragmentCallbacks mListener;
    List<CardOption> savedCardListComplete;
    CardListAdapter cardListAdapter;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CardListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardListFragment newInstance(String param1, String param2) {
        CardListFragment fragment = new CardListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private ListView cardList;

    public CardListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_list, container, false);
        cardList = (ListView)layout.findViewById(R.id.list_view);
        getCardList();
        return layout;
    }

    private void getCardList() {
        Logger.d(TAG + " Getting card list");
        mListener.showProgressDialog(false, getString(R.string.loading_saved_cards));
        CitrusClient.getInstance(getActivity()).getWallet(new com.citrus.sdk.Callback<List<PaymentOption>>() {
            @Override
            public void success(List<PaymentOption> paymentOptions) {

                savedCardListComplete = new ArrayList<CardOption>();
                Logger.d(TAG + " getWallet success ");
                if (isAdded()) {
                    mListener.dismissProgressDialog();
                    for (PaymentOption paymentOption : paymentOptions) {
                        Logger.d(TAG + " payment Option " + paymentOption.toString());
                        if (paymentOption instanceof CardOption) {
                            CardOption cardOption = (CardOption) paymentOption;
                            Logger.d(TAG + " Card Number " + cardOption.getCardNumber());
                            savedCardListComplete.add(cardOption);
                        }
                    }
                }
                if (isAdded()) {
                    if (!savedCardListComplete.isEmpty()) {
                        showCardList();
                    } else {
                        Snackbar.make(layout, getString(R.string.text_no_saved_cards), Snackbar
                                .LENGTH_SHORT).show();
                    }
                }


            }

            @Override
            public void error(CitrusError citrusError) {

                Logger.d(TAG + " getWallet failure " + citrusError.getMessage());
                if (isAdded()) {
                    mListener.dismissProgressDialog();
                    Snackbar.make(layout, getString(R.string.text_no_saved_cards), Snackbar
                            .LENGTH_SHORT).show();
                    ;
                }
            }
        });
    }

    private void showCardList() {
        Logger.d(TAG + " Show card list");
        if (savedCardListComplete !=null && !savedCardListComplete.isEmpty()) {
            cardListAdapter =  new CardListAdapter(getActivity(),savedCardListComplete,this);
            cardList.setAdapter(cardListAdapter);
        }else{
            Logger.d(TAG + " List Empty");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void deleteItem(final int position) {
        if(savedCardListComplete !=null && !savedCardListComplete.isEmpty()&&isAdded()){
            mListener.showProgressDialog(false,getString(R.string.deleting_cards));
            CitrusClient.getInstance(getActivity()).deletePaymentOption(savedCardListComplete.get(position), new Callback<CitrusResponse>() {


                @Override
                public void success(CitrusResponse citrusResponse) {
                    mListener.dismissProgressDialog();
                    savedCardListComplete.remove(position);
                    cardListAdapter.notifyDataSetChanged();
                }

                @Override
                public void error(CitrusError error) {
                    mListener.dismissProgressDialog();

                }
            });
        }
    }
}
