package jira.report

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        get "/telcoReport/index"(controller: "telcoReport", action: "index")

        post "/telcoReport/report"(controller: "telcoReport", action: "run")

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
