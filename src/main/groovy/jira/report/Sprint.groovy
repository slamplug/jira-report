package jira.report

class Sprint {

    String name
    boolean isActive
    Date startDate
    Date endDate
    List<Story> stories = new ArrayList<>()
    int storyPointCount = 0

    static constraints = {
    }

    @Override
    public String toString() {
        return "Sprint{" +
                "\nname='" + name + '\'' +
                "\n, isActive=" + isActive +
                "\n, startDate=" + startDate +
                "\n, endDate=" + endDate +
                "\n, stories=" + stories +
                "\n, storyPointCount=" + storyPointCount +
                '\n}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Sprint sprint = (Sprint) o

        if (isActive != sprint.isActive) return false
        if (endDate != sprint.endDate) return false
        if (name != sprint.name) return false
        if (startDate != sprint.startDate) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (isActive ? 1 : 0)
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0)
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0)
        return result
    }
}
