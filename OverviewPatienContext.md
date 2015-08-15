#Introduction to why a the patient in needed.

# Introduction #

The patient context is a combination of a simple search interface and an portlet-event based message service. Though the portlet can be placed on multiple pages in the portal, each instance share a common context.

# The idea #

<img src='http://oppna-program-healthcare-context.googlecode.com/svn/wiki/P1070526.png' width='400' /><br />

The patient-context is a little like a bunch of telepathic siblings.

The happy people are the patient-context portlets. They each live on their own page, on that page each patient-context direct what other portlets should do.
In the portal the patient-context's are share in one common mind.

# The reason why #

When you have many healthcare systems you want to make it easy to navigate between these systems, and pick up information about the patient at hand.

At the same time it is **very** important not to have any mixups. You should never risk while looking at a referral for one patient, flip a page and get a list of medications for the previous patient.

We need a mechanism that at the same time makes things simple and natural and at the same time prevents misstakes.

# The how #

<img src='http://oppna-program-healthcare-context.googlecode.com/svn/wiki/ScreenShot3.png' /><br />

## Patient Event ##
The patient-context keeps track of a rather frugal set of data. The current active patient-identifier stored in a Patient Event object and a history list of patient searches (Patient Event's) made in the current session.

## Patient identifier types ##
When a search are entered in the search window the text are matched against a predefined list of patient identifier types:
  * Person Number
  * Reserv Number (SäS, SU, NU)
  * Samordnings Number
  * ...

The reason why we need rich patient-identifier types is that a number can provide quite a bit extra information. If you enter a Person Number incorrectly you could end up with the information for a woman even though you sit in front of a man. These extra cues can be displayed in both the search and the listener portlets.

If the text can be interpreted as one of these types, an object of that type will be created. If several of the types can match the input text a choice dialog will appear.

If the input text cannot be match too any of the identifier types, it can be used as is, without being typed in any way.

## Patient identifier validation ##
The validation for a patient-identifier type should be quite liberal.

For instance the Person Number validator accept invalid months, invalid days and invalid control numbers. Person Numbers are used for search so it has to allow for errors entered in external systems, and as long as the entered text obeys some kind of regularity it can be used as if it obeyed all the rules.

More person-identifier-types can be added as the need arise.

## Portlet events ##
When a search are issued a Patient Event are created. This object are packaged in a portlet-event and sent for consumption of other portlets on the page.

It is up to the listener portlet to actually perform the search and retrieve and display the information, the patient-context portlet just announce that something **ought** to be done, **not what** has to be done.

Finally, when a Patient Event search request are issued, an Patient Context changed event are issued. This event are to tell all the other patient-context instances on the other pages that something has changed.

The Patient Context changed event serves two purposes:
  1. Current patient (and history) has changed.
  1. Invalidate the old patient information display.

<img src='http://oppna-program-healthcare-context.googlecode.com/svn/wiki/P1070529.png' width='400' /><br />

# Integrations #

The patient-context also represents a sweet spot for integrations.

I.e. if we want to log who has accessed information on what patient in which healthcare application - the patient-context is an obvious place. This can be accomplished  with either an independent Logging listener-portlet or as an full-blown integrated in the patient-context.

Another obvious integration could be context synchronisation with desktop systems like Journal3, and when it is in place NPÖ.

If the patient-context is used to synchronise everything inside the portal, there will only need to be **one** integration made, not one integration per healthcare application in the portal.

<img src='http://oppna-program-healthcare-context.googlecode.com/svn/wiki/P1070530.jpg' width='400' /><br />

## Access Logging ##

Utility service for access logging is available.

Access information is acquired from the request and from Ldap, patient identifier and additional audit properties are provided by the client using the service.

Portlets using this service:
  * BRF
  * PLI
  * Infobroker
  * PafWeb Viewer