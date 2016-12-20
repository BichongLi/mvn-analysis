This tool is targeted for analyze dependency management for projects, depended on the output result of maven dependency plugin.
This is a simple command line tool, using Java 8 and Maven. You can simply run "mvn clean install" to package an executable jar.
Command input:
java -jar target\mvn-analysis-1.0-SNAPSHOT.jar [mode] [args]
mode: dependencyAnalyze, dependencyTree, dependencyList, treeCompare, commandRun

args for dependencyAnalyze:
 
 -p,--Project pom.xml path <arg> [required]
 
 -o,--Output target folder <arg>
 
 -jh,--Java home <arg>
 
 -mh,--Maven home <arg>
 
 -h,--help

args for dependencyTree:
 
 -p,--Project pom.xml path <arg> [required]
 
 -o,--Output tree structure file <arg> [required]
 
 -t,--Output file type <arg> [xml(default)/json]
 
 -b,--Base dependency set file <arg>
 
 -jh,--Java home <arg>
 
 -mh,--Maven home <arg>
 
 -h,--help


args for dependencyList:
 
 -p,--Project pom.xml path <arg> [required]
 
 -o,--Output file <arg> [required]
 
 -jh,--Java home <arg>
 
 -mh,--Maven home <arg>
 
 -h,--help

args for treeCompare:
 
 -l,--Left tree xml file to compare. <arg> [required]
 
 -r,--Right tree xml file to compare. <arg> [required]
 
 -o,--Output txt file <arg>
 
 -h,--help

args for commandRun:
 
 -p,--Project pom.xml path <arg> [required]
 
 -c,--Maven command <arg> [required]
 
 -jh,--Java home <arg>
 
 -mh,--Maven home <arg>
 
 -h,--help