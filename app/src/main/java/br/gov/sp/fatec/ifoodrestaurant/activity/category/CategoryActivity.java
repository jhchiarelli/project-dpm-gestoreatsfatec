package br.gov.sp.fatec.ifoodrestaurant.activity.category;

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
import br.gov.sp.fatec.ifoodrestaurant.adapter.category.CategoryAdapter;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivityCategoryBinding;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Category;
import br.gov.sp.fatec.ifoodrestaurant.repository.CategoryRepository;
import br.gov.sp.fatec.ifoodrestaurant.tasks.AsyncTaskExecutor;

public class CategoryActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-CategoryActivity";
    private ActivityCategoryBinding binding;
    private AlertDialog progressDialog;
    private CategoryRepository categoryRepository;
    private CategoryAdapter adapter;
    private List<Category> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryRepository = new CategoryRepository();
        listData = new ArrayList<>();

        binding.rvData.setLayoutManager(new LinearLayoutManager(this));
        binding.rvData.scrollToPosition(0);
        adapter = new CategoryAdapter(listData);
        binding.rvData.setAdapter(adapter);

        binding.tvNewCategory.setOnClickListener(v -> {
            showCategoryForm();
        });

        binding.fabAddCategory.setOnClickListener(v -> {
            showCategoryForm();
        });

        binding.btBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

    }

    private void showCategoryForm() {
        Intent intent = new Intent(this, CategoryFormActivity.class);
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

    private class GetAllData extends AsyncTaskExecutor<Void, Void, List<Category>> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected List<Category> doInBackground(Void... voids) {
            Task<QuerySnapshot> task = categoryRepository.getAllCategories();
            try {
                QuerySnapshot querySnapshot = Tasks.await(task);
                List<Category> data = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Category category = document.toObject(Category.class);
                    if (category != null) {
                        category.setId(document.getId());
                        data.add(category);
                    }
                }
                return data;

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Category> data) {
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