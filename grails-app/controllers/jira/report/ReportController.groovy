package jira.report

import groovy.util.logging.Log4j

@Log4j
class ReportController {

    def JiraService jiraService;

    def index() {

    }

    def reportTelco() {

        log.debug("retrieve telco at scale backlog")

        def backlog = jiraService.retrieveBacklogByProject("TAS")

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

    def reportTelcoVersion3() {

        log.debug("retrieve telco at scale backlog")

        def backlog = jiraService.retrieveBacklogByProjectAndVersion("TAS", "V3.0")

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
}
