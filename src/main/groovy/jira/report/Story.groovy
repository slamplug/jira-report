package jira.report

class Story {

    String jiraKey
    String summary
    int storyPoints
    boolean inActiveSprint
    Set<String> labels
    Date startDate
    Date endDate
    int portionInSprint

    static constraints = {
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Story story = (Story) o

        if (inActiveSprint != story.inActiveSprint) return false
        if (storyPoints != story.storyPoints) return false
        if (jiraKey != story.jiraKey) return false
        if (labels != story.labels) return false
        if (summary != story.summary) return false

        return true
    }

    int hashCode() {
        int result
        result = (jiraKey != null ? jiraKey.hashCode() : 0)
        result = 31 * result + (summary != null ? summary.hashCode() : 0)
        result = 31 * result + storyPoints
        result = 31 * result + (inActiveSprint ? 1 : 0)
        result = 31 * result + (labels != null ? labels.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "Story{" +
                "\njiraKey='" + jiraKey + '\'' +
                "\n, summary='" + summary + '\'' +
                "\n, storyPoints=" + storyPoints +
                "\n, portionInSprint=" + portionInSprint +
                "\n, inActiveSprint=" + inActiveSprint +
                "\n, labels=" + labels +
                "\n, startDate=" + startDate +
                "\n, endDate=" + endDate +
                '\n}';
    }
}
