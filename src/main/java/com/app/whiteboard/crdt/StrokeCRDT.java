package com.app.whiteboard.crdt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class StrokeCRDT {
    private final Map<String, Stroke> adds = new ConcurrentHashMap<>();
    private final Set<String> tombstones = Collections.newSetFromMap(new ConcurrentHashMap<>());


    public void addStroke(Stroke s) {
        if (!tombstones.contains(s.getId())) {
            adds.putIfAbsent(s.getId(), s);
        }
    }


    public void removeStroke(String strokeId) {
        adds.remove(strokeId);
        tombstones.add(strokeId);
    }


    public void clearAll(long clearTimestamp) {
        adds.keySet().forEach(tombstones::add);
        adds.clear();
    }


    public Collection<Stroke> getAllStrokes() {
        return Collections.unmodifiableCollection(adds.values());
    }


    public void merge(StrokeCRDT remote) {
        remote.tombstones.forEach(id -> {
            adds.remove(id);
            tombstones.add(id);
        });
        remote.adds.forEach((id, stroke) -> {
            if (!tombstones.contains(id)) {
                adds.putIfAbsent(id, stroke);
            }
        });
    }
}
