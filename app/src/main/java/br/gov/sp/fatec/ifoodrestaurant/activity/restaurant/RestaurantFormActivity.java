package br.gov.sp.fatec.ifoodrestaurant.activity.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.gov.sp.fatec.ifoodrestaurant.R;
import br.gov.sp.fatec.ifoodrestaurant.activity.auth.SignUpFinishActivity;
import br.gov.sp.fatec.ifoodrestaurant.adapter.restaurant.RestaurantAdapter;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivityRestaurantFormBinding;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Category;
import br.gov.sp.fatec.ifoodrestaurant.models.Restaurant;
import br.gov.sp.fatec.ifoodrestaurant.repository.RestaurantRepository;
import br.gov.sp.fatec.ifoodrestaurant.tasks.AsyncTaskExecutor;
import br.gov.sp.fatec.ifoodrestaurant.utils.PhoneNumberTextWatcher;

public class RestaurantFormActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-RestaurantFormActivity";
    private ActivityRestaurantFormBinding binding;
    private AlertDialog progressDialog;
    private RestaurantRepository restaurantRepository;
    private boolean isUpdateMode;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityRestaurantFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        restaurantRepository = new RestaurantRepository();

        isUpdateMode = getIntent().getBooleanExtra("isUpdateMode", false);
        restaurantId = "";

        if (isUpdateMode) {
            Restaurant restaurant = (Restaurant)getIntent().getSerializableExtra("restaurant");
            if (restaurant != null) {
                restaurantId = restaurant.getId();
                loadData(restaurant);
            }
            binding.btCreate.setText("Atualizar Restaurante");
        }

        binding.edFone.addTextChangedListener(new PhoneNumberTextWatcher(binding.edFone));
        binding.btCreate.setOnClickListener(v -> checkData());
        binding.btBack.setOnClickListener(v -> goBack());
    }

    private void loadData(Restaurant restaurant) {
        binding.edNome.setText(restaurant.getName());
        binding.edImageUrl.setText(restaurant.getUrlImage());
        binding.edFone.setText(restaurant.getPhone());
        binding.edEndereco.setText(restaurant.getAddress());
    }

    private void goBack() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void checkData() {
        Log.i(TAG_SCREEN, "checkData");
        closeKeyboard();

        String nome = binding.edNome.getText().toString();
        String fone = binding.edFone.getText().toString();
        String endereco = binding.edEndereco.getText().toString();
        String urlImage = binding.edImageUrl.getText().toString();

        if (!nome.isEmpty()) {
            if (!fone.isEmpty()) {
                addRestaurant(nome, fone, endereco, urlImage, isUpdateMode);
            } else {
                binding.edFone.setError("Informe seu Fone.");
                binding.edFone.requestFocus();
                Toast.makeText(this, "Informe seu Fone.", Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.edNome.setError("Informe seu nome.");
            binding.edNome.requestFocus();
            Toast.makeText(this, "Informe seu nome.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addRestaurant(String nome, String fone, String endereco, String urlImage, boolean isUpdateMode) {
        Log.i(TAG_SCREEN, "addRestaurant");
        Restaurant restaurant = new Restaurant(nome, fone, endereco, urlImage, true);
        if (isUpdateMode) {
            restaurant.setId(restaurantId);
            new UpdateRestaurantTask().execute(restaurant);
        }
    }

    private class UpdateRestaurantTask extends AsyncTaskExecutor<Restaurant, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Restaurant... restaurants) {
            Restaurant restaurant = restaurants[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            restaurantRepository.updateRestaurant(restaurant,
                    aVoid -> {
                        handler.post(() -> {
                                result[0] = true;
                                completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(RestaurantFormActivity.this, "Error update Restaurante", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RestaurantFormActivity.this, "Restaurante atualizado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                Toast.makeText(RestaurantFormActivity.this, "Erro ao atualizar Restaurante", Toast.LENGTH_SHORT).show();
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