# Family Tree App
Family Tree App


## Build Application:
### Change dir to source
cd source

Run local server
./gradlew runApp

Build with UX for distribution
./gradlew clean buildDist -PbuildReactApp


## Date Handling
For date without timezone, always use LocalDate class in Java.
When sending this date to UX, format the date to String in ISO 8601 format i.e. yyyy-MM-dd
i.e. Response Model should be of type string.

on UX, use date formatter to format the date to dd-MMM-yyyy format.
While submitting the date from UX, always convert the date to ISO format i.e. yyyy-MM-dd.
On backend, convert this input date string to Local Date.
