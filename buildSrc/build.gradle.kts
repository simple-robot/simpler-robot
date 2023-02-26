plugins {
    `kotlin-dsl`
    idea
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}

val kotlinVersion = "1.8.0"
val dokkaPluginVersion = "1.7.20"
val suspendTransformVersion = "0.2.2"
val gradleCommon = "0.0.11"

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaPluginVersion")
    implementation("org.jetbrains.dokka:dokka-base:$dokkaPluginVersion")

    // see https://github.com/gradle-nexus/publish-plugin
    implementation("io.github.gradle-nexus:publish-plugin:1.2.0")
    implementation("love.forte.plugin.suspend-transform:suspend-transform-plugin-gradle:$suspendTransformVersion")

    implementation("love.forte.gradle.common:gradle-common-core:$gradleCommon")
    implementation("love.forte.gradle.common:gradle-common-kotlin-multiplatform:$gradleCommon")
    implementation("love.forte.gradle.common:gradle-common-publication:$gradleCommon")
}

idea {
    module {
        isDownloadSources = true
    }
}

/*
java.lang.NoSuchMethodError: 'boolean org.jetbrains.kotlin.backend.common.ir.IrUtilsKt.isStatic(org.jetbrains.kotlin.ir.declarations.IrFunction)'
    at love.forte.plugin.suspendtrans.utils.IrFunctionUtilsKt.paramsAndReceiversAsParamsList(IrFunctionUtils.kt:136)
    at love.forte.plugin.suspendtrans.utils.IrFunctionUtilsKt.createSuspendLambdaWithCoroutineScope(IrFunctionUtils.kt:72)
    at love.forte.plugin.suspendtrans.ir.SuspendTransformTransformerKt.generateTransformBodyForFunction(SuspendTransformTransformer.kt:199)
    at love.forte.plugin.suspendtrans.ir.SuspendTransformTransformerKt.access$generateTransformBodyForFunction(SuspendTransformTransformer.kt:1)
    at love.forte.plugin.suspendtrans.ir.SuspendTransformTransformer.resolveFunctionBody(SuspendTransformTransformer.kt:172)
    at love.forte.plugin.suspendtrans.ir.SuspendTransformTransformer.resolveFunctionBodyByDescriptor(SuspendTransformTransformer.kt:84)
    at love.forte.plugin.suspendtrans.ir.SuspendTransformTransformer.visitFunctionNew(SuspendTransformTransformer.kt:68)
 */
