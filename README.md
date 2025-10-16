# Family Tree App
Family Tree App


## Build Application:
### Change dir to source
cd source

Run local server
```
./gradlew runApp
```

Build with UX for distribution
```
./gradlew clean buildDist -PbuildReactApp
```

## Date Handling
For date without timezone, always use LocalDate class in Java.
When sending this date to UX, format the date to String in ISO 8601 format i.e. yyyy-MM-dd
i.e. Response Model should be of type string.

on UX, use date formatter to format the date to dd-MMM-yyyy format.
While submitting the date from UX, always convert the date to ISO format i.e. yyyy-MM-dd.
On backend, convert this input date string to Local Date.


## Task 

Last number is 42:

### Pending Tasks
| Issue | Task Desc             |   Priority   |POC   | Date Reported | Start Date | End Date | Effort | Status |
|---|------------------ |---------------|-------|--|--|--|--|--|
|I-16|Add family member- in sequence  |      Low   |  Vijay| 29-Sep-2025 |29-Sep-2025 | | 1 day | Not Started |
|I-15|Add family - only admin- show add admin only if user has permission  |      High   |  Vijay| 03-Oct-2025 | | 1 day | WIP |
|I-37|Disable cross origin request on server |      High   |  Vijay| 12-Oct-2025 | | 1 day | WIP |
|I-34| Add family for Ramnarayan Rajput with members |      High   |  Vijay| 11-Oct-2025 | | 1 hour | WIP |
|I-35| Add family for Lachoriya (Megha family) |      High   |  Vijay| 11-Oct-2025 | | 1 hour | WIP |
|I-39| Add screen/modal for terms and condition and privacy link on SignUp page and footer |      High   |  Vijay| 12-Oct-2025 | | | 1 day | Not Started |
|I-33|Create thumbnail image while updating profile image |      High   |  Vijay| 11-Oct-2025 | | 1 hour | WIP |
|I-19|Change Table background color to little light color |      High   |  Vijay| 29-Sep-2025 |29-Sep-2025 | | 1 hour | Not Started |
|I-6|Migrate to Oracle Cloud |      High   |  Vijay| 29-Sep-2025 |29-Sep-2025 | | 1 hour | Not Started |
|I-9|App should support only TLS 1.3 |      Low   |  Vijay| 29-Sep-2025 |29-Sep-2025 | | 1 day | Not Started |
|I-10|CSRF - CSRF should be blocked |      Low   |  Vijay| 29-Sep-2025 |29-Sep-2025 | | 1 day | Not Started |
|I-11|Add social media link like YouTube, FB, etc. |      Low   |  Vijay| 29-Sep-2025 |29-Sep-2025 | | 1 day | Not Started |
|I-12|Create term and condition/ privacy content from lawyer  |      Low   |  Vijay| 29-Sep-2025 |29-Sep-2025 | | 1 day | Not Started |
|I-13|Add about us page  |      Low   |  Vijay| 29-Sep-2025 |29-Sep-2025 | | 1 day | Not Started |
|I-17|On profile page link to paternal/maternal family  |      Low   |  Vijay| 29-Sep-2025 |29-Sep-2025 | | 1 day | Not Started |
|I-21|When user does not have permission to update member image and try to update member image, screen blank out and shows only error message and whole page is gone.  |      Low   |  Vijay| 05-Oct-2025 | | | 1 day | Not Started |
|I-22|If user try to upload image other than jpg/npg screen shows uncaught error. E.g. try uploading HEIC image  |      Low   |  Vijay| 05-Oct-2025 | | | 1 day | Not Started |
|I-25|Create thumbname for member image to best fit in family tree |      High   |  Vijay| 05-Oct-2025 | | | 1 day | Not Started |
|I-27|Update appUser firstname and last name on updating member profile |      High   |  Vijay| 11-Oct-2025 | | | 1 day | Not Started |


