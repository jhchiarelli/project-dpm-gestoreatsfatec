package br.gov.sp.fatec.ifoodrestaurant.adapter.product;

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
import br.gov.sp.fatec.ifoodrestaurant.activity.product.ProductFormActivity;
import br.gov.sp.fatec.ifoodrestaurant.databinding.AdapterProductItemBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Product;
import br.gov.sp.fatec.ifoodrestaurant.utils.DataFormatter;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterProductItemBinding binding = AdapterProductItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product category = products.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final AdapterProductItemBinding binding;

        public ProductViewHolder(@NonNull AdapterProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product) {
            binding.tvName.setText(product.getName());
            binding.tvDescription.setText(product.getDescription());
            binding.tvPrice.setText(DataFormatter.formatCurrency(product.getPrice()));

            Glide.with(binding.getRoot().getContext())
                    .load(product.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_24)
                    .into(binding.ivProduct);

            binding.btDetails.setOnClickListener(v -> {
                showEditForm(binding.getRoot().getContext(), product);
            });
        }
    }

    private static void showEditForm(Context ctx, Product product) {
        Intent intent = new Intent(ctx, ProductFormActivity.class);
        intent.putExtra("isUpdateMode", true);
        intent.putExtra("product", product);
        ctx.startActivity(intent);
        Context context = ctx;
        if(context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}

