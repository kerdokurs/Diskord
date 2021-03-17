package diskord.server;

import javax.persistence.NoResultException;
import java.util.List;

public interface Repository<T, ID> {
  T findOne(ID id) throws NoResultException;

  List<T> findAll() throws NoResultException;

  boolean save(T t) throws Exception;
}
