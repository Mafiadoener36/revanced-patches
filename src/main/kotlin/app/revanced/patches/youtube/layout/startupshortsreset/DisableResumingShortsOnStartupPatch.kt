package app.revanced.patches.youtube.layout.startupshortsreset

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.all.misc.resources.AddResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.youtube.layout.startupshortsreset.fingerprints.UserWasInShortsFingerprint
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.util.exception

@Patch(
    name = "Disable resuming Shorts on startup",
    description = "Adds an option to disable the Shorts player from resuming on app startup when Shorts were last being watched.",
    dependencies = [IntegrationsPatch::class, SettingsPatch::class, AddResourcesPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube", [
                "18.32.39",
                "18.37.36",
                "18.38.44",
                "18.43.45",
                "18.44.41",
                "18.45.43",
                "18.48.39",
                "18.49.37",
                "19.01.34",
                "19.02.39",
                "19.03.35"
            ]
        )
    ]
)
@Suppress("unused")
object DisableResumingShortsOnStartupPatch : BytecodePatch(
    setOf(UserWasInShortsFingerprint)
) {

    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/youtube/patches/DisableResumingStartupShortsPlayerPatch;"

    override fun execute(context: BytecodeContext) {
        AddResourcesPatch(this::class)

        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference("revanced_disable_resuming_shorts_player")
        )

        UserWasInShortsFingerprint.result?.apply {
            val moveResultIndex = scanResult.patternScanResult!!.endIndex

            mutableMethod.addInstructionsWithLabels(
                moveResultIndex + 1,
                """
                invoke-static { }, $INTEGRATIONS_CLASS_DESCRIPTOR->disableResumingStartupShortsPlayer()Z
                move-result v5
                if-eqz v5, :disable_shorts_player
                return-void
                :disable_shorts_player
                nop
            """
            )
        } ?: throw UserWasInShortsFingerprint.exception
    }
}
