package app.revanced.patches.youtube.video.speed.button

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.misc.playercontrols.BottomControlsResourcePatch
import app.revanced.util.ResourceGroup
import app.revanced.util.copyResources

@Patch(
    dependencies = [BottomControlsResourcePatch::class],
)
internal object PlaybackSpeedButtonResourcePatch : ResourcePatch() {
    override fun execute(context: ResourceContext) {
        context.copyResources(
            "speedbutton",
            ResourceGroup(
                "drawable",
                "revanced_playback_speed_dialog_button.xml",
            ),
        )

        BottomControlsResourcePatch.addControls("speedbutton")
    }
}
