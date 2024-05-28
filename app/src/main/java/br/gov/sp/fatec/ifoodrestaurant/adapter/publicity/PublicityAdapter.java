package br.gov.sp.fatec.ifoodrestaurant.adapter.publicity;

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
import br.gov.sp.fatec.ifoodrestaurant.activity.publicity.PublicityFormActivity;
import br.gov.sp.fatec.ifoodrestaurant.databinding.AdapterPublicityItemBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Publicity;
public class PublicityAdapter extends RecyclerView.Adapter<PublicityAdapter.PublicityViewHolder> {
    private List<Publicity> publicityads;

    public PublicityAdapter(List<Publicity> publicityads) {
        this.publicityads = publicityads;
    }

    @NonNull
    @Override
    public PublicityAdapter.PublicityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterPublicityItemBinding binding = AdapterPublicityItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PublicityViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull PublicityAdapter.PublicityViewHolder holder, int position) {
        Publicity publicity = publicityads.get(position);
        holder.bind(publicity);
    }

    @Override
    public int getItemCount() {
        return publicityads.size();
    }

    static class PublicityViewHolder extends RecyclerView.ViewHolder {
        private final AdapterPublicityItemBinding binding;

        public PublicityViewHolder(@NonNull AdapterPublicityItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Publicity publicity) {
            binding.tvName.setText(publicity.getName());
            binding.tvTarget.setText(publicity.getTargetLink());
            Glide.with(binding.getRoot().getContext())
                    .load(publicity.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_24)
                    .into(binding.ivLogo);
            binding.btEdit.setOnClickListener(v -> {
                showEditForm(binding.getRoot().getContext(), publicity);
            });
        }
    }

    private static void showEditForm(Context ctx, Publicity publicity) {
        Intent intent = new Intent(ctx, PublicityFormActivity.class);
        intent.putExtra("isUpdateMode", true);
        intent.putExtra("publicity", publicity);
        ctx.startActivity(intent);
        Context context = ctx;
        if(context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
