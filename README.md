# Fairphone Checkup

An Android app that allows end users to test the features of their Fairphone 2.

> **Note:**
This branch (**prebuilts**) only contains prebuilts, check out **master** for the up-to-date
development branch. Make sure to have a look at the documentation there, it explains the development
workflow.

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

> **FIXME:**
The prebuilts tags should be signed by the build system.
