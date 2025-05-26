## Family Member

Represents an individual within a family tree. This includes all relatives and ancestors, regardless of whether they use the app.

## Family Member Association

Each family member is linked to exactly one primary family.  
However, a member may appear in multiple family trees—typically in both their **birth family** and their **spouse's family**—to reflect real-life relationships and connections.

---

## Family Member Profile Attributes

Below are the key attributes captured for each family member:

- **Member ID**:  
  A system-generated unique identifier assigned at creation. This ID is permanent and cannot be changed.

- **First Name**:  
  The individual's given name.

- **Last Name**:  
  The family name shared across members of the same family.

- **Nick Name**:  
  An informal or commonly used alternate name.

- **Family Address**:  
  The address associated with the entire family.

- **Member Address**:  
  The specific address of the member.  
  *A flag determines whether this address is the same as the family address or a separate one.*

- **Phone Number**:  
  Contact number of the member.  
  *Display a WhatsApp icon if the number is WhatsApp-enabled.*

- **Email ID**:  
  The member’s email address for communication purposes.

- **LinkedIn URL**:  
  Link to the member's LinkedIn profile (if applicable).

- **Family ID**:  
  A reference to the family this member is registered under.

- **Sex**:  
  Male / Female

- **Date of Birth**:  
  Includes month and year (day is optional).

- **Marital Status**:  
  One of the following options:  
  `Single`, `Married`, `Divorced`, `Widowed`, `Separated`, `Engaged`

- **Education Details**:  
  A list of academic qualifications.  
  *Examples: B.E. Engineer, MBBS, MD*

- **Occupation**:  
  The member's profession or role.  
  *Examples: Software Engineer, Orthopedic Surgeon, Business Owner (flour mill), Fast Food Restaurant Owner*

- **Working At**:  
  Name of the organization, company, or place of business.

- **Hobby**:  
  Member's hobbies or recreational interests.  
  *Example: Volleyball, cooking, painting*

- **Profile Image**:  
  Upload a single profile picture for the member. The application automatically generates a thumbnail (100*100 pixels) and a larger version (600 × 600 pixels) to optimize performance.


---

### Multilingual Name Display

- **First Name**, **Last Name**, and **Nick Name** should be captured and displayed in both **English** and **Hindi**.

---

## Family Member Registration

### Permissions

Users with the following roles/permission can add, update, or delete family members:

- **Application Admin**
- **Family Admin**

---

### Registration Workflow

#### Step 1: Log In and Select Family

1. Log in to the application using valid credentials.
2. Navigate to the **Family Search** screen.
3. Search for and select the target family.

---

#### Step 2: Add the First Family Member (Head of Family)

If no members exist in the selected family:

1. The user is prompted to register the **Head of the Family**.
2. Fill out the family member’s profile information on the form.
3. Enable the **Head of Family** flag for this member.
4. Submit the form to add the first member to the family.

> **Note**: Only one member can be designated as the head of a family.

---

#### Step 3: Add Additional Family Members

If the family already has existing members:

1. The system displays the **Family Tree** for the selected family.
2. Click **"Add New Member"**.
3. In the registration dialog:
   - Select an existing family member from the dropdown list.
   - Choose the **relationship type** of the new member relative to the selected family member:
     - `Father`
     - `Mother`
     - `Husband`
     - `Wife`
     - `Son`
     - `Daughter`
     - `Brother`
     - `Sister`
4. Fill in the new member’s profile details.
5. Submit the form to add the member to the family tree.

---

### Summary

- The **Head of the Family** must be added first if no members exist.
- Additional members are added based on their **relationship** to an existing member.
- Each member must have a complete profile to be registered successfully.
