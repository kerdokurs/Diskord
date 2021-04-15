package diskord.server.database;

import javax.persistence.EntityManager;

@FunctionalInterface
public interface Transaction<T> {
  T execute(final EntityManager em);
}
