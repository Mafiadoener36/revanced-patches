package app.revanced.patches.shared.misc.integrations

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.fingerprint.MethodFingerprint
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patches.shared.misc.integrations.BaseIntegrationsPatch.IntegrationsFingerprint.IRegisterResolver
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method

abstract class BaseIntegrationsPatch(
    private val hooks: Set<IntegrationsFingerprint>,
) : BytecodePatch(hooks) {

    @Deprecated(
        "Use the constructor without the integrationsDescriptor parameter",
        ReplaceWith("BaseIntegrationsPatch(hooks)"),
    )
    @Suppress("UNUSED_PARAMETER")
    constructor(
        integrationsDescriptor: String,
        hooks: Set<IntegrationsFingerprint>,
    ) : this(hooks)

    override fun execute(context: BytecodeContext) {
        if (context.findClass(INTEGRATIONS_CLASS_DESCRIPTOR) == null) {
            throw PatchException(
                "Integrations have not been merged yet. This patch can not succeed without merging the integrations.",
            )
        }

        hooks.forEach { hook ->
            hook.invoke(INTEGRATIONS_CLASS_DESCRIPTOR)
        }
    }

    /**
     * [MethodFingerprint] for integrations.
     *
     * @param contextRegisterResolver A [IRegisterResolver] to get the register.
     * @see MethodFingerprint
     */
    abstract class IntegrationsFingerprint(
        returnType: String? = null,
        accessFlags: Int? = null,
        parameters: Iterable<String>? = null,
        opcodes: Iterable<Opcode?>? = null,
        strings: Iterable<String>? = null,
        customFingerprint: ((methodDef: Method, classDef: ClassDef) -> Boolean)? = null,
        private val insertIndexResolver: ((Method) -> Int) = object : IHookInsertIndexResolver {},
        private val contextRegisterResolver: (Method) -> Int = object : IRegisterResolver {}
    ) : MethodFingerprint(
        returnType,
        accessFlags,
        parameters,
        opcodes,
        strings,
        customFingerprint,
    ) {
        @Deprecated("Previous constructor that is missing the insert index." +
                "Here only for binary compatibility, " +
                "and this can be removed after the next major version update.")
        constructor(
            returnType: String? = null,
            accessFlags: Int? = null,
            parameters: Iterable<String>? = null,
            opcodes: Iterable<Opcode?>? = null,
            strings: Iterable<String>? = null,
            customFingerprint: ((methodDef: Method, classDef: ClassDef) -> Boolean)? = null,
            contextRegisterResolver: (Method) -> Int = object : IRegisterResolver {}
        ) : this(
            returnType,
            accessFlags,
            parameters,
            opcodes,
            strings,
            customFingerprint,
            object : IHookInsertIndexResolver {},
            contextRegisterResolver
        )

        fun invoke(integrationsDescriptor: String) {
            result?.mutableMethod?.let { method ->
                val insertIndex = insertIndexResolver(method)
                val contextRegister = contextRegisterResolver(method)

                method.addInstruction(
                    insertIndex,
                    "invoke-static/range { v$contextRegister .. v$contextRegister }, " +
                        "$integrationsDescriptor->setContext(Landroid/content/Context;)V",
                )
            } ?: throw PatchException("Could not find hook target fingerprint.")
        }

        interface IHookInsertIndexResolver : (Method) -> Int {
            override operator fun invoke(method: Method) = 0
        }

        interface IRegisterResolver : (Method) -> Int {
            override operator fun invoke(method: Method) = method.implementation!!.registerCount - 1
        }
    }

    private companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/shared/Utils;"
    }
}
