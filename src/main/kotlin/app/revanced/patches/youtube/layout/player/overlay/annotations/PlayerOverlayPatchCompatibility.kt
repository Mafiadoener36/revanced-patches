package app.revanced.patches.youtube.layout.player.overlay.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.google.android.youtube")])
@Target(AnnotationTarget.CLASS)
internal annotation class PlayerOverlayPatchCompatibility
