package hanu.npr.messengerserver.repositories;

import java.util.List;

public interface CRUDRepository<I, M> {
    void addOne(M data);
    List<M> getAll();
    M getById(I id);
    void updateById(I id, M updateData);
    void deleteById(I id);
}
