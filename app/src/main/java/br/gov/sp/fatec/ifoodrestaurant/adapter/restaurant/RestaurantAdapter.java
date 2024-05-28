package br.gov.sp.fatec.ifoodrestaurant.adapter.restaurant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.gov.sp.fatec.ifoodrestaurant.R;
import br.gov.sp.fatec.ifoodrestaurant.activity.restaurant.RestaurantFormActivity;
import br.gov.sp.fatec.ifoodrestaurant.databinding.AdapterRestaurantBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Restaurant;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private List<Restaurant> restaurants;

    public RestaurantAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterRestaurantBinding binding = AdapterRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RestaurantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private final AdapterRestaurantBinding binding;

        public RestaurantViewHolder(@NonNull AdapterRestaurantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Restaurant restaurant) {
            binding.tvNomeRestaurante.setText(restaurant.getName());
            binding.tvEmail.setText(restaurant.getEmail());
            binding.tvFone.setText(restaurant.getPhone());
            binding.tvEndereco.setText(restaurant.getAddress());
            Glide.with(binding.getRoot().getContext())
                    .load(restaurant.getUrlImage())
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_24)
                    .into(binding.ivLogo);
            binding.btnDetalhes.setOnClickListener(v -> showEditForm(binding.getRoot().getContext(), restaurant));
        }
    }

    private static void showEditForm(Context ctx, Restaurant restaurant) {
        Intent intent = new Intent(ctx, RestaurantFormActivity.class);
        intent.putExtra("isUpdateMode", true);
        intent.putExtra("restaurant", restaurant);
        ctx.startActivity(intent);
        Context context = ctx;
        if(context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
