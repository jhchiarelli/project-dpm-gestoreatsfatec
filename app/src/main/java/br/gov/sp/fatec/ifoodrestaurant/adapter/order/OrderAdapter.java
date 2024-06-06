package br.gov.sp.fatec.ifoodrestaurant.adapter.order;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.sp.fatec.ifoodrestaurant.databinding.OrderItemBinding;
import br.gov.sp.fatec.ifoodrestaurant.models.Order;
import br.gov.sp.fatec.ifoodrestaurant.utils.DataFormatter;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderItemBinding binding = OrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final OrderItemBinding binding;

        public OrderViewHolder(@NonNull OrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.tvIdOrder.setText("Id: " + order.getId());
            binding.tvDateOrder.setText(order.getDateOrder());
            binding.tvPaymentMethod.setText(order.getPaymentMethod());
            binding.tvTotalOrder.setText(DataFormatter.formatCurrency(order.getTotalOrder()));
            binding.btnDetalhes.setOnClickListener(v -> {
                Toast.makeText(binding.getRoot().getContext(), "Itens do pedido em processamento.", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
