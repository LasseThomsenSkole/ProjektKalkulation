package projectmanament.model;

public enum Status {
    DONE("Done"),
    IN_PROGRESS("In Progress"),
    TO_DO("To Do"),
    ARCHIVED("Archived"),
    NOT_STARTED("Not Started");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}