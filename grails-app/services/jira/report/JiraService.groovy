package jira.report

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.domain.BasicIssue
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.Project
import com.atlassian.jira.rest.client.api.domain.SearchResult
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.atlassian.util.concurrent.Promise
import grails.transaction.Transactional
import grails.util.Pair
import groovy.util.logging.Log4j
import jdk.nashorn.internal.runtime.Version
import org.codehaus.jettison.json.JSONArray

import java.text.SimpleDateFormat

@Transactional
@Log4j
class JiraService {

    private final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory()

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(
            URI.create("https://first-utility.atlassian.net"),
            "api.user",
            "Connect01!")

    /*
    start of just in to test

    def Pair<List<Story>, List<Sprint>> retrieveBacklogByProjectAndVersion(def projectKey, def version) {
        List<Story> stories = new ArrayList<>();
        List<Sprint> sprints = new ArrayList<>();

        stories.add(createStory('TAS-212', 'Check service availability for MPF (standard BB) - using Gold ALK', 13, true, 'TELCO1'))
        stories.add(createStory('TAS-255', 'TECH - Store MPF (standard BB) service availability details (REGISTRATION Database) (child of TAS-7)', 3, true, 'TELCO1'))
        stories.add(createStory('TAS-254', 'TECH - Store MPF (standard BB) service availability details (FACT Database) (child of TAS-7)', 5, true, 'TELCO1'))
        stories.add(createStory('TAS-252', 'TECH - Store MPF (standard BB) service availability details (REGISTRATION Service implementation) (child of TAS-7)', 5, true, 'TELCO1'))
        stories.add(createStory('TAS-253', 'TECH -Store MPF (standard BB) service availability details (FACT Service implementation) (child of TAS-7)', 8, false, 'TELCO1'))
        stories.add(createStory('TAS-329', 'TECH - Develop Business Service for Address Matching (TAS-5)', 21, false, 'TELCO2'))
        stories.add(createStory('TAS-342', 'TECH- Develop Business Service for Line Characteristics Service (child of TAS-288)', 21, false, 'TELCO2'))
        stories.add(createStory('TAS-344', 'TECH- Develop Business Service for Product Availability Service (child of TAS-288)', 21, false, 'TELCO2'))
        stories.add(createStory('TAS-13', 'Check address details - using postcode', 8, false, 'TELCO2'))
        stories.add(createStory('TAS-15', 'Handle and translate errors returned by the address matching pre-provisioning check into customer-friendly language', 3, true, 'TELCO2'))
        stories.add(createStory('TAS-347', 'TECH- Create Telco Provisioning Order Adaptor (child of TAS-38)', 21, false, 'TELCO2'))
        stories.add(createStory('TAS-14', 'Store address matching details - where an individual address is selected', 3, false, 'TELCO2'))
        stories.add(createStory('TAS-16', 'Confirm whether customer can retain their CLI', 3, false, 'TELCO2'))
        stories.add(createStory('TAS-345', 'TECH- Create First Utility Telco Order business service - write order to d-b and place on queue (child of TAS-38)', 21, false, 'TELCO1'))
        stories.add(createStory('TAS-346', 'TECH- Create Telco Provisioning Order Creator service (child of TAS-38)', 34, false, 'TELCO1'))
        stories.add(createStory('TAS-53', 'Store Telco order', 2, false, 'TELCO1'))
        stories.add(createStory('TAS-44', 'Store MPF Migrate Order Details - when a standard copper (MPF) BB product selected', 2, false, 'TELCO1'))
        stories.add(createStory('TAS-39', 'Place MPF Provide Takeover Order - when a standard copper (MPF) BB product selected', 13, false, 'TELCO2'))
        stories.add(createStory('TAS-45', 'Store MPF Provide Takeover Order Details - when a standard copper (MPF) BB product selected', 3, false, 'TELCO2'))
        stories.add(createStory('TAS-207', 'Refine Deployment Pipeline', 1, false, 'Technical, TELCO1, Story'))
        stories.add(createStory('TAS-37', 'Identify Relevant TT Order Type', 5, false, 'TELCO1'))

        sprints.add(createSprint('TAS 1 Sprint 4',true,'2016-04-27T10:56:43.573+01:00','2016-05-10T18:00:00.000+01:00'))
        sprints.add(createSprint('TAS 2 Sprint 2',true,'2016-05-04T09:00:00.000+01:00','2016-05-17T18:00:00.000+01:00'))

        new Pair<>(stories,sprints)
    }

    def createStory(String id, String summary, int points, boolean inActiveSprint, String labels) {
        new Story(jiraKey: id, summary: summary, storyPoints: points, inActiveSprint: inActiveSprint,
                labels: new HashSet<>(Arrays.asList(labels.split(","))))
    }

    def createSprint(String name, boolean isActive, String startDate, String endDate) {
        new Sprint(name: name, isActive: isActive, startDate: sdf.parse(startDate), endDate: sdf.parse(endDate))
    }

    end of in to test
     */

