/*****************************************************************
 * * File: - PluginBuildSrc
 * * Description:
 * * Version: 1.0
 * * Date : 2020/8/6
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/6    1.0         create
 ******************************************************************/
package com.example.plugin

/*****************************************************************
 * * File: - PluginBuildSrc
 * * Description:
 * * Version: 1.0
 * * Date : 2020/8/6
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/6    1.0         create
 ******************************************************************/
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginBuildSrc : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("hello") {
            doLast {
                println("Hello from the GreetingPlugin")
            }
        }
    }
}

