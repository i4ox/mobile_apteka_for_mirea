package ru.artlkv.aptekaformirea.controllers;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

import ru.artlkv.aptekaformirea.Application;
import ru.artlkv.aptekaformirea.Constants;
import ru.artlkv.aptekaformirea.models.Address;
import ru.artlkv.aptekaformirea.models.User;
import rx.Observable;

public class UserManager {

    public static Observable<User> getUserFromId(String id) {
        return Observable.create(sub -> {
            FirebaseDatabase.getInstance().getReference("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    sub.onNext(user);
                    sub.onCompleted();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    sub.onError(new Throwable(error.getMessage()));
                }
            });
        });
    }

    public static Observable<User> getUser() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        return getUserFromId(uid);
    }

    public static Observable<Address> getAddressFromId(String id) {
        return Observable.create(sub -> {
            FirebaseDatabase.getInstance().getReference("addresses").child(id).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Address address = snap.getValue(Address.class);
                        sub.onNext(address);
                        sub.onCompleted();
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    sub.onError(new Throwable(error.getMessage()));
                }
            });
        });
    }

    public static Observable<Void> updateAddress(Address address) {
        return Observable.create(sub -> {
            String TAG = Application.getGlobalContext().getClass().getSimpleName();
            String uniqueID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("addresses").child(uniqueID);
            addressRef.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DataSnapshot snap = snapshot.getChildren().iterator().next();
                    String addressKey = snap.getKey();

                    Log.d(TAG, "Address key: " + addressKey);

                    addressRef.child(addressKey).setValue(address, ((error, ref) -> {
                        if (error != null) {
                            Log.e(TAG, "Address not supported: " + error.getMessage());
                            sub.onError(new Throwable("Адрес не может быть обновлен"));
                        } else {
                            sub.onCompleted();
                        }
                    }));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    String TAG = Application.getGlobalContext().getClass().getSimpleName();
                    Log.e(TAG, "Address not supported: " + error.getMessage());
                    sub.onError(new Throwable("Адрес не может быть обновлен"));
                }
            });
        });
    }

    public static Observable<String> getAddressKey() {
        return Observable.create(sub -> {
            String TAG = Application.getGlobalContext().getClass().getSimpleName();
            String uniqueID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference(Constants.Path.ADDRESSES).child(uniqueID);
            addressRef.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DataSnapshot snap = snapshot.getChildren().iterator().next();
                    String addressKey = snap.getKey();
                    Log.d(TAG, "Address key: " + addressKey);

                    sub.onNext(addressKey);
                    sub.onCompleted();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    sub.onError(new Throwable(error.getMessage()));
                }
            });
        });
    }

    public static Observable<Void> updateUser(Map<String, Object> userMap) {
        String uniqueID = getUserRef().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Constants.Path.USERS).child(uniqueID);
        return Observable.create(subscriber -> {
            userRef.updateChildren(userMap, (firebaseError, firebase) -> {
                if (firebaseError != null) {
                    subscriber.onError(new Throwable("Вы не можете обновить пользователя"));
                } else {
                    subscriber.onCompleted();
                }
            });
        });
    }

    public static FirebaseUser getUserRef() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
