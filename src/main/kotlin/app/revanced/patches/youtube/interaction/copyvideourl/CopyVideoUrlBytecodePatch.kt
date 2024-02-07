package app.revanced.patches.youtube.interaction.copyvideourl

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.misc.playercontrols.PlayerControlsBytecodePatch
import app.revanced.patches.youtube.video.information.VideoInformationPatch

@Patch(
    name = "Copy video URL",
    description = "Adds options to display buttons in the video player to copy video URLs.",
    dependencies = [
        CopyVideoUrlResourcePatch::class,
        PlayerControlsBytecodePatch::class,
        VideoInformationPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube", [
                "18.48.39",
                "18.49.37",
                "19.01.34",
                "19.02.39",
                "19.03.35",
                "19.03.36",
                "19.04.37"
            ]
        )
    ]
)
@Suppress("unused")
object CopyVideoUrlBytecodePatch : BytecodePatch() {
    private const val INTEGRATIONS_PLAYER_PACKAGE = "Lapp/revanced/integrations/youtube/videoplayer"
    private val BUTTONS_DESCRIPTORS = listOf(
        "$INTEGRATIONS_PLAYER_PACKAGE/CopyVideoUrlButton;",
        "$INTEGRATIONS_PLAYER_PACKAGE/CopyVideoUrlTimestampButton;"
    )

    override fun execute(context: BytecodeContext) {
        // Initialize buttons and inject visibility control
        BUTTONS_DESCRIPTORS.forEach { descriptor ->
            PlayerControlsBytecodePatch.initializeControl("$descriptor->initializeButton(Landroid/view/View;)V")
            PlayerControlsBytecodePatch.injectVisibilityCheckCall("$descriptor->changeVisibility(Z)V")
        }
    }
}