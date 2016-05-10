<%@ page import="org.grails.plugins.google.visualization.data.Cell; org.grails.plugins.google.visualization.util.DateUtil" %>
<html>
    <head>
        <title>Google Visualization API plugin</title>
        <meta name="layout" content="main" />
        <gvisualization:apiImport/>
    </head>
    <body>
       <gvisualization:timeLine elementId="sprint-timeline" columns="${sprintTimelineColumns}" data="${sprintTimelineData}" />

       <table cellpadding="2" cellspacing="0" width="100%">
           <tr>
               <td>
                   <div id="sprint-timeline" style='width: 100%; height: 750px;'></div>
               </td>
           </tr>
       </table>
    </body>
</html>