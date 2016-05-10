package jira.report

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        get "/report/index"(
                controller:"report",
                action:"index"
        )

        get "/report/telco"(
                controller:"report",
                action:"reportTelco"
        )

        get "/report/telco/v3"(
                controller:"report",
                action:"reportTelcoVersion3"
        )

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
