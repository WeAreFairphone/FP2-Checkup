# Contributing to Fairphone Checkup

Make sure to have a look at the README.md file, it explains the development workflow for this
project.

All contributions and bugs can be submitted in our [bug tracker](https://bugtracker.fairphone.com/).
Even small issues such as spelling mistakes can be made into a bug.

> **Note:**
Testing the app on a Fairphone 2 running a public release requires 1) signing the app with the
Fairphone key, or 2) changing the package name to install it as a separate app.

## Building

> **Note:**
We assume that the remote repository of Fairphone is called **origin**.

Make sure that you have the latest changes available:
```git fetch origin --tags```

List the official release:
```git tag```

Check out the version you want to build, e.g. **release-1.2.3**:
```git checkout release-1.2.3```

Build:
```./gradlew clean build```

The unsigned app packages (release and debug) are located in `app/build/outputs/apk`. Test and lint
reports are generated and located in `app/build/reports/`.

## Developing

> **Note:**
We assume that the remote repository of Fairphone is called **origin**.

Make sure that you have the latest changes available:
```git fetch origin --tags```

Branch off of **origin/master** (replace **master** by a release branch to work on a hotfix):
```git checkout -b <branch name> --track origin/master```

Develop and play around. Commit.

Build:
```./gradlew clean build```

Push your changes for review (again, replace **master** by the branch you were working on):
```git push HEAD:refs/for/master```

Or, if you have git-review installed:
```git review --remote origin --track <branch name>```
