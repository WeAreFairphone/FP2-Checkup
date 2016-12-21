# Checkup

An Android app that allows end-users to test their Fairphone 2 features.

## Development flow

### Branches

#### master

The up-to-date development branch.
It should always be build-able.

Solo commits (not part of a feature branch nor a bugfix branch) should be avoided.
Version bumping is the exception and happens directly on the branch.

> Accepts completed feature branches -that will lead to a new release- and cherry-picks of bugfix branches -to be integrated forth-.

#### release-\<major\>.\<minor\>

A release branch coming off master.

Each tagged release **release-_X_._Y_._Z_** can be built and will be equivalent to what what released as this version.
Fingerprint will vary based on the builder, features and version will not.

> Only accepts bugfix branches that then bumps the revision number.

#### fix_CHK-\<issue ID\>

A bugfix branch coming off a **release-_X_._Y_** branch.

Work in progress until merged back to the release branch.

#### ft_CHK-\<issue ID\>

A feature branch coming off **master**.

Work in progress until merged back to **master** (will be integrated in a future release).

### Developing

New features are implemented in feature branches -and therefore must have an issue ID- that are merged back to **master** when complete.

> **Note:**
A feature branch must not be merged into a release branch.
The release branch scope is restricted to bugfixing.

### Releasing

When **master** is ready to be released, a new release branch is created with either the _major_ or the _minor_ version bumped (see the rules in the next section).

Bumping the version **must** happen in **master** before branching out.
That way, the **master** branch can be built and installed on previous releases and is still considered part of the next (now on-going) release.

> **FIXME:**
The version of **master** should be clearly different from a release. Having a higher version allows us to -always- be able to install **master** on top.

Each release is tagged following **release-\<major\>.\<minor\>.\<revision\>** and belongs to a **release-\<major\>.\<minor\>** branch.

### Fixing releases

Because the bugfix branches **fix_CHK-_N_** are related to specific releases, they are branched off and merged back to specific release branches.

Once merged back in the **release-_X_._Y_** branch, the minor version **_Z_** is then bumped with a new commit and a new tag **release-_X_._Y_._Z_** is created.

If the fix can be applied to **master** and other release branches, the bugfix branch **fix_CHK-_N_** is forth- and back-ported as required.
Once again with release branches, the minor version needs to be bumped. No version is bumped on **master**.

## Versioning scheme

The app version (a.k.a Version Code) is based on the following scheme:

* Major version number
* Minor version number
* Revision number

The rules are as follow:

* The major version uses two digits
* The minor version uses two digits
* The revision uses two digits
* The major version should be bumped when backward compatibility is broken
* The minor version should be bumped when introducing new features
* The revision should be bumped whenever there is a (simple/hot) fix (from an issue-fixing branch)

The readable app version (a.k.a Version Name) is based on the app version with the following schemes:

* Public releases are named after \<major version\>.\<minor version\>.\<revision\>
