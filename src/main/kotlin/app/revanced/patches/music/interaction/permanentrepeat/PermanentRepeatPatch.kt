package app.revanced.patches.music.interaction.permanentrepeat

import app.revanced.util.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.music.interaction.permanentrepeat.fingerprints.RepeatTrackFingerprint

@Patch(
    name = "Permanent repeat",
    description = "Permanently remember your repeating preference even if the playlist ends or another track is played.",
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.apps.youtube.music",
            [
                "6.45.54",
                "6.51.53",
                "7.01.53",
                "7.02.52",
                "7.03.52",
            ]
        )
    ],
    use = false
)
@Suppress("unused")
object PermanentRepeatPatch : BytecodePatch(
    setOf(RepeatTrackFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        RepeatTrackFingerprint.result?.let {
            val startIndex = it.scanResult.patternScanResult!!.endIndex
            val repeatIndex = startIndex + 1

            it.mutableMethod.apply {
                addInstructionsWithLabels(
                    startIndex,
                    "goto :repeat",
                    ExternalLabel("repeat", getInstruction(repeatIndex))
                )
            }
        } ?: throw RepeatTrackFingerprint.exception
    }
}
