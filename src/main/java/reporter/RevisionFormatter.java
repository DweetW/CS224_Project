package reporter;

public class RevisionFormatter {

    public String format(Revision revision) {
        return revision.getTimestamp() + "  " + revision.getUser();
    }
}
