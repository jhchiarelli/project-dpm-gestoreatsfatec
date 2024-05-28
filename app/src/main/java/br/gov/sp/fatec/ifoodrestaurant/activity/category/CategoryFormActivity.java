package br.gov.sp.fatec.ifoodrestaurant.activity.category;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import br.gov.sp.fatec.ifoodrestaurant.MainActivity;
import br.gov.sp.fatec.ifoodrestaurant.R;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivityCategoryFormBinding;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Category;
import br.gov.sp.fatec.ifoodrestaurant.repository.CategoryRepository;
import br.gov.sp.fatec.ifoodrestaurant.tasks.AsyncTaskExecutor;

public class CategoryFormActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-CategoryFormActivity";
    private ActivityCategoryFormBinding binding;
    private CategoryRepository categoryRepository;
    private AlertDialog progressDialog;
    private boolean isUpdateMode;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityCategoryFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryRepository = new CategoryRepository();

        isUpdateMode = getIntent().getBooleanExtra("isUpdateMode", false);
        categoryId = "";

        if (isUpdateMode) {
            Category category = (Category)getIntent().getSerializableExtra("category");
            if (category != null) {
                categoryId = category.getId();
                loadData(category);
            }
            binding.btCreate.setText("Atualizar Categoria");
        }

        binding.btCreate.setOnClickListener(v -> checkData());
        binding.btBack.setOnClickListener(v -> goBack());
    }

    private void loadData(Category category) {
        binding.edCategoria.setText(category.getDescription());
        binding.edImageUrl.setText(category.getImageUrl());
    }

    private void goBack() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void checkData() {
        Log.i(TAG_SCREEN, "checkData");
        closeKeyboard();

        String description = binding.edCategoria.getText().toString();
        String imageUrl = binding.edImageUrl.getText().toString();

        if (!description.isEmpty()) {
            createCategory(description, imageUrl, isUpdateMode);
        } else {
            binding.edCategoria.setError("Informe a descrição da categoria");
            binding.edCategoria.requestFocus();
        }
    }

    private void createCategory(String description, String imageUrl, boolean isUpdateMode) {
        Log.i(TAG_SCREEN, "createCategory");
        Category category = new Category(description, imageUrl, true);
        if (isUpdateMode) {
            category.setId(categoryId);
            new UpdateCategoryTask().execute(category);
        } else {
            new AddCategoryTask().execute(category);
        }
    }

    private class AddCategoryTask extends AsyncTaskExecutor<Category, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Category... categories) {
            Category category = categories[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            categoryRepository.addCategory(category,
                    documentReference -> {
                        handler.post(() -> {
                            if (documentReference != null && documentReference.getId() != null) {
                                result[0] = true;
                            } else {
                                Toast.makeText(CategoryFormActivity.this, "Error adding categoria", Toast.LENGTH_SHORT).show();
                            }
                            completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(CategoryFormActivity.this, "Error adding categoria", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CategoryFormActivity.this, "Categoria adicionada com sucesso", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                Toast.makeText(CategoryFormActivity.this, "Erro ao adicionar categoria", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateCategoryTask extends AsyncTaskExecutor<Category, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Category... categories) {
            Category category = categories[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            categoryRepository.updateCategory(category,
                    aVoid -> {
                        handler.post(() -> {
                                result[0] = true;
                                completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(CategoryFormActivity.this, "Error update categoria", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CategoryFormActivity.this, "Categoria atualizada com sucesso", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                Toast.makeText(CategoryFormActivity.this, "Erro ao atualizar categoria", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDashboard() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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