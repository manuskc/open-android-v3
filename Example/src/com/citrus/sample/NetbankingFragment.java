package com.citrus.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.dynamicPricing.DynamicPricingOperation;
import com.citrus.sdk.dynamicPricing.DynamicPricingResponse;
import com.citrus.sdk.payment.MerchantPaymentOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;

import java.util.ArrayList;

public final class NetbankingFragment extends Fragment {

    private ArrayList<NetbankingOption> mNetbankingOptionsList;
    private Utils.PaymentType paymentType = null;
    private Amount amount = null;
    private MerchantPaymentOption mMerchantPaymentOption = null;
    private MerchantPaymentOption mLoadMoneyPaymentOptions = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NetbankingPaymetFragment.
     */
    public static NetbankingFragment newInstance(Utils.PaymentType paymentType, Amount amount) {
        NetbankingFragment fragment = new NetbankingFragment();
        Bundle args = new Bundle();
        args.putSerializable("paymentType", paymentType);
        args.putParcelable("amount", amount);
        fragment.setArguments(args);
        return fragment;
    }

    public NetbankingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            amount = (Amount) getArguments().getParcelable("amount");
            paymentType = (Utils.PaymentType) getArguments().getSerializable("paymentType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_net_banking, container, false);

        final NetbankingAdapter netbankingAdapter = new NetbankingAdapter(mNetbankingOptionsList);

        RecyclerView recylerViewNetbanking = (RecyclerView) view.findViewById(R.id.recycler_view_netbanking);
        recylerViewNetbanking.setAdapter(netbankingAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recylerViewNetbanking.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recylerViewNetbanking.setLayoutManager(mLayoutManager);

        recylerViewNetbanking.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new OnItemClickListener()));

        if (paymentType == Utils.PaymentType.LOAD_MONEY) {

            // Get the load money payment options.
            CitrusClient.getInstance(getActivity()).getLoadMoneyPaymentOptions(new Callback<MerchantPaymentOption>() {
                @Override
                public void success(MerchantPaymentOption loadMoneyPaymentOptions) {
                    mLoadMoneyPaymentOptions = loadMoneyPaymentOptions;
                    netbankingAdapter.setNetbankingOptionList(mLoadMoneyPaymentOptions.getNetbankingOptionList());
                    netbankingAdapter.notifyDataSetChanged();

                    mNetbankingOptionsList = mLoadMoneyPaymentOptions.getNetbankingOptionList();
                }

                @Override
                public void error(CitrusError error) {
                    Utils.showToast(getActivity(), error.getMessage());
                }
            });
        } else {
            // Get the load money payment options.
            CitrusClient.getInstance(getActivity()).getMerchantPaymentOptions(new Callback<MerchantPaymentOption>() {
                @Override
                public void success(MerchantPaymentOption merchantPaymentOption) {
                    mMerchantPaymentOption = merchantPaymentOption;
                    netbankingAdapter.setNetbankingOptionList(mMerchantPaymentOption.getNetbankingOptionList());
                    netbankingAdapter.notifyDataSetChanged();

                    mNetbankingOptionsList = mMerchantPaymentOption.getNetbankingOptionList();
                }

                @Override
                public void error(CitrusError error) {
                    Utils.showToast(getActivity(), error.getMessage());
                }
            });
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnPaymentOptionSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnPaymentOptionSelectedListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private NetbankingOption getItem(int position) {
        NetbankingOption netbankingOption = null;

        if (mNetbankingOptionsList != null && mNetbankingOptionsList.size() > position && position >= -1) {
            netbankingOption = mNetbankingOptionsList.get(position);
        }

        return netbankingOption;
    }

    private class OnItemClickListener extends RecyclerItemClickListener.SimpleOnItemClickListener {

        @Override
        public void onItemClick(View childView, int position) {
            NetbankingOption netbankingOption = getItem(position);

            PaymentType paymentType1;
            CitrusClient client = CitrusClient.getInstance(getActivity());

            Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse) {
                    ((UIActivity) getActivity()).onPaymentComplete(transactionResponse);
                }

                @Override
                public void error(CitrusError error) {
                    Utils.showToast(getActivity(), error.getMessage());
                }
            };

            try {
                if (paymentType == Utils.PaymentType.LOAD_MONEY) {
                    paymentType1 = new PaymentType.LoadMoney(amount, Constants.RETURN_URL_LOAD_MONEY, netbankingOption);
                    client.loadMoney((PaymentType.LoadMoney) paymentType1, callback);
                } else if (paymentType == Utils.PaymentType.PG_PAYMENT) {
                    paymentType1 = new PaymentType.PGPayment(amount, Constants.BILL_URL, netbankingOption, new CitrusUser(client.getUserEmailId(), client.getUserMobileNumber()));
                    client.pgPayment((PaymentType.PGPayment) paymentType1, callback);
                } else if (paymentType == Utils.PaymentType.DYNAMIC_PRICING) {
                    client.performDynamicPricing(DynamicPricingOperation.SEARCH_AND_APPLY_RULE, Constants.BILL_URL, amount, netbankingOption, null, new Callback<DynamicPricingResponse>() {
                        @Override
                        public void success(DynamicPricingResponse dynamicPricingResponse) {
                            showPrompt(dynamicPricingResponse);
                        }

                        @Override
                        public void error(CitrusError error) {
                            Utils.showToast(getActivity(), error.getMessage());
                        }
                    });
                }
            } catch (CitrusException e) {
                e.printStackTrace();

                Utils.showToast(getActivity(), e.getMessage());
            }
        }
    }

    private void showPrompt(final DynamicPricingResponse dynamicPricingResponse) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        String message = dynamicPricingResponse.getMessage();
        String positiveButtonText = "Pay";

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final TextView originalAmount = new TextView(getActivity());
        final TextView alteredAmount = new TextView(getActivity());
        final TextView txtMessage = new TextView(getActivity());

        linearLayout.addView(originalAmount);
        linearLayout.addView(alteredAmount);
        linearLayout.addView(txtMessage);

        originalAmount.setText("Original Amount : " + (dynamicPricingResponse.getOriginalAmount() != null ? dynamicPricingResponse.getOriginalAmount().getValue() : ""));
        alteredAmount.setText("Altered Amount : " + (dynamicPricingResponse.getAlteredAmount() != null ? dynamicPricingResponse.getAlteredAmount().getValue() : ""));
        txtMessage.setText("Message : " + dynamicPricingResponse.getMessage());

        alert.setTitle("Dynamic Pricing");
        alert.setMessage(message);
        // Set an EditText view to get user input
        alert.setView(linearLayout);
        if (dynamicPricingResponse.getStatus() == DynamicPricingResponse.Status.SUCCESS) {
            alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    CitrusClient.getInstance(getActivity()).pgPayment(dynamicPricingResponse, new Callback<TransactionResponse>() {
                        @Override
                        public void success(TransactionResponse transactionResponse) {
                            Utils.showToast(getActivity(), transactionResponse.getMessage());
                        }

                        @Override
                        public void error(CitrusError error) {
                            Utils.showToast(getActivity(), error.getMessage());
                        }
                    });

                    dialog.dismiss();
                }
            });
        }

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}


