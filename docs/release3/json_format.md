
# 📂 JSON Save Format

The **KollApp** project uses JSON for data persistence, leveraging the Jackson library for JSON processing.

---

## 🗂️ Persistence Structure

Under the persistence directory, we have the following folders for saving relevant information:

- `users`: Stores user login data.
- `groups`: Stores information about available groups.
- `todolists`: Stores the to-do lists for each user.
- `grouptodolists`: Stores the to-do lists for each group.

This structure ensures that all relevant information about the user, the user's to-do lists, and the available groups with their respective to-do lists are organized and easily accessible.

---

### 👤 User Login Data Format

Each user has their own JSON file that includes the following fields:

- **username**: The unique identifier for the user.
- **password**: The user's password for authentication.
- **userGroups**: A list of all the groups the user has access to.

Example for `kollapp/persistence/src/main/java/persistence/users/johnDoe.json`:

```json
{
  "username": "kristin",
  "password": "password123",
  "userGroups": ["Lerkendal studentby rom 33"]
}
```

---

### 📝 User TodoList Data Format

Each user todo-list has its own JSON file that includes the following fields:

- **taskName**: The name of the task.
- **dateTime**: The date and time for the task, specified in the format [YYYY, MM, DD]
- **priority**: The priority level of the task.
- **completed**: A boolean indicating whether the task is completed.

Example for `kollapp/persistence/src/main/java/persistence/todolists/johnDoe.json`:

```json
{
  "tasks": [
    {
      "taskName": "Finish Data Structures and Algorithms Homework",
      "dateTime": [2024, 10, 10],
      "priority": "High Priority",
      "completed": false
    },
    {
      "taskName": "Attend ITP Meeting",
      "dateTime": [2024, 10, 11],
      "priority": "Medium Priority",
      "completed": false
    }
  ]
}
```

---

### 👥 Group Data Format

Each group has its own JSON file that includes the following fields:

- **groupName**: The unique identifier for the group.
- **users**: A list of usernames that are members of the group.

Example for `kollapp/persistence/src/main/java/persistence/groups/bergStudentbyRom102.json`:

```json
{
  "groupName": "Berg studentby rom 102",
  "users": ["oleander", "lars", "emily"]
}
```

Example for `kollapp/persistence/src/main/java/persistence/groups/moholtStudentbyGutta.json`:

```json
{
  "groupName": "Moholt studentby gutta",
  "users": ["henrik", "johan", "michael"]
}
```

Example for `kollapp/persistence/src/main/java/persistence/groups/lerkendalStudentbyRom33.json`:

```json
{
  "groupName": "Lerkendal studentby rom 33",
  "users": ["kristin", "andreas", "sarah"]
}
```

---

## 📋 Group TodoList Data Format

Each group-specific to-do list maintains its own JSON file, which includes the following fields, similar to how an individual user would add a task.

- **taskName**: The name of the task.
- **dateTime**: The date and time for the task, specified in the format [YYYY, MM, DD].
- **priority**: The priority level of the task.
- **completed**: A boolean indicating whether the task is completed.

Example for `kollapp/persistence/src/main/java/persistence/grouptodolists/bergStudentbyRom102.json`:

```json
{
  "tasks": [
    {
      "taskName": "Weekly Cleaning",
      "dateTime": [2024, 10, 11],
      "priority": "High Priority",
      "completed": false
    },
    {
      "taskName": "Grocery Shopping",
      "dateTime": [2024, 10, 12],
      "priority": "Medium Priority",
      "completed": false
    },
    {
      "taskName": "Organize Movie Night",
      "dateTime": [2024, 10, 14],
      "priority": "Low Priority",
      "completed": false
    }
  ]
}
```

---

## 💬 Group Chat Data Format

Each group chat maintains its own JSON file, which includes the following fields:

- **author**: The username of the sender.
- **text**: The content of the message.
- **timestamp**: The date and time when the message was sent, specified in the format [YYYY, MM, DD, HH, mm, ss, nnnnnnnnn]

Example for `kollapp/persistence/src/main/java/persistence/groupchat/bergStudentbyRom102.json`:

```json
{
  "messages": [
    {
      "author": "oleander",
      "text": "Hey everyone, don't forget about the meeting tomorrow!",
      "timestamp": [2024, 11, 3, 15, 16, 11, 982169000]
    },
    {
      "author": "lars",
      "text": "Got it, see you all there.",
      "timestamp": [2024, 11, 3, 15, 21, 11, 982169000]
    }
  ]
}
```

---

## 💰 Shared Expense Data Format

Each shared expense maintains its own JSON file, which includes the following fields:

- **description**: A brief description of the expense.
- **amount**: The total amount of the expense.
- **paidBy**: The username of the user who paid for the expense.
- **participants**: A list of usernames of the users who are sharing the expense.
- **settlements**: A list of settlements indicating wether each participant has settled their share of the expense.

Each settlement includes the following fields:

- **username**: The username of the participant.
- **settled**: A boolean indicating whether the participant has settled their share of the expense.

Example for `kollapp/persistence/src/main/java/persistence/groupexpenses/bergStudentbyRom102.json`:

  ```json
  {
    "description": "Grocery Shopping",
    "amount": 100.0,
    "paidBy": "emily",
    "participants": ["emily", "lars", "oleander"],
    "settlements": [
      {
        "username": "lars",
        "settled": true
      },
      {
        "username": "oleander",
        "settled": false
      }
    ]
  }
  ```

---

📖 Return to the **[Main README](../../readme.md)** for additional information and project overview.
