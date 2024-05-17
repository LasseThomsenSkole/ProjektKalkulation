package projectmanagement.model;

public enum Status {
    DONE("Afsluttet"),
    IN_PROGRESS("påbegyndt"),
    TODO("To do"),
    ARCHIVED("Arkiveret"),
    NOT_STARTED("Ikke påbegyndt");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}