### Completed Tasks
| Issue | Task Desc             |   Priority   |POC   | Date Reported | Start Date | End Date | Effort | Status |
|---|------------------ |---------------|-------|--|--|--|--|--|
|I-1 |Create New GMail account without numbers     |      High   |  Vijay| 29-Sep-2025 |02-Oct-2025 | 02-Oct-2025 | 1 hour | Done |
|I-2 |Create New App password for Gmail account     |      High   |  Vijay| 29-Sep-2025 |02-Oct-2025 | 02-Oct-2025 | 1 hour | Done |
|I-3 |Update App password and Email id in application |      High   |  Vijay| 29-Sep-2025 |02-Oct-2025 | 02-Oct-2025 | 1 hour | Done |
|I-5|Delete old Gmail account |      High   |  Vijay| 02-Oct-2025 | 29-Sep-2025 |02-Oct-2025 | 1 hour | Done |
|I-8|Update Personal profile to show parents, spouse, children and siblings (Clean-Up) |      High   |  Vijay| 29-Sep-2025 |29-Oct-2025 | 03-Oct-2025 | 1 day | Done |
|I-20|Add family - Family search string is not updated |      High   |  Vijay| 29-Sep-2025 |04-Oct-2025 | 04-Oct-2025 | 1 day | Done |
|I-21|If Family does not have head of family, family does not show up in search family |      High   |  Vijay| 29-Sep-2025 |04-Oct-2025 | 04-Oct-2025 | 1 day | Done |
|I-7|Merge UX changes |      High   |  Vijay| 29-Sep-2025 |04-Oct-2025 | 04-Oct-2025 | 1 hour | Done |
|I-18| Update user name to email address |      Low   |  Vijay| 29-Sep-2025 |04-Oct-2025 | 04-Oct-2025 | 1 day | Done |
|I-26|Change wedding date, death date to Date and remove timezone. When saved from UX, date move back by one day. This also required update to member history table. |      High   |  Vijay| 08-Oct-2025 | 11-Oct-2025 | 11-Oct-2025| 1 day | Done |
|I-14|Update member functionality   |      Low   |  Vijay| 29-Sep-2025 |02-Oct-2025 | 08-Oct-2025| 1 day | Done |
|I-23|On Image upload model, modal heading is not right  |      High   |  Vijay| 05-Oct-2025 | 11-Oct-2025| 11-Oct-2025| 1 day | Done |
|I-24|Finalize the image dimention for family and member  |      High   |  Vijay| 05-Oct-2025 | 11-Oct-2025| 11-Oct-2025| 1 day | Done |
|I-32|Login page - change user name to email id |      High   |  Vijay| 11-Oct-2025 | 12-Oct-2025| 12-Oct-2025| 1 hour | Done |
|I-31|Change login page image. Should be light and clean |      High   |  Vijay| 11-Oct-2025 | 12-Oct-2025 | 12-Oct-2025| 1 hour | Done |
|I-38|Add 2 check box on signup page. See detail on screen shot |      High   |  Vijay| 12-Oct-2025 | 12-Oct-2025 |12-Oct-2025 | 1 hour | Done |
|I-28|Comment Ads on main page.  |      High   |  Vijay| 11-Oct-2025 | 12-Oct-2025 | 12-Oct-2025| 1 hour | Done |
|I-29|Click on profile on profile page loads same page. And does not show the page from top. Scroll the page on the top when profile is loaded  |      High   |  Vijay| 11-Oct-2025 | 12-Oct-2025 | 12-Oct-2025 | 1 day | Done |
|I-36|Member profile shows wrong family address. It should be the family address. |      High   |  Vijay| 12-Oct-2025 | 12-Oct-2025 | 12-Oct-2025| 1 day | Done |
|I-30|Cleanup current data base. Should not have any registered users. Check all the profiles. Check all the families |      High   |  Vijay| 11-Oct-2025 | 12-Oct-2025| 12-Oct-2025 | 1 hour | Done |
|I-4|Create Constant for contact email id in UX. Use same id in help doc as well |High   |  Vijay| 29-Sep-2025 |14-Oct-2025 | 14-Oct-2025| 1 hour | Done |
|I-40| Update FAQ to have vasic questions like, how to register, reset password, add family, add member |      High   |  Vijay| 12-Oct-2025 | 14-Oct-2025| 14-Oct-2025| 1 day | Done |
|I-41|Super Admin should see edit button for all family and members  |      High   |  Vijay| 14-Oct-2025 |14-Oct-2025 | 14-Oct-2025| 1 day | Done |
|I-42|If there is no member in family, show message as no members instead of empty tree/table.  |      Low   |  Vijay| 14-Oct-2025 |14-Oct-2025 | 14-Oct-2025| 1 day | Done |



## Feedback
|Feedback             |   provided by   |Status   | Date | Notes | Issue Linked |
|------------------ |---------------|-------|--|--|--|
|Show family daughter in tree even after marriage. Show external member with different color     |      Megha   |  Not Started | 20-Sep-2025 | Analyzed and ready to implement.| |