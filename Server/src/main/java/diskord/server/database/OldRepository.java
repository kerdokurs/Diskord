package diskord.server.database;

import javassist.NotFoundException;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Tegemist on JPA (Java Persistence API) repo ülesehtusega.
 * Sellega suhtleme andmebaasiga. Iga repo implementeerib oma
 * vajadused ise.
 *
 * @param <T>  klass
 * @param <ID> klassi id tüüp (unikaalne)
 */
public interface OldRepository<T, ID> {
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
  List<T> findAll();

  /**
   * Salvestab isendi andmebaasi
   *
   * @param t salvestatav isend
   * @return <code>true</code>, kui suudeti salvestada, <code>false</code> muul juhul
   */
  boolean save(@NotNull T t);

  /**
   * Kustutab isendi andmebaasist
   *
   * @param id kustutava isendi id
   * @return <code>true</code>, kui kustutamine õnnestus, <code>false</code> muul juhul
   * @throws NotFoundException kui kasutajat ei leitud
   * TODO: Kas boolean tagastustüüpi on sel juhul üldse vaja?
   */
  boolean delete(@NotNull ID id) throws NotFoundException;
}
