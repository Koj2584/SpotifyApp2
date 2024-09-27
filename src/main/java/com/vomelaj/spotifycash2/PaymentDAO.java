package com.vomelaj.spotifycash2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface PaymentDAO {
    @Insert
    void insert(Payment payment);

    @Query("SELECT * FROM payments")
    List<Payment> getAllPayments();

    @Query("SELECT * FROM payments WHERE dluznikId = :debtorId")
    List<Payment> getPaymentsForDebtor(int debtorId);

    @Delete
    void delete(Payment payment);

    @Query("SELECT * FROM payments WHERE id = :id")
    Payment getPaymentById(int id);
}
