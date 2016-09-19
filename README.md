# github-activity-monitor

Summarise the status and latest activity of github repositories.
Optionally configure a GoCD server to included deployment status tracking
See application.conf.template for config options

## Running

through sbt:

    > sbt run

or with docker:

    > sbt docker:publishLocal
    > docker run -p 8080:8080 github-activity-monitor:0.1.0-SNAPSHOT

Handy docker commands while testing:

    # delete all stopped containers
    > docker ps -a -q | xargs docker rm

    # delete all untagged images
    > docker rmi $(docker images -q --filter "dangling=true")
    

The webserver runs on port 8080 through sbt but 8090 through docker.
Nginx is then running on port 8080 to proxy through to the webserver or serve static content based on the URL
