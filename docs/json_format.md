
## **📂 File Format for User Data and Settings**

The **KollApp** project uses JSON for data persistence, leveraging the Jackson library for JSON processing. The file format for user data and settings is defined as follows:


### **User Login Data**

The user login data format includes the following fields:

- **username**: The unique identifier for the user.
- **password**: The user's password for authentication.
- **userGroups**: A list of all the groups the user is connected to.

Example for `kollapp/persistence/src/main/java/persistence/users/johnDoe.json`:


```json
{
  "username": "johnDoe",
  "password": "password123",
  "userGroups": ["exampleGroup"]
}
```

### **User Data Format for TodoList within a group**

The user data format for a todo-list within a group includes the following fields:

- **index**: The unique identifier for the task.
- **taskName**: The name of the task.
- **dateTime**: The date and time for the task, specified in the format [YYYY, MM, DD]
- **priority**: The priority level of the task.
- **completed**: A boolean indicating whether the task is completed.

Example:

```json
{
  "tasks": [
    {
      "index": 0,
      "taskName": "Grocery Shopping",
      "dateTime": [2024, 10, 11],
      "description": "Buy shared groceries for the week, including fruits, vegetables, and dairy products.",
      "priority": "High Priority",
      "completed": false
    },
    {
      "index": 1,
      "taskName": "Clean the Kitchen",
      "dateTime": [2024, 10, 12],
      "description": "Deep clean the kitchen, including the stove, countertops, and sink.",
      "priority": "Medium Priority",
      "completed": false
    },
  ]
}
```

## Persistence Structure

Under the persistence directory, we have the following folders for saving relevant information:

- users: Stores user login data.
- groups: Stores information about available groups.
- todolists: Stores the to-do lists for each user.

This structure ensures that all relevant information about the user, the user's to-do lists, and the available groups are organized and easily accessible.

---

📖 Return to the **[Main README](../readme.md)** for additional information and project overview.