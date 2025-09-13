# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep launcher-specific classes
-keep class org.fossify.home.** { *; }

# Keep ServiceLocator and core components
-keep class org.fossify.home.core.ServiceLocator { *; }
-keep class org.fossify.home.core.DeviceCapabilities { *; }

# Keep database entities and DAOs
-keep class org.fossify.home.models.** { *; }
-keep class org.fossify.home.interfaces.**Dao { *; }
-keep class org.fossify.home.databases.AppsDatabase { *; }

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep ViewBinding classes
-keep class * extends androidx.viewbinding.ViewBinding {
    public static *** inflate(android.view.LayoutInflater);
    public static *** inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
    public static *** bind(android.view.View);
}

# Keep custom views
-keep class org.fossify.home.views.** { *; }

# Keep gesture and touch handling classes
-keep class org.fossify.home.gestures.** { *; }
-keep class org.fossify.home.touch.** { *; }

# Keep fragment and menu management classes
-keep class org.fossify.home.fragments.** { *; }
-keep class org.fossify.home.menu.** { *; }

# Keep utility classes
-keep class org.fossify.home.utils.** { *; }

# Keep theme and icon handling
-keep class org.fossify.home.theme.** { *; }
-keep class org.fossify.home.icons.** { *; }

# Keep search and prediction classes
-keep class org.fossify.home.search.** { *; }
-keep class org.fossify.home.predict.** { *; }

# Keep notification and badge handling
-keep class org.fossify.home.notifications.** { *; }

# Keep backup and restore functionality
-keep class org.fossify.home.backup.** { *; }

# Keep effects and transitions
-keep class org.fossify.home.effects.** { *; }

# Keep sort and app management
-keep class org.fossify.home.sort.** { *; }

# Keep receivers and services
-keep class org.fossify.home.receivers.** { *; }
-keep class org.fossify.home.services.** { *; }

# Keep adapters
-keep class org.fossify.home.adapters.** { *; }

# Keep dialogs
-keep class org.fossify.home.dialogs.** { *; }

# Keep extensions
-keep class org.fossify.home.extensions.** { *; }

# Keep interfaces
-keep interface org.fossify.home.interfaces.** { *; }

# Keep helpers
-keep class org.fossify.home.helpers.** { *; }

# Keep data classes
-keep class org.fossify.home.data.** { *; }

# Keep activities
-keep class org.fossify.home.activities.** { *; }

# Keep Fossify Commons classes
-keep class org.fossify.commons.** { *; }

# Keep launcher-specific permissions and intents
-keep class android.content.pm.LauncherApps { *; }
-keep class android.appwidget.AppWidgetManager { *; }
-keep class android.appwidget.AppWidgetHost { *; }

# Keep notification listener service
-keep class * extends android.service.notification.NotificationListenerService {
    public *;
}

# Keep device admin receiver
-keep class * extends android.app.admin.DeviceAdminReceiver {
    public *;
}

# Keep broadcast receivers
-keep class * extends android.content.BroadcastReceiver {
    public *;
}

# Keep parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom attributes
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove our custom logger in release builds
-assumenosideeffects class org.fossify.home.utils.Logger {
    public static void d(...);
    public static void i(...);
    public static void w(...);
    public static void e(...);
    public static void v(...);
}

# Optimize code
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Keep line numbers for debugging (remove in production)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
