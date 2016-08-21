# github-activity-monitor

Summarise user activity on github repositories.

Add an application.conf file:

    github {
      oauthToken = "myOauthTokenForGithub"
    }
    
    repos = [
      "yourOrganisation/githubRepo",
      "yourOrganisation/anotherGithubRepo"
    ]
    
    organisation = "yourOrganisation"
    monthsDataToRetrieve = 2