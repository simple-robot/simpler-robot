import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

val kotlinVersion = "1.7.20"
val dokkaPluginVersion = "1.7.20"
val suspendTransformVersion = "0.0.5"
val gradleCommon = "0.0.11"

dependencies {
    // kotlin("jvm") apply false
   implementation(gradleApi())
   implementation(kotlin("gradle-plugin", kotlinVersion))
   implementation(kotlin("serialization", kotlinVersion))
   implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaPluginVersion")
    
    // see https://github.com/gradle-nexus/publish-plugin
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
    implementation("love.forte.plugin.suspend-transform:suspend-transform-plugin-gradle:$suspendTransformVersion")
    
    implementation("love.forte.gradle.common:gradle-common-core:$gradleCommon")
    implementation("love.forte.gradle.common:gradle-common-kotlin-multiplatform:$gradleCommon")
    implementation("love.forte.gradle.common:gradle-common-publication:$gradleCommon")
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
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