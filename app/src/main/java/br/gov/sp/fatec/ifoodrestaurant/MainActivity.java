package br.gov.sp.fatec.ifoodrestaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.gov.sp.fatec.ifoodrestaurant.activity.auth.SignInActivity;
import br.gov.sp.fatec.ifoodrestaurant.activity.auth.SignUpActivity;
import br.gov.sp.fatec.ifoodrestaurant.activity.auth.SignUpFinishActivity;
import br.gov.sp.fatec.ifoodrestaurant.activity.category.CategoryActivity;
import br.gov.sp.fatec.ifoodrestaurant.activity.product.ProductActivity;
import br.gov.sp.fatec.ifoodrestaurant.activity.publicity.PublicityActivity;
import br.gov.sp.fatec.ifoodrestaurant.activity.restaurant.RestaurantActivity;
import br.gov.sp.fatec.ifoodrestaurant.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-MainActivity";
    private ActivityMainBinding binding;
    //Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btSignIn.setOnClickListener(v -> showSignIn());

        binding.btSignUp.setOnClickListener(v -> showNewUser());

        binding.btLogout.setOnClickListener(v -> logOut());

        binding.btSignFinish.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUpFinishActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.btListRestaurants.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.btListCategories.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.btProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.btPublicity.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PublicityActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG_SCREEN, "Entry onStart");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            binding.isLogged.setText(currentUser.getEmail());
            binding.btSignIn.setEnabled(false);
            binding.btLogout.setVisibility(View.VISIBLE);
            Log.i(TAG_SCREEN, "User is logged in");
            Log.i(TAG_SCREEN, "User ID: " + currentUser.getUid());
            Log.i(TAG_SCREEN, "User Email: " + currentUser.getEmail());
        } else {
            Log.i(TAG_SCREEN, "Efetue o Login");
            binding.btLogout.setVisibility(View.INVISIBLE);
            binding.btSignIn.setEnabled(true);
            showSignIn();
        }
    }

    private void logOut() {
        mAuth.signOut();
        binding.isLogged.setText("Usuário deslogado.");
        binding.btLogout.setVisibility(View.INVISIBLE);
        binding.btSignIn.setEnabled(true);
        showSignIn();
    }

    private void showSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    private void showNewUser() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}