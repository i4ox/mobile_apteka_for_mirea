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

import java.io.File;
import java.util.Objects;

import ru.artlkv.aptekaformirea.Application;
import ru.artlkv.aptekaformirea.Constants;
import ru.artlkv.aptekaformirea.Utils;
import ru.artlkv.aptekaformirea.models.Order;
import rx.Observable;

public class OrderManager {
    public static Observable<String> createOrder(Order order) {
        return Observable.create(sub -> {
            String TAG = Application.getGlobalContext().getClass().getSimpleName();
            FirebaseDatabase ref = FirebaseDatabase.getInstance();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            String uniqueID = firebaseUser.getUid();

            DatabaseReference newOrderRef =  ref.getReference("orders").push();

            String newOrderKey = newOrderRef.getKey();

            order.setUniqueID(uniqueID);

            order.setOrderPath(newOrderKey);

            long timestamp = System.currentTimeMillis() / 1000L;


            newOrderRef.setValue(order, (databaseError, firebase) -> {
                if (databaseError != null) {
                    Log.e(TAG,"Неудалось создать заказ, id: " + order.getOrderID());
                    sub.onError(databaseError.toException());

                } else {
                    Log.i(TAG, "Заказ был создан, id: " + order.getOrderID());
                    newOrderRef.setPriority(-timestamp);
                    sub.onNext(firebase.getKey());
                    sub.onCompleted();
                }
            });
        });
    }

    public static void setCompleted(String key) {

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(key);

        DatabaseReference orderStatsRef = FirebaseDatabase.getInstance().getReference("order_stats");

        orderStatsRef.child("open").child(key).removeValue();

        orderStatsRef.child("completed").child(key).setValue(true);

        orderRef.child("is_completed").setValue(true);

    }

    public static Observable<Order> fetchOrder(String orderId) {
        return Observable.create(sub -> {
            FirebaseDatabase.getInstance().getReference(Constants.Path.ORDERS).orderByChild(Constants.Order.ORDER_ID)
                    .equalTo(orderId).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("fetchOrder", "Key: " + Objects.requireNonNull(dataSnapshot.getValue()));
                            try {
                                // get the first child of the list
                                Order order = dataSnapshot.getChildren().iterator().next().getValue(Order.class);
                                if (order != null) {
                                    //    Log.d("fetchOrder", "Order uid: " + order.getUid());
                                    sub.onNext(order);
                                    sub.onCompleted();
                                } else {
                                    sub.onError(new Throwable("C заказом возникла проблема"));
                                }
                            } catch (Exception e) {
                                sub.onError(e);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            sub.onError(databaseError.toException());
                        }
                    });
        });
    }

    public static Observable<Order> fetchOrderByKey(String key) {
        return Observable.create(sub -> {
            String TAG = Application.getGlobalContext().getClass().getSimpleName();
            FirebaseDatabase.getInstance().getReference(Constants.Path.ORDERS).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Order order = dataSnapshot.getValue(Order.class);
                        sub.onNext(order);
                        sub.onCompleted();
                    } catch (Exception e) {
                        Log.e(TAG, "У заказа возникла ошибка с ключом, key: " + key);
                        sub.onError(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String TAG = Application.getGlobalContext().getClass().getSimpleName();
                    Log.e(TAG, "У заказа возникла ошибка с ключом, key: %s" + key);
                    sub.onError(databaseError.toException());
                }
            });

        });
    }



    public static Observable<Void> deleteOrder(Order order) {

        String TAG = Application.getGlobalContext().getClass().getSimpleName();
        Observable<Void> isConnectedToInternet = Utils.isNetworkAvailable();
        Observable<Void> deleteOrderObservable = Observable.create(sub -> {

            FirebaseDatabase.getInstance().getReference(Constants.Path.ORDERS).child(order.getOrderPath()).removeValue((databaseError, firebase) -> {
                if (databaseError != null) {
                    // on order delete failed
                    Log.e(TAG, "Order delete failed on id " + order.getOrderID());
                    sub.onError(databaseError.toException());
                } else {
                    Log.i(TAG,"Order deleted, key: " + order.getOrderPath());
                    sub.onCompleted();
                }
            });

        });

        if (order.getPreURL().isEmpty()) {
            return isConnectedToInternet.concatWith(deleteOrderObservable);
        }

        return Observable.concat(isConnectedToInternet, deleteOrderObservable);
    }

    public static Observable<Void> deleteOrderByKey(String key) {
        String TAG = Application.getGlobalContext().getClass().getSimpleName();
        return fetchOrderByKey(key).concatMap(order -> {
            return Observable.create(sub -> {

                        FirebaseDatabase.getInstance().getReference(Constants.Path.ORDERS).child(key).removeValue((databaseError, firebase) -> {
                            if (databaseError != null) {
                                // on order delete failed
                                Log.e(TAG, "Order delete failed on key " + key);
                                sub.onError(databaseError.toException());
                            } else {
                                Log.i(TAG, "Order deleted, key: " + key);
                                sub.onCompleted();
                            }
                        });
            });
        });
    }

    /**
     * Confirms the order
     * @return void
     */

    public static Observable<Void> setOrderStatus(Order order, @Order.Status String status) {
        String TAG = Application.getGlobalContext().getClass().getSimpleName();
        return Observable.create(sub -> {
            FirebaseDatabase.getInstance().getReference(Constants.Path.ORDERS).child(order.getOrderPath())
                    .child(Constants.Order.STATUS).setValue(status, (databaseError, firebase) -> {

                        if (databaseError != null) {
                            Log.e(TAG, "Смена статуса заказа провалена" + status);
                            sub.onError(databaseError.toException());
                        } else {
                            sub.onCompleted();

                        }
                    });
        });
    }

}
