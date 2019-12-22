package tarc.edu.prototype.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import tarc.edu.prototype.Model.Transaction;
import tarc.edu.prototype.R;
import tarc.edu.prototype.View.Activities.TransactionDetailsActivity;
import tarc.edu.prototype.ViewHolder.TransactionViewHolder;

public class TransactionAdapter extends FirebaseRecyclerAdapter<Transaction, TransactionViewHolder>{

    private Context context;

    public TransactionAdapter(@NonNull FirebaseRecyclerOptions<Transaction> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull TransactionViewHolder transactionViewHolder, int i, @NonNull final Transaction transaction) {

        if(transaction.getTransactionDesc().equals("Topup")){
            transactionViewHolder.tstatus.setText(transaction.getTransactionDesc());
            transactionViewHolder.tamount.setText(context.getResources().getString(R.string.plus_amount, Double.parseDouble(transaction.getAmount())));
            transactionViewHolder.tamount.setTextColor(Color.GREEN);
        }
        if(transaction.getTransactionDesc().equals("Withdraw")){
            transactionViewHolder.tstatus.setText(transaction.getTransactionDesc());
            transactionViewHolder.tamount.setText(context.getResources().getString(R.string.minus_amount, Double.parseDouble(transaction.getAmount())));
            transactionViewHolder.tamount.setTextColor(Color.RED);
        }
        if(transaction.getTransactionDesc().equals("Payment")){
            transactionViewHolder.tstatus.setText(context.getResources().getString(R.string.transaction_status, transaction.getTransactionDesc(), transaction.getOrderId()));
            transactionViewHolder.tamount.setText(context.getResources().getString(R.string.minus_amount, Double.parseDouble(transaction.getAmount())));
            transactionViewHolder.tamount.setTextColor(Color.RED);
        }
        if(transaction.getStatus().equals("Failed")){
            transactionViewHolder.tamount.setTextColor(Color.RED);
        }

        transactionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TransactionDetailsActivity.class);
                intent.putExtra("tId",transaction.getTransactionId());
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_list, parent, false);
        return new TransactionViewHolder(view);
    }
}
