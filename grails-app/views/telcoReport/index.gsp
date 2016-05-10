<!doctype html>
<html>
<head>
    <meta name="layout" content="report"/>
    <title>Welcome to Reports</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />

    <style>
        #telco-input {
            padding: 20px;
        }
    </style>
</head>
<body>

    <div id="telco-input">
        <g:form name="report" url="[action:'report',controller:'telcoReport']">
            <table>
                <tr>
                <td>

                    <g:textField name="telcoOneTeamName" value="${telcoOneTeamName}" />

                    <g:textField name="telcoOneTeamSprintPrefix" value="${telcoOneTeamSprintPrefix}" />

                    <g:field type="number" name="telcoOneSprintCapacity" required="true" value="${telcoOneSprintCapacity}"/>

                    <g:checkBox name="telcoOneInclude" value="${telcoOneInclude}" checked="true"/>

                </td>
                </tr>
                <tr>
                <td>

                    <g:textField name="telcoTwoTeamName" value="${telcoTwoTeamName}" />

                    <g:textField name="telcoTwoTeamSprintPrefix" value="${telcoTwoTeamSprintPrefix}" />

                    <g:field type="number" name="telcoTwoSprintCapacity" required="true" value="${telcoTwoSprintCapacity}"/>

                    <g:checkBox name="telcoTwoInclude" value="${telcoTwoInclude}" checked="true"/>

                </td>
                </tr>
                <tr>
                <td>

                <g:select name="telcoVersion" noSelection="['All':'All']" from="${telcoVersions}" />

                </td>
                </tr>
                <tr>
                <td>

                    <g:submitButton name="run" value="Run" />

                </td>
                </tr>
            </table>
        </g:form>
    </div>

    <!--<a href="/report/telco/v3">Telco V3 Report</a>-->

</body>
</html>
