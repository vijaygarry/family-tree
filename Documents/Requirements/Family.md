## Family

Represents a single family unit, comprising a group of family members. This entity may also include pets as part of the family structure.

## Family Attributes

- **Family ID**:  
  System-generated unique identifier.

- **Family Name**:  
  Last name of the family (e.g., *Garothaya*).

- **Gotra**:  
  Family’s gotra.

- **Head of Family**:  
  Full name of the head of the family (e.g., *Bhagwatnarayan Garothaya*).

- **Address**:  
  Physical address of the family.
  - **Region**:
    The region where the family resides. This will be system-derived based on the address using the following rules:
    - For families in **India**:  
      `Region = City + State` (e.g., *Amravati, MH*)
    - For families **abroad**:  
      `Region = Country` (e.g., *USA*)

- **Phone**:  
  Contact number for the family.  
  *Show a WhatsApp icon if the number is WhatsApp-enabled.*

- **Email**:  
  Contact email for the family.

- **Family Display Name**:  
  Automatically derived by the app as:  
  `[Head of Family Name] + [Region]`  
  **Example**: *Bhagwatnarayan Garothaya – Amravati, MH*

- **Family photo**:
  Family photo (600 * 1200 pixels)
  
- **Pets**:
  List pets in the family with image

## Audit Attributes (for Family Changes)

- **Created By**:  
  User ID who created the family entry.

- **Created At**:  
  Timestamp when the family was created.

- **Last Updated By**:  
  User ID who last modified the family details.

- **Last Updated At**:  
  Timestamp of the latest update.

- **Change Log**:  
  Maintain family change history in FamilyHistory table.


## Family Registration
Only the **Super Admin** can register new families. This feature may later be opened to **App Admins** as adoption increases.

The first family will be added through the backend.
Every family member must belong to a family.