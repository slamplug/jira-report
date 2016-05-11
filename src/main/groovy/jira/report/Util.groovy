package jira.report

import grails.util.Pair
import groovy.util.logging.Log4j
import org.grails.plugins.google.visualization.util.DateUtil

@Log4j
class Util {

    private static int SPRINT_LENGTH = 14;

    def static fillSprints(def backlog, def teams) {
        List<Sprint> futureSprints = new ArrayList<>();

        for (Team team : teams) {
            log.info("processing team ${team.teamName}")

            def currSprint = getCurrentSprint(backlog.bValue, team.sprintPrefix)
            def sprintNumber = currSprint.bValue.intValue()

            def Sprint sprint = createSprint("${team.sprintPrefix} ${++sprintNumber}",
                    addDaysToDate(currSprint.aValue, 1),
                    addDaysToDate(currSprint.aValue, 1 + SPRINT_LENGTH))
            def Story prevStory = null

            def remainingCapacity = team.sprintCapacity

            for (Story story : backlog.aValue) {
                log.debug("STORY ${story.jiraKey}, labels ${story.labels} active ${story.inActiveSprint}")

                log.debug("sprint startDate ${sprint.startDate} , prevStory ${prevStory}")

                if (story.labels.contains(team.teamName) && !story.inActiveSprint) {

                    if (canFitInSprint(remainingCapacity, story.storyPoints)) {
                        log.debug("CANFITINSPRINT: adding story ${story.jiraKey}, " +
                                "size ${story.storyPoints},  " +
                                "capacity ${remainingCapacity}")

                        story.startDate = (prevStory != null) ? prevStory.endDate : sprint.startDate
                        story.endDate = addMinutesToDate(story.startDate, team.sprintCapacity, story.storyPoints)

                        log.debug("CANFITINSPRINT: after setting satrt and end dates, story $story")

                        sprint.stories.add(new Story(jiraKey: story.jiraKey,
                                summary: story.summary,
                                storyPoints: story.storyPoints,
                                inActiveSprint: story.inActiveSprint,
                                labels: story.labels,
                                startDate: story.startDate,
                                endDate: story.endDate,
                                portionInSprint: story.storyPoints))
                        sprint.storyPointCount += story.storyPoints

                        remainingCapacity -= story.storyPoints

                        log.debug("after add, new remaining capacity ${remainingCapacity}")

                    } else {
                        log.debug("adding story ${story.jiraKey}, " +
                                "size ${story.storyPoints},  " +
                                "capacity ${remainingCapacity}")

                        def pointsToAddToSprint = story.storyPoints
                        if (remainingCapacity > 0) {
                            log.debug("can fit part of story into sprint, remainingCapacity ${remainingCapacity}")

                            story.startDate = (prevStory != null) ? prevStory.endDate : sprint.startDate
                            story.endDate = sprint.endDate

                            log.debug("FILLUPSPRINT: after setting satrt and end dates, story $story")

                            sprint.stories.add(new Story(jiraKey: story.jiraKey,
                                    summary: story.summary,
                                    storyPoints: story.storyPoints,
                                    inActiveSprint: story.inActiveSprint,
                                    labels: story.labels,
                                    startDate: story.startDate,
                                    endDate: story.endDate,
                                    portionInSprint: remainingCapacity))
                            sprint.storyPointCount += remainingCapacity

                            pointsToAddToSprint -= remainingCapacity

                            //TODO - if points to add > sprint capacity add another sprint
                            while (pointsToAddToSprint > team.sprintCapacity) {
                                log.debug("story points will completly fill sprint")

                                futureSprints.add(sprint)

                                sprint = createSprint("${team.sprintPrefix} ${++sprintNumber}",
                                        addDaysToDate(sprint.endDate, 1),
                                        addDaysToDate(sprint.endDate, 1 + SPRINT_LENGTH))

                                story.startDate = sprint.startDate
                                story.endDate = sprint.endDate

                                sprint.stories.add(new Story(jiraKey: story.jiraKey,
                                        summary: story.summary,
                                        storyPoints: story.storyPoints,
                                        inActiveSprint: story.inActiveSprint,
                                        labels: story.labels,
                                        startDate: story.startDate,
                                        endDate: story.endDate,
                                        portionInSprint: team.sprintCapacity))

                                pointsToAddToSprint -= team.sprintCapacity
                            }
                        }

                        log.debug("now need to add ${pointsToAddToSprint} to new sprint")

                        futureSprints.add(sprint)

                        sprint = createSprint("${team.sprintPrefix} ${++sprintNumber}",
                                addDaysToDate(sprint.endDate, 1),
                                addDaysToDate(sprint.endDate, 1 + SPRINT_LENGTH))

                        story.startDate = sprint.startDate
                        story.endDate = addMinutesToDate(story.startDate, team.sprintCapacity, pointsToAddToSprint)

                        log.debug("ADDREMINDERTONEWSPRINT: after setting satrt and end dates, story $story")

                        sprint.stories.add(new Story(jiraKey: story.jiraKey,
                                summary: story.summary,
                                storyPoints: story.storyPoints,
                                inActiveSprint: story.inActiveSprint,
                                labels: story.labels,
                                startDate: story.startDate,
                                endDate: story.endDate,
                                portionInSprint: pointsToAddToSprint))
                        sprint.storyPointCount += pointsToAddToSprint

                        remainingCapacity = team.sprintCapacity - pointsToAddToSprint

                        log.debug("added, so remainingCapacity ${remainingCapacity}")
                    }
                    prevStory = story
                }
            }
            // add sprint being added to
            futureSprints.add(sprint)
        }

        log.debug("FUTURE_SPRINTS ${futureSprints}")

        def sprintTimelineColumns = [
                ['string', 'Sprint'],
                ['string', 'Story'],
                ['date', 'Start'],
                ['date', 'End']
        ]

        def sprintTimelineData = []

        futureSprints.each { sprint ->
            sprintTimelineData << buildSprintData(sprint)
            sprint.stories.each { story ->
                sprintTimelineData << buildSprintStoryData(sprint.name, story)
            }
        }

        new Pair(sprintTimelineColumns, sprintTimelineData)
    }

