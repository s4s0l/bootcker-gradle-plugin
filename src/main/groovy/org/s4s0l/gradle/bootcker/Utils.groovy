package org.s4s0l.gradle.bootcker

/**
 * @author Matcin Wielgus
 */
class Utils {
    static String friendlyProjectName(String name){

        return name.replaceAll("[^0-9a-zA-Z]", " ")
                .toLowerCase().trim().replaceAll("[^0-9a-zA-Z]", ".");
    }
}
