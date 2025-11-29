package com.aureobank.dao.json;

import com.aureobank.dao.CardsDao;
import com.aureobank.model.Card;
import com.aureobank.util.JsonStore;
import java.util.*;

public class JsonCardsDao implements CardsDao {
    private final JsonStore store;

    public JsonCardsDao(JsonStore store) { this.store = store; }

    private Map<String, Card> load() { return store.readMap("cards.json", Card.class); }
    private void persist(Map<String, Card> map) { store.writeMap("cards.json", map); }

    @Override
    public Optional<Card> findById(String id) { return Optional.ofNullable(load().get(id)); }

    @Override
    public List<Card> findByUser(String userId) {
        List<Card> out = new ArrayList<>();
        for (Card c : load().values()) if (c.getUserId().equals(userId)) out.add(c);
        return out;
    }

    @Override
    public void save(Card c) {
        Map<String, Card> map = load();
        if (c.getId() == null) c.setId(UUID.randomUUID().toString());
        map.put(c.getId(), c);
        persist(map);
    }

    @Override
    public void delete(String id) {
        Map<String, Card> map = load();
        map.remove(id);
        persist(map);
    }
}
