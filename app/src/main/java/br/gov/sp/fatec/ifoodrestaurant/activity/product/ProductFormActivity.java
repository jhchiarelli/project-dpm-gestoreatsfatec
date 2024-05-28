package br.gov.sp.fatec.ifoodrestaurant.activity.product;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import br.gov.sp.fatec.ifoodrestaurant.R;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivityProductFormBinding;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Product;
import br.gov.sp.fatec.ifoodrestaurant.repository.ProductRepository;
import br.gov.sp.fatec.ifoodrestaurant.tasks.AsyncTaskExecutor;

public class ProductFormActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-ProductFormActivity";
    private ActivityProductFormBinding binding;
    private ProductRepository productRepository;
    private AlertDialog progressDialog;
    private boolean isUpdateMode;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityProductFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productRepository = new ProductRepository();

        isUpdateMode = getIntent().getBooleanExtra("isUpdateMode", false);
        productId = "";

        if (isUpdateMode) {
            Product product = (Product)getIntent().getSerializableExtra("product");
            if (product != null) {
                productId = product.getId();
                loadData(product);
            }
            binding.btCreate.setText("Atualizar Produto");
        }

        binding.btCreate.setOnClickListener(v -> checkData());
        binding.btBack.setOnClickListener(v -> goBack());
    }

    private void loadData(Product product) {
        binding.edProduct.setText(product.getName());
        binding.edDescProduct.setText(product.getDescription());
        binding.edPrice.setText(String.valueOf(product.getPrice()));
        binding.edCategoria.setText(product.getCategoryId());
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
        String categoryId = "1";
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