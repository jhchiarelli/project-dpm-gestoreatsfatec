package br.gov.sp.fatec.ifoodrestaurant.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.gov.sp.fatec.ifoodrestaurant.R;
import br.gov.sp.fatec.ifoodrestaurant.adapter.order.OrderAdapter;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivityOrderBinding;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Order;
import br.gov.sp.fatec.ifoodrestaurant.repository.OrderRepository;
import br.gov.sp.fatec.ifoodrestaurant.tasks.AsyncTaskExecutor;

public class OrderActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-OrderActivity";
    private ActivityOrderBinding binding;
    private AlertDialog progressDialog;
    private OrderRepository orderRepository;
    private List<Order> listData;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderRepository = new OrderRepository();
        listData = new ArrayList<>();

        binding.rvData.setLayoutManager(new LinearLayoutManager(this));
        binding.rvData.scrollToPosition(0);
        adapter = new OrderAdapter(listData);
        binding.rvData.setAdapter(adapter);

        binding.btBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }

    private void fetchData() {
        Log.i(TAG_SCREEN, "fetchData");
        new GetAllData().execute();
    }

    private class GetAllData extends AsyncTaskExecutor<Void, Void, List<Order>> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG_SCREEN, "onPreExecute");
            showProgressDialog();
        }

        @Override
        protected List<Order> doInBackground(Void... voids) {
            Task<QuerySnapshot> task = orderRepository.getAll();
            try {
                QuerySnapshot querySnapshot = Tasks.await(task);
                List<Order> data = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Log.i(TAG_SCREEN, "Document: " + document.getId());
                    Order order = document.toObject(Order.class);
                    Log.i(TAG_SCREEN, "Order: " + order.getId());
                    if (order != null) {
                        order.setId(document.getId());
                        data.add(order);
                    }
                }
                return data;

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Order> data) {
            Log.i(TAG_SCREEN, "onPostExecute");
            hideProgressDialog();
            if (data != null) {
                listData.clear();
                listData.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            ProgressDialogBinding dialogBinding = ProgressDialogBinding.inflate(getLayoutInflater());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogBinding.getRoot());
            builder.setCancelable(false);
            progressDialog = builder.create();
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}