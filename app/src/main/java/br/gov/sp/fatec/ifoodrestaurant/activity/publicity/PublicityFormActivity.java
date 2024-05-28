package br.gov.sp.fatec.ifoodrestaurant.activity.publicity;

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
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivityPublicityFormBinding;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Category;
import br.gov.sp.fatec.ifoodrestaurant.models.Publicity;
import br.gov.sp.fatec.ifoodrestaurant.repository.PublicityRepository;
import br.gov.sp.fatec.ifoodrestaurant.tasks.AsyncTaskExecutor;

public class PublicityFormActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-PublicityFormActivity";
    private ActivityPublicityFormBinding binding;
    private PublicityRepository publicityRepository;
    private AlertDialog progressDialog;
    private boolean isUpdateMode;
    private String publicityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityPublicityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        publicityRepository = new PublicityRepository();

        isUpdateMode = getIntent().getBooleanExtra("isUpdateMode", false);
        publicityId = "";

        if (isUpdateMode) {
            Publicity publicity = (Publicity)getIntent().getSerializableExtra("publicity");
            if (publicity != null) {
                publicityId = publicity.getId();
                loadData(publicity);
            }
            binding.btCreate.setText("Atualizar Publicidade");
        }

        binding.btCreate.setOnClickListener(v -> checkData());
        binding.btBack.setOnClickListener(v -> goBack());
    }

    private void loadData(Publicity publicity) {
        binding.edName.setText(publicity.getName());
        binding.edImageUrl.setText(publicity.getImageUrl());
        binding.edTargetUrl.setText(publicity.getTargetLink());
    }

    private void goBack() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void checkData() {
        Log.i(TAG_SCREEN, "checkData");
        closeKeyboard();

        String name = binding.edName.getText().toString();
        String imageUrl = binding.edImageUrl.getText().toString();
        String targetUrl = binding.edTargetUrl.getText().toString();

        if (!name.isEmpty()) {
            if (!imageUrl.isEmpty()) {
                createPublicity(name, imageUrl, targetUrl, isUpdateMode);
            } else {
                binding.edImageUrl.setError("Informe a URL da imagem");
                binding.edImageUrl.requestFocus();
            }
        } else {
            binding.edName.setError("Informe o nome da publicidade");
            binding.edName.requestFocus();
        }
    }

    private void createPublicity(String name, String imageUrl, String targetUrl, boolean isUpdateMode) {
        Log.i(TAG_SCREEN, "createPublicity");
        Publicity publicity = new Publicity(name, imageUrl, targetUrl, true);
        if (isUpdateMode) {
            publicity.setId(publicityId);
            new UpdatePublicityTask().execute(publicity);
        } else {
            new AddPublicityTask().execute(publicity);
        }
    }

    private class AddPublicityTask extends AsyncTaskExecutor<Publicity, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Publicity... publicities) {
            Publicity publicity = publicities[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            publicityRepository.addPublicity(publicity,
                    documentReference -> {
                        handler.post(() -> {
                            if (documentReference != null && documentReference.getId() != null) {
                                Toast.makeText(PublicityFormActivity.this, "Publicidade added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                                result[0] = true;
                            } else {
                                Toast.makeText(PublicityFormActivity.this, "Error adding Publicidade", Toast.LENGTH_SHORT).show();
                            }
                            completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(PublicityFormActivity.this, "Error adding Publicidade", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PublicityFormActivity.this, "Publicidade adicionada com sucesso", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                Toast.makeText(PublicityFormActivity.this, "Erro ao adicionar Publicidade", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdatePublicityTask extends AsyncTaskExecutor<Publicity, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Publicity... publicities) {
            Publicity publicity = publicities[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            publicityRepository.updatePublicity(publicity,
                    documentReference -> {
                        handler.post(() -> {
                            result[0] = true;
                            completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(PublicityFormActivity.this, "Error adding Publicidade", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PublicityFormActivity.this, "Publicidade atualizada com sucesso", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                Toast.makeText(PublicityFormActivity.this, "Erro ao atualizar Publicidade", Toast.LENGTH_SHORT).show();
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