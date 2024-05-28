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

import br.gov.sp.fatec.ifoodrestaurant.models.Publicity;

public class PublicityRepository {
    private FirebaseFirestore db;
    private CollectionReference collection;
    private static final String COLLECTION_NAME = "publicityads";

    public PublicityRepository() {
        db = FirebaseFirestore.getInstance();
        collection = db.collection(COLLECTION_NAME);
    }

    public void addPublicity(Publicity publicity, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        collection.add(publicity)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void setPublicity(Publicity publicity, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document().set(publicity)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getPublicity(String id, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        collection.document(id).get()
                .addOnCompleteListener(onCompleteListener);
    }

    public Task<DocumentSnapshot> getPublicity(String id) {
        return collection.document(id).get();
    }

    public void updatePublicity(Publicity publicity, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(publicity.getId()).update("name", publicity.getName(), "targetLink", publicity.getTargetLink(), "imageUrl", publicity.getImageUrl(), "active", publicity.getActive())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void deletePublicity(String id, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(id).delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getAll(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        collection.get()
                .addOnCompleteListener(onCompleteListener);
    }

    public Task<QuerySnapshot> getAll() {
        return collection.get();
    }

}