    private static buildSprintStoryData(String sprintName, Story story) {

        Calendar startCal = Calendar.getInstance()
        startCal.setTime(story.startDate)

        Calendar endCal = Calendar.getInstance()
        endCal.setTime(story.endDate)

        return [
                sprintName,
                story.jiraKey,
                DateUtil.createDate(
                        startCal.get(Calendar.YEAR),
                        startCal.get(Calendar.MONTH),
                        startCal.get(Calendar.DAY_OF_MONTH)
                ),
                DateUtil.createDate(
                        endCal.get(Calendar.YEAR),
                        endCal.get(Calendar.MONTH),
                        endCal.get(Calendar.DAY_OF_MONTH)
                )
        ]
    }

    private static buildSprintData(Sprint sprint) {

        Calendar startCal = Calendar.getInstance()
        startCal.setTime(sprint.startDate)

        Calendar endCal = Calendar.getInstance()
        endCal.setTime(sprint.endDate)

        return [
                sprint.name,
                sprint.name,
                DateUtil.createDate(
                        startCal.get(Calendar.YEAR),
                        startCal.get(Calendar.MONTH),
                        startCal.get(Calendar.DAY_OF_MONTH)
                ),
                DateUtil.createDate(
                        endCal.get(Calendar.YEAR),
                        endCal.get(Calendar.MONTH),
                        endCal.get(Calendar.DAY_OF_MONTH)
                )
        ]
    }

    private static canFitInSprint(def remaining, def size) {
        remaining >= size
    }

    private static Sprint createSprint(String name, Date startDate, Date endDate) {
        log.debug("create sprint ${name}, ${startDate}, ${endDate}")
        new Sprint(name: name, isActive: false, startDate: startDate, endDate: endDate)
    }

    private static Date addDaysToDate(Date date, int days) {
        Calendar c = Calendar.getInstance()
        c.setTime(date)
        c.add(Calendar.HOUR, days * 24)
        c.getTime()
    }

    private static Date addMinutesToDate(Date date, int sprintSize, int storySize) {
        Calendar c = Calendar.getInstance()
        c.setTime(date)
        c.add(Calendar.MINUTE, (int)((24 * 60 * SPRINT_LENGTH) * (storySize/sprintSize)))
        c.getTime()
    }

    private static Pair<Date, Integer> getCurrentSprint(List<Sprint> sprints, String sprintPrefix) {
        for (Sprint sprint : sprints) {
            if (sprint.name.startsWith(sprintPrefix)) {
                //System.out.println("[${sprint.endDate}] [${sprint.name.substring(sprintPrefix.length())}] [${sprintPrefix}]")
                return new Pair(sprint.endDate, new Integer(sprint.name.substring(sprintPrefix.length())))
                //return new Pair(sprint.endDate, 1)
            }
        }
        return new Pair(new Date(), 1)
    }
}
