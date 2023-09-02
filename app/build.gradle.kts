plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.camerongineer.tipcalculatorwear"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.camerongineer.tipcalculatorwear"
        minSdk = 28
        targetSdk = 33
        versionCode = 17
        versionName = "1.4.1"
        vectorDrawables {
            useSupportLibrary = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            proguardFiles("baseline-profiles-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"
}

dependencies {

    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.google.android.gms:play-services-wearable:18.0.0")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.wear.compose:compose-navigation:1.2.0")
    implementation("androidx.wear.compose:compose-material:1.2.0")
    implementation("androidx.wear.compose:compose-foundation:1.2.0")
    implementation("androidx.wear:wear-remote-interactions:1.0.0")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.wear.tiles:tiles:1.2.0")
    implementation("androidx.wear.tiles:tiles-material:1.2.0")
    implementation("com.google.android.horologist:horologist-compose-tools:0.5.3")
    implementation("com.google.android.horologist:horologist-tiles:0.5.3")
    implementation("androidx.compose.material:material-icons-extended:1.5.0")

    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Testing dependencies
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("junit:junit:4.13.2")

    // Compose testing dependencies
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    testImplementation("org.mockito:mockito-core:3.11.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}