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

import br.gov.sp.fatec.ifoodrestaurant.models.Category;

public class CategoryRepository {
    private FirebaseFirestore db;
    private CollectionReference collection;
    private static final String COLLECTION_NAME = "categories";

    public CategoryRepository() {
        db = FirebaseFirestore.getInstance();
        collection = db.collection(COLLECTION_NAME);
    }

    public void addCategory(Category category, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        collection.add(category)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void setCategory(Category category, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document().set(category)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getCategory(String id, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        collection.document(id).get()
                .addOnCompleteListener(onCompleteListener);
    }

    public Task<DocumentSnapshot> getCategory(String id) {
        return collection.document(id).get();
    }

    public void updateCategory(Category category, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(category.getId()).update("description", category.getDescription(), "imageUrl", category.getImageUrl(), "active", category.getActive())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void deleteCategory(String id, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(id).delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getAll(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        collection.get()
                .addOnCompleteListener(onCompleteListener);
    }

    public Task<QuerySnapshot> getAllCategories() {
        return collection.get();
    }
}
