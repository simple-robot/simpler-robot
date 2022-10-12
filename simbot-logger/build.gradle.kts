import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
}

kotlin {
    explicitApi()
    
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                javaParameters = true
            }
        }
        withJava()
        testRuns.all {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    
    js(IR) {
        browser()
        nodejs()
    }
    
    configureAllNativePlatforms()
    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        getByName("jvmMain") {
            dependencies {
                api(libs.slf4j.api)
            }
        }
        val nativeCommonMain = create("nativeCommonMain") {
            dependsOn(commonMain)
        }
        val nativeCommonTest = create("nativeCommonTest") {
            dependsOn(commonTest)
        }
        val notInNative = setOf(
            "commonMain", "commonTest",
            "jvmMain", "jvmTest",
            "jsMain", "jsTest",
            "nativeCommonMain", "nativeCommonTest",
        )
        
        names.forEach { n ->
            if (n !in notInNative) {
                getByName(n) {
                    when {
                        n.endsWith("Main") -> dependsOn(nativeCommonMain)
                        n.endsWith("Test") -> dependsOn(nativeCommonTest)
                    }
                }
            }
        }
    }
    
    
}

fun org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithPresetFunctions.configureAllNativePlatforms() {
    presets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset<*>> {
        configureOrCreate(this)
    }
    // iosArm32()
    // iosArm64 {
    // }
    // iosX64 {
    // }
    // iosSimulatorArm64 {
    // }
    // watchosArm32 {
    // }
    // watchosArm64 {
    // }
    // watchosX86 {
    // }
    // watchosX64 {
    // }
    // watchosSimulatorArm64 {
    // }
    // tvosArm64 {
    // }
    // tvosX64 {
    // }
    // tvosSimulatorArm64 {
    // }
    // linuxX64 {
    // }
    // mingwX86 {
    // }
    // mingwX64 {
    // }
    // macosX64 {
    // }
    // macosArm64 {
    // }
    // linuxArm64 {
    // }
    // linuxArm32Hfp {
    // }
    // linuxMips32 {
    // }
    // linuxMipsel32 {
    // }
    // wasm32 {
    // }
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinTargetsContainerWithPresets.configureOrCreate(
    targetPreset: org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset<*>,
    configure: (KotlinNativeTarget.() -> Unit)? = null,
): KotlinNativeTarget {
    val targetName = targetPreset.name.targetName()
    val existingTarget = targets.findByName(targetName) as? KotlinNativeTarget
    when {
        existingTarget?.isProducedFromPreset(targetPreset) ?: false -> {
            existingTarget as KotlinNativeTarget
            configure?.invoke(existingTarget)
            return existingTarget
        }
        
        existingTarget == null -> {
            val newTarget = targetPreset.createTarget(targetName)
            targets.add(newTarget)
            configure?.invoke(newTarget)
            return newTarget
        }
        
        else -> {
            throw InvalidUserCodeException(
                "The target '$targetName' already exists, but it was not created with the '${targetPreset.name}' preset. " +
                        "To configure it, access it by name in `kotlin.targets`" +
                        (" or use the preset function '${existingTarget.preset?.name}'."
                            .takeIf { existingTarget.preset != null } ?: ".")
            )
        }
    }
}

fun String.targetName(): String = buildString {
    val iter = this@targetName.iterator()
    while (iter.hasNext()) {
        val next = iter.nextChar()
        if (next == '_' && iter.hasNext()) {
            append(iter.nextChar().toUpperCase())
        } else {
            append(next)
        }
        
    }
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinTarget.isProducedFromPreset(kotlinTargetPreset: org.jetbrains.kotlin.gradle.plugin.KotlinTargetPreset<*>): Boolean =
    preset == kotlinTargetPreset