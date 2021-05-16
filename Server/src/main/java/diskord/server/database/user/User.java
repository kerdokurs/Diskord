package diskord.server.database.user;

import diskord.server.crypto.Hash;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "users")
public class User {
  @Id
  @Getter
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(
      name = "id",
      unique = true,
      nullable = false,
      updatable = false
  )
  private UUID id;

  @Getter
  @Column(
      name = "username",
      unique = true
  )
  @Size(
      min = 5,
      message = "kasutajanimi peab olema vähemalt 5-tähemärgi pikkune"
  )
  private String username;

  @Getter
  @Setter
  @NotNull
  @Column(
      name = "password",
      nullable = false
  )
  private String password;

  @Getter
  @Setter
  @NotNull
  @ElementCollection
  @Column(
    name = "joined_servers",
    nullable = false
  )
  private Set<UUID> joinedServers;

  @Getter
  @Setter
  @NotNull
  @ElementCollection
  @Column(
    name = "admin_servers",
    nullable = false
  )
  private Set<UUID> privilegedServers;

  @Getter
  @CreationTimestamp
  @Column(
      name = "created_at"
  )
  private Date createdAt;

  @Getter
  @Setter
  @UpdateTimestamp
  @Column(
      name = "updated_at"
  )
  private Date updatedAt;

  public User(final String username, final String password, final Role role) {
    this.username = username;
    this.password = Hash.hash(password);
    this.joinedServers = new HashSet<>();
    this.privilegedServers = new HashSet<>();

  }

  /**
   * This method updates the hashmap that is stored in the serverPrivileges column for the User row in the db.
   * It checks if the user has already joined the server with the given serverId, if not, the method
   * will add the server id to the map as a key and the corresponding Role as value.
   * @param serverId The server id that the user is joining
   * @param role
   */
//  public void setPrivilegesMap(String serverId, Role role) {
//    try{
//      if(!privilegesMap.containsKey(serverId)){
//        privilegesMap.put(serverId, role);
//      } else {
//        System.out.println("User has already joined this server. [Server id: " + serverId + " ]");
//      }
//    } catch (Exception e){
//      System.out.println("Problem with the User's server privileges map."); //TODO replace with logger
//      e.printStackTrace();
//    }
//  }
//
//  public void updatePrivileges(String serverId, Role newRole){
//    try{
//      if(privilegesMap.containsKey(serverId)){
//        privilegesMap.put(serverId, newRole);
//      } else {
//        setPrivilegesMap(serverId, newRole);
//      }
//    } catch (Exception e){
//      System.out.println("Problem updating user role in the server.");
//      e.printStackTrace();
//    }
//  }
  public User() {
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        '}';
  }
}
