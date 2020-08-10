/*****************************************************************
 * * File: - Deps
 * * Description:
 * * Version: 1.0
 * * Date : 2020/8/6
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/6    1.0         create
 ******************************************************************/
/*****************************************************************
 * * File: - Deps
 * * Description:
 * * Version: 1.0
 * * Date : 2020/8/6
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/6    1.0         create
 ******************************************************************/
object Deps {
    object BuildPlugins
    object Android {
        val buildToolsVersion = "29.0.3"
        val minSdkVersion = 21
        val targetSdkVersion = 29
        val compileSdkVersion = 29
        val versionCode = 1
        val versionName = "0.1"
    }

    object Libs{
        val appcompt="androidx.appcompat:appcompat:1.1.0"
        val constraintlayout="androidx.constraintlayout:constraintlayout:1.1.3"
    }
    object TestLibs
}