    def Pair<List<Story>, List<Sprint>> retrieveBacklogByProject(def projectKey) {
        log.info("retrieve backlog by project $projectKey")

        def SearchResult result = jiraSearchClientSearchJQL(
                "project = $projectKey AND type = Story AND status != Done " +
                        //"AND \"Story Points\" != 0 AND \"Story Points\" != 55 AND \"Story Points\" is not EMPTY " +
                        "AND \"Story Points\" != 0 AND \"Story Points\" is not EMPTY " +
                        "ORDER BY Rank ASC").claim()

        getIssueDetails(result.issues)
    }

    def Pair<List<Story>, List<Sprint>> retrieveBacklogByProjectAndVersion(def projectKey, def version) {
        log.info("retrieve backlog by project $projectKey and version $version")

        def SearchResult result = jiraSearchClientSearchJQL(
                "project = $projectKey AND type = Story AND fixVersion = $version AND status != Done " +
                        //"AND \"Story Points\" != 0 AND \"Story Points\" != 55 AND \"Story Points\" is not EMPTY " +
                        "AND \"Story Points\" != 0 AND \"Story Points\" is not EMPTY " +
                        "ORDER BY Rank ASC").claim()

        getIssueDetails(result.issues)
    }

    def retrieveVersionsForProject(def projectKey) {
        log.info("retrieve versions by project $projectKey")

        def project = projectClientGetProjectByProjectKey(projectKey).claim()
        def iterator = project.versions.iterator()
        iterator.forEachRemaining{ version ->
            System.out.println("version name : ${version.name}")
        }
    }

    private def Pair<List<Story>, List<Sprint>> getIssueDetails(Iterable<BasicIssue> issues) {
        List<Story> stories = new ArrayList<>();
        List<Sprint> sprints = new ArrayList<>();
        Iterator<BasicIssue> it = issues.iterator()
        while (it.hasNext()) {
            BasicIssue basicIssue = (BasicIssue)it.next();
            Issue issue = restClient.issueClient.getIssue(basicIssue.key).claim()

            def sprint = isInActiveSprint(issue)
            if (sprint != null && !sprints.contains(sprint)) {
                sprints.add(sprint)
            }

            stories.add(new Story(
                    jiraKey: issue.key,
                    summary: issue.summary,
                    storyPoints: (int)issue.getFieldByName("Story Points").value,
                    inActiveSprint: (sprint != null),
                    labels: issue.labels
            ))
        }
        new Pair<>(stories,sprints)
    }

    private static def Sprint isInActiveSprint(Issue issue) {
        if (issue.getFieldByName("Sprint").value != null &&
                ((String)((JSONArray)issue.getFieldByName("Sprint").value).get(0)).contains("state=ACTIVE")) {

            String[] parts = ((String)((JSONArray)issue.getFieldByName("Sprint").value).get(0)).split(",")

            return new Sprint(
                    name: parts[3].split("=")[1],
                    isActive: true,
                    startDate: sdf.parse(parts[4].split("=")[1]),
                    endDate: sdf.parse(parts[5].split("=")[1])
            )
        }
        return null
    }

    private def Promise<SearchResult> jiraSearchClientSearchJQL(final String query) {
        restClient.searchClient.searchJql(query)
    }

    private def Promise<Project> projectClientGetProjectByProjectKey(final String projectKey) {
        restClient.projectClient.getProject(projectKey)
    }
}
