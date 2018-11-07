# Fairphone Checkup

An Android app that allows end users to test the features of their Fairphone 2.

If you are looking for build artifacts, please have a look at the **prebuilts** branch.

## Versioning scheme

The app version (a.k.a. version code) is an integer based on the following scheme:

* `<major version><minor version><revision>`

The rules are as follows:

* The major version uses two digits
* The minor version uses two digits, padded with a zero if necessary
* The revision uses two digits, padded with a zero if necessary
* The major version should be bumped when backward compatibility is broken
* The minor version should be bumped when introducing new features
* The revision should be bumped when one or more hotfixes are ready to be deployed

The visible app version (a.k.a. version name) is a string based on the version code, with the
following scheme:

* `<major version>.<minor version>.<revision>`

> **Note:**
The version code features leading zeroes between numbers so that it matches the full pattern, the
version name does not. A release with a version code of “10203” is named “1.2.3”.

## Development flow

### The master branch

The **master** branch is the up-to-date development branch. It should always be buildable.

> Only completed feature branches that will lead to a new (major or minor) release, as well as
cherry-picks of hotfixes to be integrated forward, can be merged into the **master** branch.

### Developing features

New features are implemented in dedicated branches that are coming from and merged back into
**master** when complete.

They are named after the issue ID they are solving. No feature should be implemented without a
logged issue backing it.

> **Note:**
A feature branch must not be merged into a release branch. The release branch scope is restricted to
bug fixing (hotfix).

### Releasing

When **master** is ready to be released, a new commit is created with either the _major_ or the
_minor_ version increased (cf. versioning scheme).

Increasing the version **must** happen in **master**. That commit represents the new release.

Each release is tagged following the `release-<major>.<minor>.0` scheme and a new release branch
**release-_X_._Y_** is created from there. The release branch scope is strictly limited to hotfixing.

> New release branches **release-_X_._Y_** begin at the release commit **release-_X_._Y_.0** from
**master**.

> **Note:**
The _revision_ number cannot be bumped in **master** as it is only applicable to hotfixes, and those
do not happen in **master** but in release branches.

> **FIXME:**
The release tags should be signed off by a Fairphone developer.

### Fixing releases

Bug fixing is relative to an identified release, it happens on a specific release branch. We only
consider hotfixes, design flaws and higher level considerations should be handled as new features.
A hotfix is usually a single commit.

If the hotfix can be applied to **master** or other release branches, the hotfix is cherry-picked
whenever possible to keep the relationship to the original commit. Note that the version number is
not bumped on **master** when integrating a fix.

The current release revision **_Z_** is eventually bumped with a new commit, and a new release
(**release-_X_._Y_._Z+1_**) is created.

### Distribution

Releasing a new version happens at the code level. We now use a prebuilt approach to have a shared
release between the different operating system flavours running on the Fairphone 2. This approach
also allows for OTA updates through app markets.

The **prebuilts** branch versions the official build artifacts from a named release (in the form of
**prebuilts-_X_._Y_._Z_**), signed with the widely known Fairphone key. They match a source code
release (**release-_X_._Y_._Z_**) (from the **master** branch).

Updates to prebuilts take the form of **prebuilts-_X_._Y_._Z_-MR_N_** where **_N_** is a natural
number denoting the update number. Such updates can only happen if the change is limited to
the build system. If a source code change is needed, its version cannot be maintained and thus a new
release is required.

> **Note:**
There is only one release supported at a given time, hence using a single branch to version the
prebuilts artifacts is not an issue.

## Building

Checkup can be built using _Android Studio_ or directly with Gradle.

### Android Studio

In order to build in _Android Studio_, import the root of the Git repository
as a new project and use the features of the IDE to edit, build, run, and
debug _Checkup_.


### Commandline

To build from commandline you will need the Android SDK and Gradle.
Follow the
[instructions in Android documentation.](https://developer.android.com/studio/build/building-cmdline)


## Signing for Release

By default both _debug_ and _release_ build variants are signed using default
keys included in the Android SDK. To sign the app for release, you need

* a keystore containing the release key and
* a configuration file describing how to access the key.

The release key can be put in a keystore using a
[script in `vendor/fairphone/tools/`](https://review.fairphone.software:29443/3494).

Point the build system to the keystore using a configuration file which includes
the path to the keystore, the alias of the key, and passwords for keystore and
key. The file `keystore.properties` needs to be at the root of the Git
repository and contain information like:

    storePassword=<pwd-of-keystore>
    keyPassword=<pwd-of-key-in-keystore>
    keyAlias=CheckupReleaseKey
    storeFile=../relative/path/to/checkup.keystore

You can verify that Gradle can find and open the key by checking the output of
`gradlew signingReport`. The sections for `release` and `releaseUnitTest`
variants should look similar to:

    Variant: release
    Config: release
    Store: /absolute/path/to/checkup.keystore
    Alias: CheckupReleaseKey
    MD5: 7D:F2:FC:AA:FD:75:F3:83:5C:ED:AE:63:B7:F2:AD:AB
    SHA1: 30:63:51:FB:98:33:47:1A:26:00:2D:58:C7:F1:1D:76:DE:F0:34:8E
    Valid until: Monday, November 24, 2042

If configuration is correct, `gradlew assembleRelease` can be used to create the
signed app at `./app/build/outputs/apk/release/app-release.apk`.