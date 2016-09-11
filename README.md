# github-activity-monitor

Summarise the status and latest activity of github repositories.
Optionally configure a GoCD server to included deployment status tracking
See application.conf.template for config options

## Running

through sbt:

    > sbt run

or with docker:

    > docker build -t github-activity-monitor .
    > docker run -p 8080:8080 github-activity-monitor

Handy docker commands while testing:

    # delete all stopped containers
    > docker ps -a -q | xargs docker rm

    # delete all untagged images
    > docker rmi $(docker images -q --filter "dangling=true")
    