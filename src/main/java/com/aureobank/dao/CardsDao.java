package com.aureobank.dao;

import com.aureobank.model.Card;
import java.util.List;
import java.util.Optional;

public interface CardsDao {
    Optional<Card> findById(String id);
    List<Card> findByUser(String userId);
    void save(Card c);
    void delete(String id);
}
