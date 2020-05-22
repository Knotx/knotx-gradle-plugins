## Updating Azure pipelines in all Knot.x repos

## Prerequisites
To be able to automatically create PR from the command line, please install [`hub`](https://hub.github.com/) 
and configure it with OAuth token provided to your GitHub account.

## Updating Azure config

1. Update `azure-pipelines.yml`
2. Run `sh update.sh` (you may use `-p` option in order to automatically create PR, see [Prerequisites](#prerequisites)).

If you didn't create PR automatically, please create them manually on each repository.
The branch name should be: `feature/azure-pipeline-upgrade-{TIMESTAMP}`.