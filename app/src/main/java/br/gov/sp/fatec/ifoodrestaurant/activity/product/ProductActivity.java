package br.gov.sp.fatec.ifoodrestaurant.activity.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Intent;
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
import br.gov.sp.fatec.ifoodrestaurant.activity.product.ProductActivity;
import br.gov.sp.fatec.ifoodrestaurant.adapter.product.ProductAdapter;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivityProductBinding;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Product;
import br.gov.sp.fatec.ifoodrestaurant.repository.ProductRepository;
import br.gov.sp.fatec.ifoodrestaurant.tasks.AsyncTaskExecutor;

public class ProductActivity extends AppCompatActivity {

    final String TAG_SCREEN = "TAG-ProductActivity";
    private ActivityProductBinding binding;
    private AlertDialog progressDialog;
    private ProductRepository productRepository;
    private ProductAdapter adapter;
    private List<Product> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productRepository = new ProductRepository();
        listData = new ArrayList<>();

        binding.rvData.setLayoutManager(new LinearLayoutManager(this));
        binding.rvData.scrollToPosition(0);
        adapter = new ProductAdapter(listData);
        binding.rvData.setAdapter(adapter);

        binding.fabAdd.setOnClickListener(v -> {
            showForm();
        });

        binding.btBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void showForm() {
        Intent intent = new Intent(this, ProductFormActivity.class);
        intent.putExtra("isUpdateMode", false);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    private class GetAllData extends AsyncTaskExecutor<Void, Void, List<Product>> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected List<Product> doInBackground(Void... voids) {
            Task<QuerySnapshot> task = productRepository.getAll();
            try {
                QuerySnapshot querySnapshot = Tasks.await(task);
                List<Product> data = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Product product = document.toObject(Product.class);
                    if (product != null) {
                        product.setId(document.getId());
                        data.add(product);
                    }
                }
                return data;

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Product> data) {
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