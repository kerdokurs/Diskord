package diskord.client;

public enum ChatFileType {
    IMAGE("IMAGE"),
    FILE("FILE");

    final String parameter;
    ChatFileType(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return parameter;
    }

}
