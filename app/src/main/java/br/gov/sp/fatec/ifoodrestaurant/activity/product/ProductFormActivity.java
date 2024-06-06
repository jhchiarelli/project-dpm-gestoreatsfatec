package br.gov.sp.fatec.ifoodrestaurant.activity.product;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.gov.sp.fatec.ifoodrestaurant.R;
import br.gov.sp.fatec.ifoodrestaurant.adapter.category.CategoryAdapter;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivityProductFormBinding;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Category;
import br.gov.sp.fatec.ifoodrestaurant.models.Product;
import br.gov.sp.fatec.ifoodrestaurant.repository.CategoryRepository;
import br.gov.sp.fatec.ifoodrestaurant.repository.ProductRepository;
import br.gov.sp.fatec.ifoodrestaurant.tasks.AsyncTaskExecutor;

public class ProductFormActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-ProductFormActivity";
    private ActivityProductFormBinding binding;
    private ProductRepository productRepository;
    private AlertDialog progressDialog;
    private boolean isUpdateMode;
    private String productId;
    private String selectedCategoryId;
    private CategoryRepository categoryRepository;
    ArrayAdapter<Category> adapterCategory;
    private List<Category> listCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityProductFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryRepository = new CategoryRepository();
        productRepository = new ProductRepository();

        isUpdateMode = getIntent().getBooleanExtra("isUpdateMode", false);
        productId = "";
        selectedCategoryId = "";

        listCategory = new ArrayList<>();
        adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCategory);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCategoria.setAdapter(adapterCategory);

        binding.spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedCategoryId = listCategory.get(position).getId();
                } else {
                    selectedCategoryId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoryId = "";
            }
        });

        if (isUpdateMode) {
            Product product = (Product)getIntent().getSerializableExtra("product");
            if (product != null) {
                productId = product.getId();
                selectedCategoryId = product.getCategoryId();
                loadData(product);
            }
            binding.btCreate.setText("Atualizar Produto");
        }

        binding.btCreate.setOnClickListener(v -> checkData());
        binding.btBack.setOnClickListener(v -> goBack());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategory();
    }

    private void loadCategory() {
        Log.i(TAG_SCREEN, "loadCategory");
        new GetAllCategory().execute();
    }

    private class GetAllCategory extends AsyncTaskExecutor<Void, Void, List<Category>> {
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
                if (selectedCategoryId.isEmpty()) {
                    data.add(new Category("Selecione uma categoria", "", false));
                }
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
                listCategory.clear();
                listCategory.addAll(data);
                adapterCategory.notifyDataSetChanged();
                // Seleciona a categoria recebida via Intent
                if (selectedCategoryId != null && !selectedCategoryId.isEmpty()) {
                    for (int i = 0; i < listCategory.size(); i++) {
//                        Log.d(TAG_SCREEN, "Listando: " + listCategory.get(i).getId() + "- " + listCategory.get(i).getDescription());
                        if (listCategory.get(i).getId().equals(selectedCategoryId)) {
                            binding.spCategoria.setSelection(i);
                            Log.d(TAG_SCREEN, "Setando selectedCategoryId: " + selectedCategoryId);
                            break;
                        }
                    }
                } else {
                    binding.spCategoria.setSelection(-1);
                }

            }
        }
    }

    private void loadData(Product product) {
        binding.edProduct.setText(product.getName());
        binding.edDescProduct.setText(product.getDescription());
        binding.edPrice.setText(String.valueOf(product.getPrice()));
        binding.edImageUrl.setText(product.getImageUrl());
    }

    private void goBack() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void checkData() {
        Log.i(TAG_SCREEN, "checkData");
        closeKeyboard();

        String name = binding.edProduct.getText().toString();
        String description = binding.edDescProduct.getText().toString();
        Double price = 0.0;
        String imageUrl = binding.edImageUrl.getText().toString();
        Boolean active = true;
        String categoryId = selectedCategoryId;
        String restaurantId = "1";


        if (!name.isEmpty()) {
            if (!binding.edPrice.getText().toString().isEmpty()) {
                price = Double.parseDouble(binding.edPrice.getText().toString());
                createData(name, description, price, imageUrl, active, categoryId, restaurantId, isUpdateMode);
            } else {
                binding.edPrice.setError("Informe o preço do produto");
                binding.edPrice.requestFocus();
                return;
            }
        } else {
            binding.edProduct.setError("Informe o nome do produto");
            binding.edProduct.requestFocus();
        }
    }

    private void createData(String name, String description, Double price, String imgageUrl, Boolean active, String categoryId, String restaurantId, boolean isUpdateMode) {
        Log.i(TAG_SCREEN, "createData");
        Product product = new Product(name, description, price, imgageUrl, active, categoryId, restaurantId);
        if (isUpdateMode) {
            product.setId(productId);
            new UpdateProductTask().execute(product);
        } else {
            new AddProductTask().execute(product);
        }
    }

private class AddProductTask extends AsyncTaskExecutor<Product, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Product... products) {
            Product product = products[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            productRepository.addProduct(product,
                    documentReference -> {
                        handler.post(() -> {
                            if (documentReference != null && documentReference.getId() != null) {
                                Toast.makeText(ProductFormActivity.this, "Produto added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                                result[0] = true;
                            } else {
                                Toast.makeText(ProductFormActivity.this, "Error adding produto", Toast.LENGTH_SHORT).show();
                            }
                            completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(ProductFormActivity.this, "Error adding produto", Toast.LENGTH_SHORT).show();
                            completed[0] = true;
                        });
                    });

            // Aguarde até que a operação seja concluída
            while (!completed[0]) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return result[0];
        }
        @Override
        protected void onPostExecute(Boolean result) {
            hideProgressDialog();
            if (result) {
                Toast.makeText(ProductFormActivity.this, "Produto adicionado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                Toast.makeText(ProductFormActivity.this, "Erro ao adicionar produto", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateProductTask extends AsyncTaskExecutor<Product, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Product... products) {
            Product product = products[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            productRepository.updateProduct(product,
                    aVoid -> {
                        handler.post(() -> {
                            result[0] = true;
                            completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(ProductFormActivity.this, "Error adding produto", Toast.LENGTH_SHORT).show();
                            completed[0] = true;
                        });
                    });

            // Aguarde até que a operação seja concluída
            while (!completed[0]) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return result[0];
        }
        @Override
        protected void onPostExecute(Boolean result) {
            hideProgressDialog();
            if (result) {
                Toast.makeText(ProductFormActivity.this, "Produto atualizado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                Toast.makeText(ProductFormActivity.this, "Erro ao atualizar produto", Toast.LENGTH_SHORT).show();
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
    private void closeKeyboard() {
        // Obtem o servico de entrada
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Verifica se o teclado esta aberto
        if (imm != null && getCurrentFocus() != null) {
            // Fecha o teclado
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}