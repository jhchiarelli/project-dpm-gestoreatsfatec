package br.gov.sp.fatec.ifoodrestaurant.adapter.category;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.gov.sp.fatec.ifoodrestaurant.R;
import br.gov.sp.fatec.ifoodrestaurant.activity.category.CategoryFormActivity;
import br.gov.sp.fatec.ifoodrestaurant.databinding.AdapterCategoryItemBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterCategoryItemBinding binding = AdapterCategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final AdapterCategoryItemBinding binding;

        public CategoryViewHolder(@NonNull AdapterCategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Category category) {
            binding.tvCategory.setText(category.getDescription());
            Glide.with(binding.getRoot().getContext())
                    .load(category.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_24)
                    .into(binding.ivLogo);
            binding.btEdit.setOnClickListener(v -> {
                showEditForm(binding.getRoot().getContext(), category);
            });
        }
    }

    private static void showEditForm(Context ctx, Category category) {
        Intent intent = new Intent(ctx, CategoryFormActivity.class);
        intent.putExtra("isUpdateMode", true);
        intent.putExtra("category", category);
        ctx.startActivity(intent);
        Context context = ctx;
        if(context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
