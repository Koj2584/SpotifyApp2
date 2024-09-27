package com.vomelaj.spotifycash2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DluznikDAO {
    @Insert
    void insert(Dluznik dluznik);

    @Query("SELECT * FROM dluznici")
    List<Dluznik> getAllDluznici();

    @Delete
    void delete(Dluznik dluznik);

    @Query("SELECT * FROM dluznici WHERE id = :id")
    Dluznik getDluznikById(int id);

    @Query("SELECT * FROM dluznici WHERE name = :name")
    Dluznik getDluznikByName(String name);

    @Query("DELETE FROM dluznici")
    void deleteAll();

    @Query("DELETE FROM dluznici WHERE name = :name")
    void deleteByName(String name);

    @Update
    void update(Dluznik dluznik);
}
