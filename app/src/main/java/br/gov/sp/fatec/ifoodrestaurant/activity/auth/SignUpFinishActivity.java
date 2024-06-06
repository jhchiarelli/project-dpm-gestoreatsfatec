package br.gov.sp.fatec.ifoodrestaurant.activity.auth;

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

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import br.gov.sp.fatec.ifoodrestaurant.MainActivity;
import br.gov.sp.fatec.ifoodrestaurant.R;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivitySignUpFinishBinding;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Restaurant;
import br.gov.sp.fatec.ifoodrestaurant.repository.RestaurantRepository;
import br.gov.sp.fatec.ifoodrestaurant.tasks.AsyncTaskExecutor;
import br.gov.sp.fatec.ifoodrestaurant.utils.PhoneNumberTextWatcher;

public class SignUpFinishActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-SignUpActivity";
    private ActivitySignUpFinishBinding binding;
    private RestaurantRepository restaurantRepository;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivitySignUpFinishBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        restaurantRepository = new RestaurantRepository();

        String idRes = getIntent().getStringExtra("idUser");
        String nomeRes = getIntent().getStringExtra("nomeRest");
        String emailRes = getIntent().getStringExtra("emailRest");

        binding.edFone.addTextChangedListener(new PhoneNumberTextWatcher(binding.edFone));

        binding.btFinish.setOnClickListener(v -> checkData(idRes, nomeRes, emailRes));

    }

    private void checkData(String idUser, String nome, String email) {
        Log.i(TAG_SCREEN, "checkData");
        closeKeyboard();

        String fone = binding.edFone.getText().toString();
        String endereco = binding.edEndereco.getText().toString();
        String urlImage = binding.edImageUrl.getText().toString();

        if (!fone.isEmpty()) {
            if (!endereco.isEmpty()) {
                addRestaurant(idUser, nome, email, fone, endereco, urlImage);
            } else {
                binding.edEndereco.setError("Informe seu endereço.");
                binding.edEndereco.requestFocus();
                Toast.makeText(this, "Informe seu endereço.", Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.edFone.setError("Informe seu nome.");
            binding.edFone.requestFocus();
            Toast.makeText(this, "Informe seu número de telefone.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addRestaurant(String idUser, String nome, String email, String fone, String endereco, String urlImage) {
        Log.i(TAG_SCREEN, "addRestaurant");
        Restaurant user = new Restaurant(nome, email, fone, endereco,  "restaurante", urlImage, true, idUser);
        new AddRestaurantTask().execute(user);
    }

    private void showDashboard() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private class AddRestaurantTask extends AsyncTaskExecutor<Restaurant, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Restaurant... users) {
            Restaurant user = users[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            restaurantRepository.addUser(user,
                    documentReference -> {
                        handler.post(() -> {
                            if (documentReference != null && documentReference.getId() != null) {
                                result[0] = true;
                            } else {
                                Toast.makeText(SignUpFinishActivity.this, "Error adding user", Toast.LENGTH_SHORT).show();
                            }
                            completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(SignUpFinishActivity.this, "Error adding user", Toast.LENGTH_SHORT).show();
                            completed[0] = true;
                        });
                    }
            );

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
                showDashboard();
            } else {
                Toast.makeText(SignUpFinishActivity.this, "Failed to add user.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetUserTask extends AsyncTaskExecutor<String, Void, Restaurant> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Restaurant doInBackground(String... strings) {
            String userId = strings[0];
            Task<DocumentSnapshot> task = restaurantRepository.getRestaurant(userId);
            while (!task.isComplete()) {
                // Aguardando ser completado
            }
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(Restaurant.class);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Restaurant user) {
            hideProgressDialog();
            if (user != null) {
                Toast.makeText(SignUpFinishActivity.this, "User: " + user.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUpFinishActivity.this, "No such user", Toast.LENGTH_SHORT).show();
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