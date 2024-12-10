plugins {
    // Plugins necessários para o projeto Android e Kotlin
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.apinoticias"  // Define o namespace do app
    compileSdk = 35  // Define a versão do SDK com a qual a app vai ser compilado

    defaultConfig {
        applicationId = "com.example.apinoticias"  // ID único do app
        minSdk = 24  // A versão mínima do SDK que o app vai suportar
        targetSdk = 35  // A versão do SDK para a qual o app foi otimizado
        versionCode = 1  // Versão interna do app
        versionName = "1.0"  // Versão pública do app

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"  // Configuração para rodar testes
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false  // Não estamos minificando o código para a versão de release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        kotlinCompilerExtensionVersion = "1.5.1"  
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"  // Exclui certas licenças nos pacotes
        }
    }
}

dependencies {
    // Bibliotecas AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

    // Jetpack Compose para UI
    implementation("androidx.compose.ui:ui:1.5.1")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.1")

    // Retrofit para fazer requisições à API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Accompanist para adicionar recursos como refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1")

    // Dependências para testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.1")

    // Coil para carregar imagens (usado no Compose)
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Para integração do ViewModel com Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Material Components para o tema e widgets
    implementation("com.google.android.material:material:1.9.0")
}
