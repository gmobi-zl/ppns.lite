# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Programs\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-packageobfuscationdictionary names.txt

-keep public class android.content.pm.* {
    public protected *;
}

-keep public class com.mocean.* {
    public protected *;
}

-keep public class com.mocean.widget.* {
    public protected *;
}

-keepclassmembers public class * extends com.mocean.IService {
    public <init>(...);
}

-keepclassmembers public class * extends com.mocean.IActivity {
    public <init>(...);
}

-keep public class * extends com.mocean.IService