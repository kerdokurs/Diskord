package diskord.server.jpa;

import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Tegemist on JPA (Java Persistence API) repo ülesehtusega.
 * Sellega suhtleme andmebaasiga. Iga repo implementeerib oma
 * vajadused ise.
 *
 * @param <T>  klass
 * @param <ID> klassi id (unikaalne)
 */
public interface Repository<T, ID> {
  /**
   * Leiab ühe isendi andmebaasist
   *
   * @param id otsitava isendi id
   * @return tagastab isendi või <code>null</code>, kui seda ei leidu
   */
  T findOne(@NotNull final ID id);

  /**
   * Leiab kõik isendid
   *
   * @return kõi kisendid või tühja listi, kui neid pole
   */
  List<T> findAll() throws NoResultException;

  /**
   * Salvestab isendi andmebaasi
   *
   * @param t salvestatav isend
   * @return <code>true</code>, kui suudeti salvestada, <code>false</code> muul juhul
   */
  boolean save(@NotNull T t);
}
