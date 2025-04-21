## Application Users

Based on the role/permissions, a registered user can fall under one of the following categories:

- **Individual User**:  
  Can view all families and members.  
  Can view events, news, accounts, etc.  
  Can update their own profile.

- **Family Admin**:  
  Includes all permissions of an Individual User.  
  Can update family details.  
  Can add/update profiles of any individual in the family.

- **App Admin**:  
  Includes all permissions of a Family Admin.  
  Can add/update any family.

- **Super Admin**:  
  Has full access and can perform any action in the application.

Each user belongs to exactly one family.  
A user (person) might be displayed in two families: one with their spouse's family and one with their own family.


## User registration

First super user will be added manually in database with pre-define password.


User profile will have following attributes:
logonName: System generated logon name like firstname.lastname or user can select their own while registration.

User Profile should support nick name


## Authentication and Authorization
