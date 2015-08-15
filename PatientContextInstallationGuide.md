#Guide for installing and testing the Patient context portlet, making it available for other systems.

# Patient context #
## Introduction ##

The patient context parse and interprets user input, making it available to other systems/portlets through a JSR-286 portlet event.

## Event class deployment ##
The event object has to be deployed separately from the portlet, and it has to be on the classpath of the application server.
Every portlet has it's own class-loader and if the event-class where to be deployed along the portlets depending on it, they would not be able to use it.

Tomcat:
  * The event objects are packaged separately in the maven build `patient-contxt/composites/webcomp`
  * Copy `healthcare-context-patient-context-composite-webcomp-*.*.jar` to `<tomcat_home>/lib/ext`
  * Verify that `commons-lang-2.4.jar` is in `<tomcat_home>/lib/ext`

## Portlet deployment ##
Patient context is a normal portlet. However, the event class dependency is marked as provided in the maven build, so it will not be packaged into the war file (that is why we copied it to `<tomcat_home>/lib/ext` in the description above).
Along side the Patient context portlet, a test listener client portlet will be deployed. This makes it easy to verify that the portlet to portlet communication is working.

Liferay:
  * Navigate to the portlet `patient-context/modules/portlet`
  * Copy `healthcare-context-patient-context-module-portlet.war` to `<liferay_home>/deploy`

## Verification ##
  * Add the "Patient context" and the "Patient context listener" portlet to the same page.
  * Write something in the search box.
  * Verify that the listener portlet picks up the message.