package br.gov.sp.fatec.ifoodrestaurant.activity.publicity;

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
import br.gov.sp.fatec.ifoodrestaurant.adapter.publicity.PublicityAdapter;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivityPublicityBinding;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Publicity;
import br.gov.sp.fatec.ifoodrestaurant.repository.PublicityRepository;
import br.gov.sp.fatec.ifoodrestaurant.tasks.AsyncTaskExecutor;

public class PublicityActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-PublicityActivity";
    private ActivityPublicityBinding binding;
    private AlertDialog progressDialog;
    private PublicityRepository publicityRepository;
    private List<Publicity> listData;
    private PublicityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityPublicityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        publicityRepository = new PublicityRepository();
        listData = new ArrayList<>();

        binding.rvData.setLayoutManager(new LinearLayoutManager(this));
        binding.rvData.scrollToPosition(0);
        adapter = new PublicityAdapter(listData);
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
        Intent intent = new Intent(this, PublicityFormActivity.class);
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

    private class GetAllData extends AsyncTaskExecutor<Void, Void, List<Publicity>> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected List<Publicity> doInBackground(Void... voids) {
            Task<QuerySnapshot> task = publicityRepository.getAll();
            try {
                QuerySnapshot querySnapshot = Tasks.await(task);
                List<Publicity> data = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Publicity publicity = document.toObject(Publicity.class);
                    if (publicity != null) {
                        publicity.setId(document.getId());
                        data.add(publicity);
                    }
                }
                return data;

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Publicity> data) {
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