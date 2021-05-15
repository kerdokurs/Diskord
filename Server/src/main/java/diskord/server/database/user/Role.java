package diskord.server.database.user;

public enum Role {
  USER("USER"),
  ADMIN("ADMIN"),
  OWNER("OWNER");

  private final String name;

  Role(final String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
