package app.revanced.patches.music.misc.microg.patch.resource

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patches.music.misc.microg.shared.Constants.MUSIC_PACKAGE_NAME
import app.revanced.patches.music.misc.microg.shared.Constants.REVANCED_MUSIC_APP_NAME
import app.revanced.patches.music.misc.microg.shared.Constants.REVANCED_MUSIC_PACKAGE_NAME
import app.revanced.patches.music.misc.microg.shared.Constants.SPOOFED_PACKAGE_NAME
import app.revanced.patches.music.misc.microg.shared.Constants.SPOOFED_PACKAGE_SIGNATURE
import app.revanced.util.microg.MicroGManifestHelper
import app.revanced.util.microg.MicroGResourceHelper

@Description("Resource patch to allow YouTube Music ReVanced to run without root and under a different package name.")
class MicroGResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        // update manifest
        MicroGResourceHelper.patchManifest(
            context,
            MUSIC_PACKAGE_NAME,
            REVANCED_MUSIC_PACKAGE_NAME,
            REVANCED_MUSIC_APP_NAME
        )

        // add metadata to the manifest
        MicroGManifestHelper.addSpoofingMetadata(
            context,
            SPOOFED_PACKAGE_NAME,
            SPOOFED_PACKAGE_SIGNATURE
        )
    }
}