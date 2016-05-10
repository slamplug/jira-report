package jira.report

import groovy.util.logging.Log4j

@Log4j
class TelcoReportController {

    private static final String PROJECT_KEY = "TAS"

    def JiraService jiraService;

    def index() {
        //def versions = jiraService.retrieveVersionsForProject(PROJECT_KEY)
        def versions = [ 'V3.0', 'V3.1', 'V3.2', 'V3.3' ]

        def telcoOneTeamName = 'TELCO1'
        def telcoOneTeamSprintPrefix = 'TAS 1 Sprint '
        def telcoOneSprintCapacity = 26

        def telcoTwoTeamName = 'TELCO2'
        def telcoTwoTeamSprintPrefix = 'TAS 2 Sprint '
        def telcoTwoSprintCapacity = 34

        render(view: "index", model: [
                "telcoOneTeamName": telcoOneTeamName,
                "telcoOneTeamSprintPrefix": telcoOneTeamSprintPrefix,
                "telcoOneSprintCapacity": telcoOneSprintCapacity,
                "telcoTwoTeamName": telcoTwoTeamName,
                "telcoTwoTeamSprintPrefix": telcoTwoTeamSprintPrefix,
                "telcoTwoSprintCapacity": telcoTwoSprintCapacity,
                "telcoVersions": versions
        ])
    }

    def report() {
        log.info("running report")

        def teams = []
        if (params.telcoOneInclude) {
            log.info("add team 1")
            teams << new Team(
                    teamName: params.telcoOneTeamName,
                    sprintPrefix: params.telcoOneTeamSprintPrefix,
                    sprintCapacity: Integer.parseInt(params.telcoOneSprintCapacity))
        }
        if (params.telcoTwoInclude) {
            teams << new Team(
                    teamName: params.telcoTwoTeamName,
                    sprintPrefix: params.telcoTwoTeamSprintPrefix,
                    sprintCapacity: Integer.parseInt(params.telcoTwoSprintCapacity))
        }

        def backlog = null
        if (params.telcoVersion.equals('All')) {
            backlog = jiraService.retrieveBacklogByProject(PROJECT_KEY)
        } else {
            backlog = jiraService.retrieveBacklogByProjectAndVersion(PROJECT_KEY, "V3.0")
        }

        log.debug("stories : " + backlog.aValue)
        log.debug("sprints : " + backlog.bValue)
        log.debug("teams : " + teams)

        def result = Util.fillSprints(backlog, teams)

        render(view: "telco", model: ["sprintTimelineColumns": result.aValue,
                                      "sprintTimelineData": result.bValue])
    }

    /*def reportAll() {

        log.debug("retrieve telco at scale backlog")

        def backlog = jiraService.retrieveBacklogByProject(PROJECT_KEY)

        log.debug("stories : " + backlog.aValue)
        log.debug("sprints : " + backlog.bValue)

        def teams = new ArrayList<Team>() {{
            add(new Team(teamName: 'TELCO1', sprintPrefix: 'TAS 1 Sprint ', sprintCapacity: 26))
            add(new Team(teamName: 'TELCO2', sprintPrefix: 'TAS 2 Sprint ', sprintCapacity: 34))
        }}

        def result = Util.fillSprints(backlog, teams)

        render(view: "telco", model: ["sprintTimelineColumns": result.aValue,
                                      "sprintTimelineData": result.bValue])
    }

    def reportByVersion() {

        log.debug("retrieve telco at scale backlog")

        def backlog = jiraService.retrieveBacklogByProjectAndVersion(PROJECT_KEY, "V3.0")

        log.debug("stories : " + backlog.aValue)
        log.debug("sprints : " + backlog.bValue)

        def teams = new ArrayList<Team>() {{
            add(new Team(teamName: 'TELCO1', sprintPrefix: 'TAS 1 Sprint ', sprintCapacity: 26))
            add(new Team(teamName: 'TELCO2', sprintPrefix: 'TAS 2 Sprint ', sprintCapacity: 34))
        }}

        def result = Util.fillSprints(backlog, teams)

        render(view: "telco", model: ["sprintTimelineColumns": result.aValue,
                                      "sprintTimelineData": result.bValue])
    }*/
}
