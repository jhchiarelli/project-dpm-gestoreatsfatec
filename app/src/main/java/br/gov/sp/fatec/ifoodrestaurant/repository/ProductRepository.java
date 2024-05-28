package br.gov.sp.fatec.ifoodrestaurant.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import br.gov.sp.fatec.ifoodrestaurant.models.Product;

public class ProductRepository {
    private FirebaseFirestore db;
    private CollectionReference collection;
    private static final String COLLECTION_NAME = "products";

    public ProductRepository() {
        db = FirebaseFirestore.getInstance();
        collection = db.collection(COLLECTION_NAME);
    }

    public void addProduct(Product product, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        collection.add(product)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void setProduct(Product product, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document().set(product)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getProduct(String id, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        collection.document(id).get()
                .addOnCompleteListener(onCompleteListener);
    }

    Task<DocumentSnapshot> getProduct(String id) {
        return collection.document(id).get();
    }

    public Task<QuerySnapshot> getAll() {
        return collection.get();
    }

    public void updateProduct(Product product, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(product.getId()).update("name", product.getName(), "description", product.getDescription(), "price", product.getPrice(), "imageUrl", product.getImageUrl(), "active", product.getActive(), "categoryId", product.getCategoryId(), "restaurantId", product.getRestaurantId(), "active", product.getActive())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void deleteProduct(String id, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(id).delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}
